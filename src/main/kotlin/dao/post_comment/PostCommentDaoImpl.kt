package com.example.plugins.dao.post_comment

import com.example.plugins.dao.post.PostTable
import com.example.plugins.dao.user.UserTable
import com.example.plugins.model.Post
import com.example.plugins.util.IdGenerator
import dao.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq


class PostCommentDaoImpl : PostCommentDao {
    override suspend fun addComment(postId: Long, userId: Long, content: String): PostCommentRow? {
        return dbQuery {
            val commentId = IdGenerator.generateId()
            PostCommentTable.insert {
                it[PostCommentTable.commentId] = commentId
                it[PostCommentTable.postId] = postId
                it[PostCommentTable.userId] = userId
                it[PostCommentTable.content] = content
            }

            PostCommentTable
                .join(
                    otherTable = UserTable,
                    otherColumn = UserTable.id,
                    onColumn = PostCommentTable.userId,
                    joinType = JoinType.INNER
                )
                .select(PostCommentTable.columns + UserTable.name + UserTable.imageUrl)
                .where{(PostCommentTable.commentId eq commentId) and (PostCommentTable.postId eq postId)}
                .singleOrNull()
                ?.let{toPostCommentRow(it)}
        }
    }

    override suspend fun removeComment(commentId: Long, postId: Long): Boolean {
        return dbQuery {
            PostCommentTable.deleteWhere {
                (PostCommentTable.commentId eq  commentId) and (PostCommentTable.postId eq postId)
            } > 0
        }
    }

    override suspend fun findComment(commentId: Long, postId: Long): PostCommentRow? {
        return dbQuery {
            PostCommentTable
                .join(
                    otherTable = UserTable,
                    otherColumn = UserTable.id,
                    onColumn = PostCommentTable.userId,
                    joinType = JoinType.INNER
                )
                .select(PostCommentTable.columns + UserTable.name + UserTable.imageUrl)
                .where{(PostCommentTable.commentId eq commentId) and (PostCommentTable.postId eq postId)}
                .singleOrNull()
                ?.let{toPostCommentRow(it)}

        }
    }

    override suspend fun getComments(postId: Long, pageNumber: Int, pageSize: Int): List<PostCommentRow> {
        return dbQuery {
            PostCommentTable
                .join(
                    otherTable = UserTable,
                    otherColumn = UserTable.id,
                    onColumn = PostCommentTable.userId,
                    joinType = JoinType.INNER
                )
                .select(PostCommentTable.columns + UserTable.name + UserTable.imageUrl)
                .where{(PostCommentTable.postId eq postId)}
                .orderBy(PostCommentTable.createdAt, order = SortOrder.DESC)
                .offset(((pageNumber - 1) * pageSize).toLong())
                .limit(pageSize)
                .map{toPostCommentRow(it)}
        }
    }

    private fun toPostCommentRow (resultRow: ResultRow): PostCommentRow {
        return PostCommentRow(
            commentId = resultRow[PostCommentTable.commentId],
            content = resultRow[PostCommentTable.content],
            postId = resultRow[PostCommentTable.postId],
            userId = resultRow[PostCommentTable.userId],
            userName = resultRow[UserTable.name],
            userImageUrl = resultRow[UserTable.imageUrl],
            createdAt = resultRow[PostCommentTable.createdAt].toString()
        )
    }
}