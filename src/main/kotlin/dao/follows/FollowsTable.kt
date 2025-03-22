package com.example.plugins.dao.follows

import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object FollowsTable: Table(name = "follows") {
    val followerId = long(name = "follower_id")
    val followingId = long(name = "following_id")
    val followsDate = datetime(name = "follow_date").default(defaultValue = LocalDateTime.now())
}