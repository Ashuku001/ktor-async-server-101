package com.example.plugins.model

import kotlinx.serialization.Serializable

@Serializable
data class LikeParams(
    val postId: Long,
    val userId: Long,
)

@Serializable
data class Like(
    val likeId: Long,
    val postId: Long,
    val userId: Long,
    val likeDate: String
)

@Serializable
data class LikeResponse(
    val success: Boolean,
    val message: String? = null
)