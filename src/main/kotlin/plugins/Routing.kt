package com.example.plugins

import com.example.plugins.route.authRouting
import com.example.plugins.route.followsRouting
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        authRouting()
        followsRouting()
    }
}
