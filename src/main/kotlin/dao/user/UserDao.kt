package com.example.plugins.dao.user

import com.example.plugins.model.SignUpParams

// define a type
interface UserDao {
    suspend fun insert(params: SignUpParams): UserRow?
    suspend fun findByEmail(email: String): UserRow?
    suspend fun findById(userId: Long): UserRow?
    suspend fun getUsers(ids: List<Long>): List<UserRow>
    suspend fun getPopularUsers(limit: Int): List<UserRow>
    suspend fun updateUser(userId: Long, name: String, bio: String, imageUrl: String): Boolean
    suspend fun updateFollowsCount(follower: Long, following: Long, isFollowing: Boolean): Boolean
}