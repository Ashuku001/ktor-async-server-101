package com.example.plugins.dao.post_likes

import com.example.plugins.dao.post.PostTable
import com.example.plugins.dao.user.UserTable
import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object PostLikeTable: Table(name = "post_likes") {
    val likeId = long(name = "like_id").uniqueIndex()
    val postId = long(name = "post_id").references(ref = PostTable.postId, onDelete = ReferenceOption.CASCADE)
    val userId = long(name = "user_id").references(ref = UserTable.id, onDelete = ReferenceOption.CASCADE)
    val likeDate = datetime(name = "like_date").default(defaultValue = LocalDateTime.now())
}

data class PostLikeRow(
    val likeId: Long,
    val postId: Long,
    val userId: Long,
    val likeDate: String
)

