package com.example.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.example.plugins.model.AuthResponse
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.response.*

private const val ClAIM = "email"

private val jwtAudience = System.getenv("jwt.audience")
private val jwtDomain = System.getenv("jwt.domain")
private val jwtRealm = System.getenv("jwt.realm")
private val jwtSecret = System.getenv("jwt.secret")
private val CLAIM = "email"

fun Application.configureSecurity() {


    authentication {
        jwt {
            realm = jwtRealm
            verifier(
                JWT
                    .require(Algorithm.HMAC256(jwtSecret))
                    .withAudience(jwtAudience)
                    .withIssuer(jwtDomain)
                    .build()
            )
            validate { credential ->
                if (credential.payload.getClaim(CLAIM).asString() != null) {
                    JWTPrincipal(payload = credential.payload)
                } else {
                    null
                }
            }
            challenge { _,_ ->
                call.respond(
                    status = HttpStatusCode.Unauthorized,
                    message = AuthResponse(errorMessage = "Token is invalid or Token has expired"),
                )
            }
        }
    }
}

fun generateToken(email: String): String {


    println(">>>>>>>>>>>>>>>>>>>>>>>>>>> $jwtAudience")
    return JWT.create()
        .withAudience(jwtAudience)
        .withIssuer(jwtDomain)
        .withClaim(CLAIM, email)
        .sign(Algorithm.HMAC256(jwtSecret))
}