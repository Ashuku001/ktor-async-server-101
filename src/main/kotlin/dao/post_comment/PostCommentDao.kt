package com.example.plugins.dao.post_comment

import org.jetbrains.exposed.sql.ResultRow

interface PostCommentDao {
    suspend fun addComment(postId: Long, userId: Long, content: String): PostCommentRow?

    suspend fun removeComment(commentId: Long, postId: Long): Boolean

    suspend fun findComment(commentId: Long, postId: Long): PostCommentRow?

    suspend fun getComments(postId: Long, pageNumber: Int, pageSize: Int): List<PostCommentRow>
}