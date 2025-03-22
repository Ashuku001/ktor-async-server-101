package com.example.plugins.dao.user

import com.example.plugins.model.SignUpParams

// define a type
interface UserDao {
    suspend fun insert(params: SignUpParams): UserRow?
    suspend fun findByEmail(email: String): UserRow?
    suspend fun updateFollowsCount(follower: Long, following: Long, isFollowing: Boolean): Boolean
}