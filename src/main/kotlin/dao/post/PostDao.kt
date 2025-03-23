package com.example.plugins.dao.post

interface PostDao {
    suspend fun createPost(caption: String, imageUrl: String, userId: Long): Boolean

    suspend fun getFeedPost(userId: Long, follows: List<Long>, pageNumber: Int, pageSize: Int): List<PostRow>

    suspend fun getPostByUser(userId: Long, pageNumber: Int, pageSize: Int): List<PostRow>

    suspend fun getPost(postId: Long): PostRow?

    suspend fun updateCommentCounts(postId: Long, decrement: Boolean = false): Boolean

    suspend fun updateLikesCount(postId: Long, decrement: Boolean = false): Boolean

    suspend fun deletePost(postId: Long): Boolean

}