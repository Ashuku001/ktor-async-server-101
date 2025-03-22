package com.example.plugins.model

import kotlinx.serialization.Serializable

@Serializable
data class FollowAndUnfollowResponse (
    val success: Boolean,
    val message: String? = null
)

@Serializable
data class FollowsParams(
    val follower: Long,
    val following: Long,
    val isFollowing: Boolean, // if true means we are unfollowing the user
)