package com.example.plugins.route

import com.example.plugins.model.FollowAndUnfollowResponse
import com.example.plugins.model.FollowsParams
import com.example.plugins.repository.follows.FollowsRepository
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.followsRouting (
) {
    val repository by inject<FollowsRepository>()

    // ensure user is authenticated when accessing this resource
    authenticate {
        route(path = "/follow") {
            post {
                val params = call.receiveNullable<FollowsParams>()

                if (params == null) {
                    call.respond(
                        status = HttpStatusCode.BadRequest,
                        message = FollowAndUnfollowResponse(
                            success = false,
                            message = "Oops, something went wrong"
                        )
                    )
                    return@post
                }

                val result = if (params.isFollowing) {
                    repository.followUser(follower = params.follower, following = params.following)
                } else {
                    repository.unfollowUser(follower = params.follower, following = params.following)
                }

                call.respond(
                    status = HttpStatusCode.OK,
                    message = result
                )
            }
        }
    }

}