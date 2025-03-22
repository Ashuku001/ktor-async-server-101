package com.example.plugins.dao.user

import com.example.plugins.model.SignUpParams
import com.example.plugins.security.hashPassword
import com.example.plugins.util.IdGenerator
import dao.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.plus
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.update

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