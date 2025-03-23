package com.example.plugins

import com.example.plugins.route.*
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRouting()
        followsRouting()
        postRouting()
        profileRouting()
        postCommentRouting()
        postLikesRouting()

        // to view images
        static {
            resources(
                "static"
            )
        }
    }
}
