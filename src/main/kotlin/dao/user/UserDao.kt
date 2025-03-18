package com.example.plugins.dao.user

import com.example.plugins.model.SignUpParams
import com.example.plugins.model.User

// define a type
interface UserDao {
    suspend fun insert(params: SignUpParams): User?
    suspend fun findByEmail(email: String): User?
}