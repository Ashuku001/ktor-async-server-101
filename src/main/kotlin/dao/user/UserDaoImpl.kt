package com.example.plugins.dao.user

import com.example.plugins.model.SignUpParams
import com.example.plugins.security.hashPassword
import com.example.plugins.util.IdGenerator
import dao.DatabaseFactory.dbQuery
import org.h2.engine.User
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus

// extends the UserDao data access class
class UserDaoImpl : UserDao {
    override suspend fun insert(params: SignUpParams): UserRow? {
        return dbQuery {
            val insertStatement = UserTable.insert {
                it[id] = IdGenerator.generateId()
                it[name] = params.name
                it[email] = params.email
                it[password] = hashPassword(params.password)
            }

            insertStatement.resultedValues?.singleOrNull()?.let {
                rowToUser(it)
            }
        }
    }

    override suspend fun findByEmail(email: String): UserRow? {
        return dbQuery {
            UserTable.select(UserTable.columns)
                .where{ UserTable.email eq email }
                .map {rowToUser(it)}
                .singleOrNull()
        }
    }

    override suspend fun findById(userId: Long): UserRow? {
        return dbQuery {
            UserTable
                .select(
                    UserTable.columns
                )
                .where{UserTable.id eq  userId}
                .map { rowToUser(it) }.singleOrNull()
        }
    }

    override suspend fun getUsers(ids: List<Long>): List<UserRow> {
        return dbQuery {
            UserTable.select(
                UserTable.columns
            ).where{(UserTable.id inList ids)}
                .map { rowToUser(it) }

        }
    }

    override suspend fun getPopularUsers(limit: Int): List<UserRow> {
        return dbQuery {
            UserTable.selectAll()
                .orderBy(column = UserTable.followersCount, order =  SortOrder.DESC)
                .limit(limit)
                .map { rowToUser(it) }
        }
    }

    override suspend fun updateUser(userId: Long, name: String, bio: String, imageUrl: String): Boolean {
        return dbQuery {
            UserTable.update(
                where = { UserTable.id eq userId },
            ){
                it[UserTable.name] = name
                it[UserTable.bio] = bio
                it[UserTable.imageUrl] = imageUrl
            } > 0
        }
    }

    // a helper function create a User Object
    private fun rowToUser(row: ResultRow): UserRow {
        return UserRow(
            id = row[UserTable.id],
            name = row[UserTable.name],
            password = row[UserTable.password],
            bio = row[UserTable.bio],
            email = row[UserTable.email],
            imagerUrl = row[UserTable.imageUrl],
            followingCount =  row[UserTable.followingCount],
            followersCount = row[UserTable.followersCount],
        )
    }

    override suspend fun updateFollowsCount(follower: Long, following: Long, isFollowing: Boolean): Boolean {
        return dbQuery {
            val count = if (isFollowing) + 1 else -1

            val success1 = UserTable.update({ UserTable.id eq  follower }){
                it.update(column = followingCount, value = followingCount.plus(count))
            } > 0

            val success2 = UserTable.update({ UserTable.id eq  following }){
                it.update(column = followersCount, value = followersCount.plus(count))
            } > 0

            success1 && success2
        }
    }

}