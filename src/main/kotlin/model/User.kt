package com.example.plugins.model

import kotlinx.serialization.Serializable

@Serializable
data class User (
    val id: Long,
    val name: String,
    val email: String,
    val bio: String,
    val imagerUrl: String?,
    val password: String,
    val followingCount: Int,
    val followersCount: Int,
)
