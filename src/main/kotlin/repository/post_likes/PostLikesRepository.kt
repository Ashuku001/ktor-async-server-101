package com.example.plugins.repository.post_likes

import com.example.plugins.model.LikeParams
import com.example.plugins.model.LikeResponse
import com.example.plugins.util.Response

interface PostLikesRepository {
    suspend fun addLike(params: LikeParams): Response<LikeResponse>
    suspend fun removeLike(params: LikeParams): Response<LikeResponse>
}