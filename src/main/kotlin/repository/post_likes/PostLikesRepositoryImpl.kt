package com.example.plugins.repository.post_likes

import com.example.plugins.dao.post.PostDao
import com.example.plugins.dao.post_likes.PostLikeDao
import com.example.plugins.model.LikeParams
import com.example.plugins.model.LikeResponse
import com.example.plugins.util.Response
import io.ktor.http.*

class PostLikesRepositoryImpl(
    private val postLikeDao: PostLikeDao,
    private val postDao: PostDao
): PostLikesRepository {
    override suspend fun addLike(params: LikeParams): Response<LikeResponse> {
        val likeExists = postLikeDao.isPostLiked(postId = params.postId, userId = params.userId)

        return if(likeExists) {
            Response.Error(
                code = HttpStatusCode.Forbidden,
                data = LikeResponse(
                    success = false,
                    message = "Post already liked"
                )
            )
        } else {
            val likeAdded = postLikeDao.addLike(
                postId = params.postId,
                userId = params.userId,
            )

            if(likeAdded) {
                postDao.updateLikesCount(postId = params.postId, decrement = false)
                Response.Success(
                    code = HttpStatusCode.OK,
                    data = LikeResponse(
                        success = true
                    )
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.Conflict,
                    data = LikeResponse(
                        success = false,
                        message = "Unexpected error, try again!"
                    )
                )
            }

        }
    }

    override suspend fun removeLike(params: LikeParams): Response<LikeResponse> {
        val likeExists = postLikeDao.isPostLiked(postId = params.postId, userId = params.userId)

        return if(!likeExists) {
            Response.Error(
                code = HttpStatusCode.Forbidden,
                data = LikeResponse(
                    success = false,
                    message = "Post like does not exist"
                )
            )
        } else {
            val likeRemoved = postLikeDao.removeLike(
                postId = params.postId,
                userId = params.userId,
            )

            if(likeRemoved) {
                postDao.updateLikesCount(postId = params.postId, decrement = true)
                Response.Success(
                    code = HttpStatusCode.OK,
                    data = LikeResponse(
                        success = true
                    )
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.Conflict,
                    data = LikeResponse(
                        success = false,
                        message = "Unexpected could not remove like, try again later."
                    )
                )
            }

        }
    }
}