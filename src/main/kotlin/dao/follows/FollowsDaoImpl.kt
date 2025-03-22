package com.example.plugins.dao.follows

import dao.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq

class FollowsDaoImpl : FollowsDao {
    override suspend fun followUser(follower: Long, following: Long): Boolean {
        return dbQuery{
            val insertStatement = FollowsTable.insert {
                it[followerId] = follower
                it[followingId] = following
            }

            insertStatement.resultedValues?.singleOrNull() != null
        }
    }

    override suspend fun unfollowUser(follower: Long, following: Long): Boolean {
        try{
            return dbQuery {
                FollowsTable.deleteWhere {
                    (followerId eq  follower) and  (followingId eq following)
                } > 0
            }
        } catch (e: Throwable) {
            println("ERROR$e")
            return false
        }

    }

    override suspend fun getFollowers(userId: Long, pageNumber: Int, pageSize: Int): List<Long> {
        return dbQuery {
            FollowsTable.select(FollowsTable.followerId eq userId)
                .orderBy(FollowsTable.followsDate, SortOrder.DESC)
                .offset(((pageNumber - 1) * pageSize).toLong())
                .limit(pageSize)
                .map { it[FollowsTable.followingId] }
        }



    }

    override suspend fun getFollowing(userId: Long, pageNumber: Int, pageSize: Int): List<Long> {
        return dbQuery {
            FollowsTable.select(FollowsTable.followingId eq userId)
                .orderBy(FollowsTable.followsDate, SortOrder.DESC)
                .offset(((pageNumber - 1) * pageSize).toLong())
                .limit(pageSize)
                .map { it[FollowsTable.followerId] }
        }
    }

    override suspend fun isAlreadyFollowing(follower: Long, following: Long): Boolean {
        return dbQuery {
            val queryResult = FollowsTable.selectAll().where {
                (FollowsTable.followerId eq follower) and (FollowsTable.followingId eq following)
            }
            !queryResult.empty()
        }
    }

}