package com.example.plugins.util

import io.ktor.http.*

// sealed to restrict inheritance i.e., all classes must be declared inside the same file
// Generic API response
sealed class Response<T>(
    val code: HttpStatusCode = HttpStatusCode.OK,
    val data: T
) {
    class Success<T>(data: T): Response<T>(data = data)

    class Error<T>(
        code: HttpStatusCode,
        data: T
    ): Response<T>(code, data)
}