package com.example.plugins.repository.profile

import com.example.plugins.model.ProfileResponse
import com.example.plugins.model.UpdateUserParams
import com.example.plugins.util.Response

interface ProfileRepository {
    suspend fun getUserById(userId: Long, currentUserId: Long): Response<ProfileResponse>
    suspend fun updateUser(updateUserParams: UpdateUserParams): Response<ProfileResponse>
}