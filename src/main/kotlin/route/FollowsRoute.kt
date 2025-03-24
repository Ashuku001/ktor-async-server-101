package com.example.plugins.route

import com.example.plugins.model.FollowAndUnfollowResponse
import com.example.plugins.model.FollowsParams
import com.example.plugins.repository.follows.FollowsRepository
import com.example.plugins.util.Constants
import com.example.plugins.util.getLongParameter
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.followsRouting (
) {
    val repository by inject<FollowsRepository>()

    // ensure user is authenticated when accessing this resource
    authenticate {
        route(path = "/follows") {
            post ("/follow") {
                val params = call.receiveNullable<FollowsParams>()

                if (params == null) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = FollowAndUnfollowResponse(
                            success = false,
                            message = "Invalid parameters or missing parameters"
                        )
                    )
                    return@post
                }

                val result = repository.followUser(follower = params.follower, following = params.following)

                call.respond(
                    status = result.code,
                    message = result.data
                )
            }

            post ("/unfollow") {
                val params = call.receiveNullable<FollowsParams>()

                if (params == null) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = FollowAndUnfollowResponse(
                            success = false,
                            message = "Invalid parameters or missing parameters"
                        )
                    )
                    return@post
                }

                val result = repository.unfollowUser(follower = params.follower, following = params.following)

                call.respond(
                    status = result.code,
                    message = result.data
                )
            }

            get("/followers") {
                try {
                    val userId = call.getLongParameter(name = "userId", isQueryParameter = true)
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                    val result = repository.getFollowers(userId = userId, pageNumber = page, pageSize = limit)

                    call.respond(
                        status = result.code,
                        message = result.data
                    )

                } catch (badRequestError: BadRequestException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "Invalid or missing parameters"
                    )
                } catch (anyError: Throwable) {
                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = "An unexpected error occurred please try again"
                    )
                }
            }

            get("/following") {
                try {
                    val userId = call.getLongParameter(name = "userId", isQueryParameter = true)
                    val page = call.request.queryParameters["page"]?.toIntOrNull() ?: 0
                    val limit = call.request.queryParameters["limit"]?.toIntOrNull() ?: Constants.DEFAULT_PAGE_SIZE

                    val result = repository.getFollowing(userId = userId, pageNumber = page, pageSize = limit)

                    call.respond(
                        status = result.code,
                        message = result.data
                    )

                } catch (badRequestError: BadRequestException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "Invalid or missing parameters"
                    )
                } catch (anyError: Throwable) {

                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = "An unexpected error occurred please try again"
                    )
                }
            }

            get("/suggestions") {
                println("PINGED SUGGESTION")
                try {
                    val userId = call.getLongParameter(name = "userId", isQueryParameter = true)

                    val result = repository.getFollowingSuggestions(userId)

                    println("PINGED SUGGESTION > $result")
                    call.respond(
                        status = result.code,
                        message = result.data
                    )

                } catch (badRequestError: BadRequestException) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = "Invalid or missing parameters"
                    )
                } catch (anyError: Throwable) {

                    call.respond(
                        status = HttpStatusCode.InternalServerError,
                        message = "An unexpected error occurred please try again"
                    )
                }
            }
        }
    }

}