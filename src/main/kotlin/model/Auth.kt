package com.example.plugins.model

import kotlinx.serialization.Serializable

// make the classes serializable meaning it can be converted to and from formats like JSON, ProtoBuf, or CBOR. i.e., a format that can be stored or transmitted
@Serializable
data class SignUpParams(
    val name: String,
    val email: String,
    val password: String
)

@Serializable
data class SignInParams(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val data: AuthResponseData?= null,
    val errorMessage: String? = null
)

@Serializable
data class AuthResponseData(
    val id: Long,
    val name: String,
    val email: String,
    val bio: String,
    val avatar: String? = null,
    val token: String,
    val followersCount: Int = 0,
    val followingCount: Int = 0,
)