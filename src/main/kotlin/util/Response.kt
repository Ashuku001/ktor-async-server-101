package com.example.plugins.util

import io.ktor.http.*
import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable

@Serializable
sealed class Response<T> {
    abstract val data: T
    abstract val code: HttpStatusCode

    @Serializable
    data class Success<T>(
        override val data: T,
        @Contextual override val code: HttpStatusCode = HttpStatusCode.OK
    ) : Response<T>()

    @Serializable
    data class Error<T>(
        override val data: T,
        @Contextual override val code: HttpStatusCode
    ) : Response<T>()
}
