package com.example.plugins.route

import com.example.plugins.model.LikeParams
import com.example.plugins.model.LikeResponse
import com.example.plugins.repository.post_likes.PostLikesRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.postLikesRouting () {
    val repository by inject<PostLikesRepository>()

    authenticate {
        route(path = "/post/likes") {
            post(path = "/add") {
                try {
                    val likeParams = call.receiveNullable<LikeParams>()
                    if(likeParams == null) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = LikeResponse(
                                success = false,
                                message = "Could not parse like params"
                            )
                        )
                        return@post
                    }

                    val result = repository.addLike(likeParams)


                    call.respond(
                        status = result.code,
                        message = result.data
                    )

                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = LikeResponse(
                            success = false,
                            message = "An unexpected error occurred, try again!"
                        )
                    )
                }
            }

            delete(path = "/remove") {

                try {
                    val likeParams = call.receiveNullable<LikeParams>()
                    if(likeParams == null) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = LikeResponse(
                                success = false,
                                message = "Could not parse like params"
                            )
                        )
                        return@delete
                    }

                    val result = repository.removeLike(likeParams)

                    call.respond(
                        status = result.code,
                        message = result.data
                    )

                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = LikeResponse(
                            success = false,
                            message = "An unexpected error occurred, try again!"
                        )
                    )
                }
            }
        }
    }
}