package com.example.plugins.dao.post

import com.example.plugins.dao.user.UserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object PostTable: Table(name = "post") {
    val postId = long(name = "post_id").uniqueIndex()
    val caption = varchar(name = "caption", length = 300)
    val imageUrl = varchar(name = "image_url", length = 300)
    val likesCount = integer(name = "likes_count", )
    val commentsCount = integer(name = "comments_count")
    val userId = long(name = "user_id").references(ref = UserTable.id, onDelete = ReferenceOption.CASCADE)
    val createdAt = datetime(name = "created_at").default(defaultValue = LocalDateTime.now())
}


data class PostRow(
    val postId: Long,
    val caption: String,
    val imageUrl: String,
    val likesCount: Int,
    val commentsCount:Int,
    val userId: Long,
    val userName: String,
    val userImageUrl: String?,
    val createdAt: String,
)