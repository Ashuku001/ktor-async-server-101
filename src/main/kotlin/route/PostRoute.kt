package com.example.plugins.route

import com.example.plugins.model.PostResponse
import com.example.plugins.model.PostTextParams
import com.example.plugins.repository.post.PostRepository
import com.example.plugins.util.Constants
import com.example.plugins.util.Response
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

fun Routing.postRouting () {
    val postRepository by inject<PostRepository>()

    authenticate {
        // single post
        route(path = "/post") {
            post(path = "/create") {
                var fileName = ""
                var postTextParams: PostTextParams? = null
                val multipartData = call.receiveMultipart()

                multipartData.forEachPart {
                    partData ->
                    when(partData) {
                        // an image
                        is PartData.FileItem -> {
                            fileName = partData.saveFile(folderPath = Constants.POST_IMAGES_FOLDER_PATH)
                        }
                        is PartData.FormItem -> {
                            if (partData.name == "post_data") {
                                postTextParams = Json.decodeFromString(partData.value)
                            }
                        }
                        else -> {} // do anything
                    }

                    partData.dispose() // we don't need the PartData

                }

                val imageUrl = "${Constants.BASE_URL}${Constants.POST_IMAGES_FOLDER}$fileName"

                if (postTextParams == null) {
                    // delete the file that was saved
                    File("${Constants.POST_IMAGES_FOLDER_PATH}/$fileName").delete()
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = PostResponse(
                            success = false,
                            message = "Could not parse post data"
                        )
                    )
                } else {
                    val result = postRepository.createPost(imageUrl, postTextParams!!)
                    call.respond(
                        status = HttpStatusCode.OK,
                        message = result
                    )
                }

            }
            get(path = "/{postId}") {
                try {
                    val postId = call.getLongParameter(name ="postId", )
                    val currentUserId = call.getLongParameter(name ="currentUserId", isQueryParameter = true )

                    val result = postRepository.getPost(postId = postId, currentUserId = currentUserId)

                    call.respond(
                        status = HttpStatusCode.OK,
                        message = result
                    )
                } catch (badRequestError: BadRequestException) { // catch error thrown by getLongParameter
                    return@get
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = PostResponse(
                            success = false,
                            message = "An unexpected error has occurred, try again!"
                        )
                    )
                }
            }
            delete(path = "/{postId}") {
                try {
                    val postId = call.getLongParameter(name = "postId")
                    val result = postRepository.deletePost(postId = postId)

                    call.respond(
                        status = HttpStatusCode.OK,
                        message = result
                    )
                } catch (badRequestError: BadRequestException) {
                    return@delete
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = PostResponse(
                            success = false,
                            message = "An unexpected error has occurred, try again!"
                        )
                    )
                }

            }
        }

        // multipe of posts
        route(path = "/posts") {
            get(path = "/feed") {
                try {
                    val currentUserId = call.getLongParameter(name = "currentUserId", isQueryParameter = true)
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                    val result = postRepository.getFeedPosts(
                        userId = currentUserId,
                        pageNumber = page,
                        pageSize = limit,
                    )

                    call.respond(
                        status = HttpStatusCode.OK,
                        message = result
                    )
                }catch (badRequestError: BadRequestException) { // catch error thrown by getLongParameter
                    return@get
                } catch (anyError: Throwable) {
                    println(anyError)
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = PostResponse(
                            success = false,
                            message = "An unexpected error has occurred, try again!"
                        )
                    )
                }
            }

            get(path = "/{userId}") {
                try {
                    val postsOwnerId = call.getLongParameter(name = "userId")
                    val currentUserId = call.getLongParameter(name = "currentUserId", isQueryParameter = true)


                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                    val result = postRepository.getPostsByUser(
                        postsOwnerId = postsOwnerId,
                        currentUserId = currentUserId,
                        pageNumber = page,
                        pageSize = limit,
                    )

                    call.respond(
                        status = HttpStatusCode.OK,
                        message = result
                    )
                }catch (badRequestError: BadRequestException) { // catch error thrown by getLongParameter
                    return@get
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = PostResponse(
                            success = false,
                            message = "An unexpected error has occurred, try again!"
                        )
                    )
                }
            }

        }
    }
}