package com.example.plugins.repository.profile

import com.example.plugins.dao.follows.FollowsDao
import com.example.plugins.dao.user.UserDao
import com.example.plugins.dao.user.UserRow
import com.example.plugins.model.Profile
import com.example.plugins.model.ProfileResponse
import com.example.plugins.model.UpdateUserParams
import com.example.plugins.util.Response
import io.ktor.http.*
import org.jetbrains.exposed.sql.CurrentOrFollowing

class ProfileRepositoryImpl (
    private val userDao: UserDao,
    private val followsDao: FollowsDao
) : ProfileRepository {
    override suspend fun getUserById(userId: Long, currentUserId: Long): Response<ProfileResponse> {
        val userRow = userDao.findById(userId = userId)

        return if (userRow == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = ProfileResponse(
                    success = false,
                    message = "Could not find user with id $userId"
                )
            )
        } else {
            val isFollowing = followsDao.isAlreadyFollowing(follower = currentUserId, following = userId)
            val isOwnProfile =  userId == currentUserId

            Response.Success(
                data = ProfileResponse(
                    success = true,
                    profile = toProfile(userRow, isFollowing, isOwnProfile)
                )
            )
        }
    }

    override suspend fun updateUser(updateUserParams: UpdateUserParams): Response<ProfileResponse> {
        val userExists = userDao.findById(userId = updateUserParams.userId) != null

        return if (userExists) {
            val userUpdated = userDao.updateUser(
                userId = updateUserParams.userId,
                name = updateUserParams.name,
                imageUrl = updateUserParams.imageUrl,
                bio = updateUserParams.bio
            )

            return if(userUpdated) {
                Response.Success(
                    data = ProfileResponse(
                        success = true
                    )
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.Conflict,
                    data = ProfileResponse(
                        success = false,
                        message = "Could not update user: ${updateUserParams.userId} "
                    )
                )
            }
        } else {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = ProfileResponse(
                    success = false,
                    message = "We could not find user: ${updateUserParams.userId} "
                )
            )
        }
    }

    private fun toProfile(userRow: UserRow, isFollowing: Boolean, isOwnProfile: Boolean): Profile {
        return Profile(
            id = userRow.id,
            name = userRow.name,
            bio = userRow.bio,
            imageUrl = userRow.imagerUrl,
            followingCount = userRow.followingCount,
            followersCount = userRow.followersCount,
            isFollowing = isFollowing,
            isOwnProfile = isOwnProfile
        )
    }
}