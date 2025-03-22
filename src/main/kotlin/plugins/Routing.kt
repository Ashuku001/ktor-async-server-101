package com.example.plugins

import com.example.plugins.route.authRouting
import com.example.plugins.route.followsRouting
import com.example.plugins.route.postRouting
import io.ktor.server.application.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRouting()
        followsRouting()
        postRouting()

        // to view images
        static {
            resources(
                "static"
            )
        }
    }
}
