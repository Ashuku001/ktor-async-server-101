package com.example.plugins.model

import kotlinx.serialization.Serializable

@Serializable
class PostTextParams(
    val caption: String,
    val userId: Long
)

@Serializable
data class Post(
    val postId: Long,
    val caption: String,
    val imageUrl: String,
    val likesCount: Int,
    val commentsCount:Int,
    val userId: Long,
    val userName: String,
    val userImageUrl: String?,
    val createdAt: String,
    val isLiked: Boolean, // says if current user has liked the psot
    val isOwnPost: Boolean, // check if the post belongs to current user
)

@Serializable
data class PostResponse(
    val success: Boolean,
    val post: Post? = null,
    val message: String? = null,
)

@Serializable
data class PostsResponse(
    val success: Boolean,
    val posts: List<Post> = listOf(),
    val message: String? = null
)