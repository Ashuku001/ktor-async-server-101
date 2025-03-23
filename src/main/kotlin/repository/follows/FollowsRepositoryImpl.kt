package com.example.plugins.repository.follows

import com.example.plugins.dao.follows.FollowsDao
import com.example.plugins.dao.user.UserDao
import com.example.plugins.dao.user.UserRow
import com.example.plugins.model.FollowAndUnfollowResponse
import com.example.plugins.model.FollowUserData
import com.example.plugins.model.FollowsResponse
import com.example.plugins.util.Constants
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
                        success = true
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

    override suspend fun getFollowers(userId: Long, pageNumber: Int, pageSize: Int): Response<FollowsResponse> {
        val followersIds = followDao.getFollowers(userId = userId, pageSize = pageSize, pageNumber = pageNumber)
        println("FollowerIds $followersIds")
        val followersRows = userDao.getUsers(ids = followersIds)
        val followers = followersRows.map { followerRow ->
            val isFollowing = followDao.isAlreadyFollowing(follower = userId, following = followerRow.id )
            toFollowUserData(userRow = followerRow, isFollowing = isFollowing)
        }
        println("FOLLOWERS $followers")
        return Response.Success(
            data = FollowsResponse(
                success = true,
                follows = followers
            )
        )
    }

    override suspend fun getFollowing(userId: Long, pageNumber: Int, pageSize: Int): Response<FollowsResponse> {
        val followersIds = followDao.getFollowing(userId = userId, pageSize = pageSize, pageNumber = pageNumber)

        println("SUGGES$followersIds")

        val followersRows = userDao.getUsers(ids = followersIds)

        println("SUGGES$followersRows")
        val following = followersRows.map { followerRow ->
            val isFollowing = followDao.isAlreadyFollowing(follower = userId, following = followerRow.id )
            toFollowUserData(userRow = followerRow, isFollowing = isFollowing)
        }
        return Response.Success(
            data = FollowsResponse(
                success = true,
                follows = following
            )
        )
    }

    override suspend fun getFollowingSuggestions(userId: Long): Response<FollowsResponse> {
        val hasFollowing = followDao.getFollowing(userId = userId, pageNumber = 0, pageSize = 1).isNotEmpty()
        return if(hasFollowing) {
            Response.Error(
                code = HttpStatusCode.Forbidden.value,
                data = FollowsResponse(
                    success = false,
                    message = "User has following"
                )
            )
        } else {
            val suggestedFollowingRows = userDao.getPopularUsers(limit = Constants.SUGGESTED_FOLLOWING_LIMIT)
            // filter out current user
            val suggestedFollowing = suggestedFollowingRows
                .filterNot { it.id == userId }
                .map {
                    toFollowUserData(userRow = it, isFollowing = false)
                }
            Response.Success(
                data = FollowsResponse(
                    success = true,
                    follows = suggestedFollowing
                )
            )
        }
    }

    private fun toFollowUserData(userRow: UserRow, isFollowing: Boolean): FollowUserData {
        return FollowUserData(
             id = userRow.id,
             name = userRow.name,
             bio = userRow.bio,
             imageUrl = userRow.imagerUrl,
             isFollowing = isFollowing
        )
    }


}