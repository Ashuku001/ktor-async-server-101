package com.example.plugins.route

import com.example.plugins.model.AuthResponse
import com.example.plugins.model.SignInParams
import com.example.plugins.model.SignUpParams
import com.example.plugins.repository.auth.AuthRepository
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.koin.ktor.ext.inject

fun Routing.authRouting(){
    val repository by inject<AuthRepository>()

    route(path = "/signup") {
        post {
            // takes the JSON and deserialize it
            val params = call.receiveNullable<SignUpParams>()
            // deserialize failed
            if (params == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = AuthResponse(errorMessage = "Invalid credentials!"),
                )
                return@post // quit the lambda
            }

            val result = repository.signUp(params = params)
            call.respond(
                status = HttpStatusCode.OK,
                message = result
            )
        }
    }

    route(path = "/signin") {
        post {
            // takes the JSON and deserialize it
            val params = call.receiveNullable<SignInParams>()
            // deserialize failed
            if (params == null) {
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = AuthResponse(errorMessage = "Invalid credentials!"),
                )
                return@post // quit the lambda
            }

            val result = repository.signIn(params = params)
            call.respond(
                status = HttpStatusCode.OK,
                message = result
            )
        }
    }
}

