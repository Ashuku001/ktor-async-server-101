package com.example.plugins

import com.example.plugins.model.AuthResponse
import com.example.plugins.model.FollowAndUnfollowResponse
import com.example.plugins.model.PostResponse
import com.example.plugins.model.PostsResponse
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
