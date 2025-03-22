package com.example.plugins.util

import com.example.plugins.model.PostResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*

// extension of the call method in Application
suspend fun ApplicationCall.getLongParameter(name: String, isQueryParameter: Boolean = false): Long {
    val parameter = if (isQueryParameter) {
        request.queryParameters[name]?.toLongOrNull()
    } else {
        parameters[name]?.toLongOrNull()
    } ?: kotlin.run {
        respond(
            status = HttpStatusCode.BadRequest,
            message = PostResponse(
                success = false,
                message = "Parameter $name is missing or invalid"
            )
        )
        throw BadRequestException("Parameter $name is missing or invalid")
    }
    return parameter
}