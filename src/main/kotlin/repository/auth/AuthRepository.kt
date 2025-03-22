package com.example.plugins.repository.auth

import com.example.plugins.model.AuthResponse
import com.example.plugins.model.SignInParams
import com.example.plugins.model.SignUpParams
import com.example.plugins.util.Response

interface AuthRepository {
    suspend fun signUp(params: SignUpParams): Response<AuthResponse>
    suspend fun signIn(params: SignInParams): Response<AuthResponse>
}