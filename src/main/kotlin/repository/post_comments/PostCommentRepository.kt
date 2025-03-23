package com.example.plugins.repository.post_comments

import com.example.plugins.model.*
import com.example.plugins.util.Response

interface PostCommentRepository {
    suspend fun addComment(params: NewCommentParams): Response<CommentResponse>

    suspend fun removeComment(params: RemoveCommentParams): Response<CommentResponse>

    suspend fun getPostComments(postId: Long, pageNumber: Int, pageSize: Int): Response<CommentsResponse>
}