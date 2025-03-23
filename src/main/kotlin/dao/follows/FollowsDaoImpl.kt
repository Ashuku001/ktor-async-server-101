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
            return false
        }

    }

    override suspend fun getFollowers(userId: Long, pageNumber: Int, pageSize: Int): List<Long> {
        return dbQuery {
            FollowsTable
                .select(FollowsTable.columns)
                .where(FollowsTable.followingId eq userId) // user being followed
                .orderBy(FollowsTable.followsDate, SortOrder.DESC)
                .offset(((pageNumber - 1) * pageSize).toLong())
                .limit(pageSize)
                .map {
                    println("follower are here$it")
                    it[FollowsTable.followerId]
                }
        }
    }

    override suspend fun getFollowing(userId: Long, pageNumber: Int, pageSize: Int): List<Long> {
        return dbQuery {
            FollowsTable
                .select(FollowsTable.columns)
                .where(FollowsTable.followerId eq userId)
                .orderBy(FollowsTable.followsDate, SortOrder.DESC)
//                .offset(((pageNumber - 1) * pageSize).toLong())
//                .limit(pageSize)
                .map {
                    println(it)
                    it[FollowsTable.followingId] }
        }
    }

    override suspend fun getAllFollowing(userId: Long): List<Long>{
        return dbQuery {
            FollowsTable
                .select(FollowsTable.columns)
                .where(FollowsTable.followerId eq userId)
                .map{it[FollowsTable.followingId]}
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