package com.example.plugins.route

import com.example.plugins.model.CommentResponse
import com.example.plugins.model.NewCommentParams
import com.example.plugins.model.RemoveCommentParams
import com.example.plugins.repository.post_comments.PostCommentRepository
import com.example.plugins.util.Constants
import com.example.plugins.util.getLongParameter
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.postCommentRouting () {
    val repository by inject<PostCommentRepository>()

    authenticate {
        route(path = "/post/comments") {
            post(path = "/create") {
                try {
                    val params = call.receiveNullable<NewCommentParams>()

                    if (params == null) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = CommentResponse(
                                success = false,
                                message = "Could not parse comment parameters"
                            )
                        )
                        return@post
                    }

                    val result = repository.addComment(params)

                    call.respond(
                        status = HttpStatusCode.OK,
                        message = result
                    )
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = "Something unexpected occurred. Please try again"
                    )
                }
            }

            delete(path = "/delete"){
                try {
                    val params = call.receiveNullable<RemoveCommentParams>()
                    println("IN HERE")
                    if (params == null) {
                        call.respond(
                            status = HttpStatusCode.BadRequest,
                            message = CommentResponse(
                                success = false,
                                message = "Could not parse comment parameters"
                            )
                        )
                        return@delete
                    }
                    println(params)

                    val result = repository.removeComment(params)

                    call.respond(
                        status = HttpStatusCode.OK,
                        message = result
                    )
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = "Something unexpected occurred. Please try again"
                    )
                }
            }

            get(path = "/{postId}") {
                try {
                    val postId = call.getLongParameter(name = "postId")
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                    val result = repository.getPostComments(postId = postId, pageNumber = page, pageSize = limit)

                    call.respond(
                        status = HttpStatusCode.OK,
                        message = result
                    )
                }  catch (badRequestError: BadRequestException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "Invalid parameters passed"
                    )
                }  catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = "Something unexpected occurred. Please try again"
                    )
                }

            }
        }
    }
}