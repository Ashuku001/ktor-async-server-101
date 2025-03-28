package com.example.plugins.dao.post

import com.example.plugins.dao.post_likes.PostLikeDao
import com.example.plugins.dao.user.UserTable
import com.example.plugins.util.IdGenerator
import dao.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus


class PostDaoImpl: PostDao {
    override suspend fun createPost(caption: String, imageUrl: String, userId: Long): Boolean {
        return dbQuery{
            val insertStatement = PostTable.insert{
                it[postId] = IdGenerator.generateId()
                it[PostTable.caption] = caption
                it[PostTable.imageUrl] = imageUrl
                it[likesCount] = 0
                it[commentsCount] = 0
                it[PostTable.userId] = userId
            }

            insertStatement.resultedValues?.singleOrNull() != null
        }
    }

    override suspend fun getFeedPost(userId: Long, follows: List<Long>, pageNumber: Int, pageSize: Int): List<PostRow> {
        println(follows)
        return dbQuery {
            if (follows.size > 1) {
                // get posts of the user followed by userID
                getPosts(follows, pageNumber, pageSize)
            } else {
                // get the post for the current user wanting to see their own feed
                PostTable
                    .join(
                        otherTable = UserTable,
                        onColumn = PostTable.userId,
                        otherColumn = UserTable.id,
                        joinType = JoinType.INNER
                    )
                    .selectAll()
                    .orderBy(column = PostTable.likesCount, order = SortOrder.DESC) // show popular post if user has not followed anyout
                    .offset(((pageNumber - 1 ) * pageSize).toLong())
                    .limit(pageSize)
                    .map { toPostRow(it) }
            }
        }
    }

    override suspend fun getPostByUser(userId: Long, pageNumber: Int, pageSize: Int): List<PostRow> {
        return dbQuery {
            PostTable.join(
                otherColumn = UserTable.id,
                onColumn = PostTable.userId,
                otherTable = UserTable,
                joinType = JoinType.INNER
            )
                .select(
                    PostTable.postId,
                    PostTable.caption,
                    PostTable.imageUrl,
                    PostTable.likesCount,
                    PostTable.commentsCount,
                    PostTable.userId,
                    UserTable.name,
                    UserTable.imageUrl,
                    PostTable.createdAt
                )
                .where(PostTable.userId eq userId)
                .orderBy(column = PostTable.createdAt, order = SortOrder.DESC)
                .offset(((pageNumber - 1) * pageSize).toLong())
                .limit(pageSize)
                .map{toPostRow(it)}
        }
    }

    override suspend fun getPost(postId: Long): PostRow? {
        return dbQuery {
            PostTable.join(
                otherTable = UserTable,
                onColumn = PostTable.userId,
                otherColumn = UserTable.id,
                joinType = JoinType.INNER
            )
                .select(
                    PostTable.postId,
                    PostTable.caption,
                    PostTable.imageUrl,
                    PostTable.likesCount,
                    PostTable.commentsCount,
                    PostTable.userId,
                    UserTable.name,
                    UserTable.imageUrl,
                    PostTable.createdAt
                )
                .where{PostTable.postId eq postId}
                .singleOrNull()
                ?.let { toPostRow(it) }
        }
    }

    override suspend fun updateCommentCounts(postId: Long, decrement: Boolean): Boolean {
        return dbQuery {
            val value = if (decrement) -1 else 1
            PostTable.update(
                where = {PostTable.postId eq  postId},
            ) {
                it.update(
                    column = commentsCount, value = commentsCount.plus(value)
                )
            } > 0
        }
    }

    override suspend fun updateLikesCount(postId: Long, decrement: Boolean): Boolean {
        return dbQuery {
            val value = if (decrement) -1 else 1
            PostTable.update(
                where = {PostTable.postId eq  postId},
            ) {
                it.update(
                    column = likesCount, value = likesCount.plus(value)
                )
            } > 0
        }
    }

    override suspend fun deletePost(postId: Long): Boolean {
        return dbQuery {
            PostTable.deleteWhere { PostTable.postId eq postId } > 0
        }
    }

    private fun getPosts(users: List<Long>,  pageNumber: Int,  pageSize: Int): List<PostRow> {
        return PostTable
            .join(
                otherTable = UserTable,
                onColumn = PostTable.userId,
                otherColumn = UserTable.id,
                joinType = JoinType.INNER
            )
            .select(
                PostTable.postId,
                PostTable.caption,
                PostTable.imageUrl,
                PostTable.likesCount,
                PostTable.commentsCount,
                PostTable.userId,
                UserTable.name,
                UserTable.imageUrl,
                PostTable.createdAt)
            .where{(PostTable.userId inList users)}
            .orderBy(column = PostTable.createdAt, order = SortOrder.DESC)
            .offset(((pageNumber - 1 ) * pageSize).toLong())
            .limit(pageSize)
            .map { toPostRow(it) }
    }

    private fun toPostRow(row: ResultRow): PostRow {
        return PostRow(
             postId = row[PostTable.postId],
             caption = row[PostTable.caption],
             imageUrl = row[PostTable.imageUrl],
             likesCount = row[PostTable.likesCount],
             commentsCount = row[PostTable.commentsCount],
             userId = row[PostTable.userId],
             userName = row[UserTable.name],
             userImageUrl = row[UserTable.imageUrl],
             createdAt = row[PostTable.createdAt].toString()
        )
    }
}