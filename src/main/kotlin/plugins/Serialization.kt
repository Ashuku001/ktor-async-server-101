package com.example.plugins

import com.example.plugins.model.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.plugins.contentnegotiation.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.modules.polymorphic
import kotlinx.serialization.modules.subclass

val module = SerializersModule {
    polymorphic(Any::class) {
        subclass(AuthResponse::class)
        subclass(FollowAndUnfollowResponse::class)
        subclass(PostResponse::class)
        subclass(PostsResponse::class)
        subclass(ProfileResponse::class)
        subclass(CommentResponse::class)
        subclass(CommentsResponse::class)
        // Register other subclasses as needed
    }
}

fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(
            Json {
                serializersModule = module
            }
        )
    }
}
