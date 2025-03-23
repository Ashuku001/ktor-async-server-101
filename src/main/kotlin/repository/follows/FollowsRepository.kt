package com.example.plugins.repository.follows

import com.example.plugins.model.FollowAndUnfollowResponse
import com.example.plugins.model.FollowsResponse
import com.example.plugins.util.Response

interface FollowsRepository {
    suspend fun followUser(follower: Long, following: Long): Response<FollowAndUnfollowResponse>

    suspend fun unfollowUser(follower: Long, following: Long): Response<FollowAndUnfollowResponse>

    suspend fun getFollowers(userId: Long, pageNumber: Int, pageSize: Int): Response<FollowsResponse>

    suspend fun getFollowing(userId: Long, pageNumber: Int, pageSize: Int): Response<FollowsResponse>

    suspend fun getFollowingSuggestions(userId: Long): Response<FollowsResponse>
}

