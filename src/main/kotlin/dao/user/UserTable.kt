package com.example.plugins.dao.user

import org.jetbrains.exposed.sql.Table

// define the model
object UserTable: Table(name = "users") {

    val id = long(name = "user_id")
    val name = varchar(name = "user_name", length = 250)
    val email = varchar(name = "user_email", length = 250)
    val bio = text(name = "user_bio").default(
        defaultValue = "Hey, what's up? Welcome to my SocialApp page!"
    )
    val password = varchar(name = "user_password", length = 100)
    val imageUrl = text(name = "image_url").nullable()
    val followersCount = integer(name = "followers_count").default(0)
    val followingCount = integer(name = "following_count").default(0)

    // override the primary key
    override  val primaryKey: PrimaryKey
        get() = PrimaryKey(id)
}

// User model schema for User instead of UserRow
data class UserRow(
    val id: Long,
    val name: String,
    val email: String,
    val bio: String,
    val imagerUrl: String?,
    val password: String,
    val followingCount: Int,
    val followersCount: Int,
)