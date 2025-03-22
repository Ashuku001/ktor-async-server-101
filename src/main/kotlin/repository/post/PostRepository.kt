package com.example.plugins.repository.post

import com.example.plugins.model.PostResponse
import com.example.plugins.model.PostTextParams
import com.example.plugins.model.PostsResponse
import com.example.plugins.util.Response

interface PostRepository {
    suspend fun createPost(imageUrl: String, postTextParams: PostTextParams): Response<PostResponse>

    suspend fun getFeedPosts(userId: Long, pageNumber: Int, pageSize: Int): Response<PostsResponse>

    suspend fun getPostsByUser(
        postsOwnerId: Long,
        currentUserId: Long,
        pageNumber: Int,
        pageSize: Int
    ): Response<PostsResponse>

    suspend fun getPost(postId: Long, currentUserId: Long): Response<PostResponse>

    suspend fun deletePost(postId: Long): Response<PostResponse>

}