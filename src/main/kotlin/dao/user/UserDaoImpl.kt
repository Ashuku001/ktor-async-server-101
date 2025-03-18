package com.example.plugins.dao.user

import com.example.plugins.model.SignUpParams
import com.example.plugins.model.User
import com.example.plugins.model.UserRow
import dao.DatabaseFactory.dbQuery
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insert

// extends the UserDao data access class
class UserDaoImpl : UserDao {
    override suspend fun insert(params: SignUpParams): User? {
        return dbQuery {
            val insertStatement = UserRow.insert {
                it[name] = params.name
                it[email] = params.email
                it[password] = params.password
            }

            insertStatement.resultedValues?.singleOrNull()?.let {
                this.rowToUser(it)
            }
        }
    }

    override suspend fun findByEmail(email: String): User? {
        return dbQuery {
            UserRow.select(UserRow.columns)
                .where{ UserRow.email eq email }
                .map {rowToUser(it)}
                .singleOrNull()
        }
    }

    // a helper function create a User Object
    private fun rowToUser(row: ResultRow): User {
        return User(
            id = row[UserRow.id],
            name = row[UserRow.name],
            password = row[UserRow.password],
            bio = row[UserRow.bio],
            email = row[UserRow.email],
            avatar = row[UserRow.avatar],
        )
    }
}