package com.example.plugins.util

import io.ktor.http.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
sealed class Response<T> {
    @Serializable
    data class Success<T>(
        val data: T,
        @Contextual
        val code: Int = HttpStatusCode.OK.value
    ) : Response<T>()

    @Serializable
    data class Error<T>(
        val data: T,
        @Contextual
        val code: Int
    ) : Response<T>()
}
