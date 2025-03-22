package com.example.plugins.dao.post_likes

import com.example.plugins.dao.post.PostTable
import com.example.plugins.util.IdGenerator
import org.jetbrains.exposed.sql.insert
import dao.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.deleteWhere


class PostLikeDaoImpl : PostLikeDao {
    override suspend fun addLike(postId: Long, userId: Long): Boolean {
        return dbQuery {
            val insertStatement = PostLikeTable.insert {
                it[likeId] = IdGenerator.generateId()
                it[PostLikeTable.postId] = postId
                it[PostLikeTable.userId] = postId
            }

            insertStatement.resultedValues?.isNotEmpty() ?: false
        }
    }

    override suspend fun removeLike(postId: Long, userId: Long): Boolean {
        return dbQuery {
            PostLikeTable.deleteWhere {
                (PostLikeTable.postId eq postId) and (PostLikeTable.userId eq userId)
            } > 0
        }
    }

    override suspend fun isPostLiked(postId: Long, userId: Long): Boolean {
        return dbQuery {
            PostLikeTable.select(PostLikeTable.likeId).where{(PostLikeTable.postId eq postId) and (PostLikeTable.userId eq userId)}
                .toList()
                .isNotEmpty()
        }
    }
}