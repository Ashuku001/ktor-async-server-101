package com.example.plugins.repository.post

import com.example.plugins.dao.follows.FollowsDao
import com.example.plugins.dao.post.PostDao
import com.example.plugins.dao.post.PostRow
import com.example.plugins.dao.post_likes.PostLikeDao
import com.example.plugins.model.Post
import com.example.plugins.model.PostResponse
import com.example.plugins.model.PostTextParams
import com.example.plugins.model.PostsResponse
import com.example.plugins.util.Response
import io.ktor.http.*

class PostRepositoryImpl (
    private val postDao: PostDao,
    private val postLikeDao: PostLikeDao,
    private val followsDao: FollowsDao
) : PostRepository {
    override suspend fun createPost(imageUrl: String, postTextParams: PostTextParams): Response<PostResponse> {
        val postIsCreated = postDao.createPost(
            caption = postTextParams.caption,
            userId = postTextParams.userId,
            imageUrl = imageUrl
        )

        return if(postIsCreated) {
            Response.Success(
                data = PostResponse(
                    success = true,
                )
            )

        } else {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = PostResponse(
                    success = false,
                    message = "Post was not inserted in the db"
                )
            )
        }
    }

    override suspend fun getFeedPosts(userId: Long, pageNumber: Int, pageSize: Int): Response<PostsResponse> {
        val followingUsers = followsDao.getAllFollowing(userId = userId,).toMutableList()

        followingUsers.add(userId)


        val postsRows = postDao.getFeedPost(userId = userId, follows = followingUsers, pageSize = pageSize, pageNumber = pageNumber)

        val posts = postsRows.map { toPost(
            postRow = it,
            isPostLiked = postLikeDao.isPostLiked(postId = it.postId, userId = userId),
            isOwnPost = it.userId == userId
        ) }

        println(posts.map { it.postId })

        return Response.Success(
            data = PostsResponse(
                posts = posts,
                success = true
            )
        )
    }

    override suspend fun getPostsByUser(
        postsOwnerId: Long,
        currentUserId: Long,
        pageNumber: Int,
        pageSize: Int
    ): Response<PostsResponse> {
        val postsRows = postDao.getPostByUser(userId = postsOwnerId, pageSize = pageSize, pageNumber = pageNumber)

        val posts = postsRows.map { toPost(
            postRow = it,
            isPostLiked = postLikeDao.isPostLiked(postId = it.postId, userId = currentUserId),
            isOwnPost = it.userId == currentUserId
        ) }


        println(posts)

        return Response.Success(
            data = PostsResponse(
                posts = posts,
                success = true
            )
        )
    }

    override suspend fun getPost(postId: Long, currentUserId: Long): Response<PostResponse> {
        val post = postDao.getPost(postId)
        return if (post == null) {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = PostResponse(
                    success = false,
                    message = "Could not retrieve post from the database"
                )
            )
        } else {
            val isPostLiked = postLikeDao.isPostLiked(postId = postId, userId = currentUserId)
            val isOwnPost = post.userId == currentUserId
            Response.Success(
                data  = PostResponse(
                    success = true,
                    post = toPost(post, isPostLiked = isPostLiked, isOwnPost = isOwnPost)
                )
            )
        }
    }

    override suspend fun deletePost(postId: Long): Response<PostResponse> {
        val postIsDeleted = postDao.deletePost(
            postId = postId
        )

        return if(postIsDeleted) {
            Response.Success(
                data = PostResponse(
                    success = true,
                )
            )
        } else {
            Response.Error(
                code = HttpStatusCode.InternalServerError,
                data = PostResponse(
                    success = false,
                    message = "Post could not be deleted from db"
                )
            )
        }
    }

    private fun toPost(postRow: PostRow, isPostLiked: Boolean, isOwnPost: Boolean): Post{
        return Post(
            postId = postRow.postId,
            caption = postRow.caption,
            imageUrl = postRow.imageUrl,
            likesCount = postRow.likesCount,
            commentsCount = postRow.commentsCount,
            userId = postRow.userId,
            userName = postRow.userName,
            userImageUrl = postRow.userImageUrl,
            createdAt = postRow.createdAt,
            isLiked = isPostLiked,
            isOwnPost = isOwnPost
        )
    }
}