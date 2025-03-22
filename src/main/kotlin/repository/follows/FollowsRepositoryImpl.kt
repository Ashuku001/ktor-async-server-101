package com.example.plugins.repository.follows

import com.example.plugins.dao.follows.FollowsDao
import com.example.plugins.dao.user.UserDao
import com.example.plugins.model.FollowAndUnfollowResponse
import com.example.plugins.util.Response
import io.ktor.http.*

class FollowsRepositoryImpl(
    private val userDao: UserDao,
    private val followDao: FollowsDao
): FollowsRepository {
    override suspend fun followUser(follower: Long, following: Long): Response<FollowAndUnfollowResponse> {
        return if (followDao.isAlreadyFollowing(follower, following)) {
            Response.Error(
                code = HttpStatusCode.Forbidden.value,
                data = FollowAndUnfollowResponse(
                    success = false,
                    message = "You are already following this user"
                )
            )
        } else {
            val success = followDao.followUser(follower, following)

            if(success) {
                val success2 = userDao.updateFollowsCount(follower, following, isFollowing = true)
                if (success2) {
                    Response.Success(
                        data = FollowAndUnfollowResponse(
                            success = true
                        )
                    )
                } else {
                    Response.Error(
                        code = HttpStatusCode.InternalServerError.value,
                        data = FollowAndUnfollowResponse (
                            success = false,
                            message = "Oops, something went wrong on our side, please try again!"
                        )
                    )
                }
            } else {
                Response.Error(
                    code = HttpStatusCode.InternalServerError.value,
                    data = FollowAndUnfollowResponse (
                        success = false,
                        message = "Oops, something went wrong on our side, please try again!"
                    )
                )
            }
        }
    }

    override suspend fun unfollowUser(follower: Long, following: Long): Response<FollowAndUnfollowResponse> {
        val success = followDao.unfollowUser(follower, following)

        return if(success) {
            val success2 = userDao.updateFollowsCount(follower, following, isFollowing = false)
            if(success2) {
                Response.Success(
                    code = HttpStatusCode.OK.value,
                    data = FollowAndUnfollowResponse(
                        success = false
                    )
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.InternalServerError.value,
                    data = FollowAndUnfollowResponse(
                        success = false,
                        message = "Oops, something went wrong on our side, please try again!d"
                    )
                )
            }
        } else {
            Response.Error(
                code = HttpStatusCode.InternalServerError.value,
                data = FollowAndUnfollowResponse(
                    success = false,
                    message = "Oops, something went wrong on our side, please try again!f"
                )
            )
        }
    }
}