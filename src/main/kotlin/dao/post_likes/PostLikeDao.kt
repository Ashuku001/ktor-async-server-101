package com.example.plugins.dao.post_likes

interface PostLikeDao {
    suspend fun addLike(postId: Long, userId: Long): Boolean

    suspend fun removeLike(postId: Long, userId: Long): Boolean

    suspend fun isPostLiked(postId: Long, userId: Long): Boolean
}