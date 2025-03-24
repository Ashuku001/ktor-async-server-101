package com.example.plugins.repository.post_comments

import com.example.plugins.dao.post.PostDao
import com.example.plugins.dao.post_comment.PostCommentDao
import com.example.plugins.dao.post_comment.PostCommentRow
import com.example.plugins.model.*
import com.example.plugins.util.Response
import io.ktor.http.*

class PostCommentRepositoryImpl(
    private val postCommentDao: PostCommentDao,
    private val postDao: PostDao
): PostCommentRepository {
    override suspend fun addComment(params: NewCommentParams): Response<CommentResponse> {
        val postCommentRow = postCommentDao.addComment(
            postId = params.postId,
            userId = params.userId,
            content = params.content
        )

        return if(postCommentRow == null) {
            Response.Error(
                code = HttpStatusCode.Conflict,
                data = CommentResponse(
                    success = false,
                    message = "Could not insert comment into the db"
                )
            )
        } else {
            postDao.updateCommentCounts(postId = params.postId, decrement = false)
            Response.Success(
                data = CommentResponse(
                    success = true,
                    comment = toPostComment(postCommentRow)
                )
            )
        }
    }

    override suspend fun removeComment(params: RemoveCommentParams): Response<CommentResponse> {
        val commentRow = postCommentDao.findComment(commentId = params.commentId, postId = params.postId)

        return if (commentRow == null) {
            Response.Error(
                code = HttpStatusCode.NotFound,
                data = CommentResponse(
                    success = false,
                    message = "Comment ${params.commentId} not found"
                )
            )
        } else {
            val postOwnerId = postDao.getPost(postId = params.postId)!!.userId

            if(params.userId != commentRow.userId && params.userId != postOwnerId) {
                Response.Error(
                    code = HttpStatusCode.Forbidden,
                    data = CommentResponse(
                        success = false,
                        message = "User ${params.userId} cannot delete comment ${params.commentId}"
                    )
                )
            } else {
                val commentWasRemoved = postCommentDao.removeComment(commentId = params.commentId, postId = params.postId)

                if(commentWasRemoved) {
                    postDao.updateCommentCounts(postId = params.postId, decrement = true)

                    Response.Success(
                        data = CommentResponse(
                            success = true
                        )
                    )
                } else {
                    Response.Error(
                        code = HttpStatusCode.Conflict,
                        data = CommentResponse(
                            success = false,
                            message = "The comment could not be removed"
                        )
                    )
                }
            }
        }
    }

    override suspend fun getPostComments(postId: Long, pageNumber: Int, pageSize: Int): Response<CommentsResponse> {
        val commentsRow = postCommentDao.getComments(
            postId = postId,
            pageSize = pageSize,
            pageNumber = pageNumber
        )

        val comments = commentsRow.map { toPostComment(it) }

        return Response.Success(
            data = CommentsResponse(
                success = true,
                comments = comments
            )
        )
    }

    private fun toPostComment(row: PostCommentRow): PostComment {
        return PostComment(
             commentId = row.commentId,
             content = row.content,
             userId = row.userId,
             userName = row.userName,
             userImageUrl = row.userImageUrl,
             postId = row.postId,
             createdAt = row.createdAt.toString()
        )
    }
}