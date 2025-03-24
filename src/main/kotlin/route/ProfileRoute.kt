package com.example.plugins.route

import com.example.plugins.model.ProfileResponse
import com.example.plugins.model.UpdateUserParams
import com.example.plugins.repository.profile.ProfileRepository
import com.example.plugins.util.Constants
import com.example.plugins.util.getLongParameter
import com.example.plugins.util.saveFile
import io.ktor.http.*
import io.ktor.http.content.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.koin.ktor.ext.inject
import java.io.File

fun Routing.profileRouting () {
    val repository by inject<ProfileRepository>()

    authenticate {
        route(path = "/profile") {
            get(path = "/{userId}") {
                try {
                    val profileOwnerId = call.getLongParameter(name = "userId")
                    val currentUserId = call.getLongParameter(name = "currentUserId", isQueryParameter = true)

                    val result = repository.getUserById(userId = profileOwnerId, currentUserId = currentUserId)

                    call.respond(
                        status = result.code,
                        message = result.data
                    )
                } catch (badRequestError: BadRequestException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "Invalid parameters"
                    )
                    return@get
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = "An unexpected error has occurred try again!"
                    )
                }
            }

            post(path = "/update"){
                var fileName = ""
                var updateUserParams: UpdateUserParams? = null
                val multiPartData = call.receiveMultipart()


                try {
                    multiPartData.forEachPart {
                            partData ->
                        when(partData) {
                            // an image
                            is PartData.FileItem -> {
                                fileName = partData.saveFile(folderPath = Constants.PROFILE_IMAGES_FOLDER_PATH)
                            }
                            is PartData.FormItem -> {
                                if (partData.name == "profile_data") {
                                        updateUserParams = Json.decodeFromString(partData.value)
                                }
                            }
                            else -> {} // do NOTHING
                        }
                        partData.dispose() // we don't need the PartData
                    }



                    val imageUrl = "${Constants.BASE_URL}${Constants.PROFILE_IMAGES_FOLDER}$fileName"

                    val results = repository.updateUser(
                        updateUserParams = updateUserParams!!.copy(
                            imageUrl = if (fileName.isNotEmpty()) imageUrl else updateUserParams!!.imageUrl // pass in new image or previous image
                        )
                    )


                    call.respond(
                        status = results.code,
                        message =  results.data
                    )
                } catch (anyError: Throwable) {
                    if (fileName.isNotEmpty()) {
                        File("${Constants.PROFILE_IMAGES_FOLDER_PATH}/$fileName").delete() // delete the saved image

                        call.respond(
                            status = HttpStatusCode.InternalServerError,
                            message = ProfileResponse(
                                success = false,
                                message = "An unexpected error has occurred, try again!"
                            )
                        )
                    }
                    return@post
                }
            }
        }
    }
}