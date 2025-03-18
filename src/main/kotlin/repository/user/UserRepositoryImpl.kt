package com.example.plugins.repository.user

import com.example.plugins.dao.user.UserDao
import com.example.plugins.generateToken
import com.example.plugins.model.AuthResponse
import com.example.plugins.model.AuthResponseData
import com.example.plugins.model.SignInParams
import com.example.plugins.model.SignUpParams
import com.example.plugins.security.hashPassword
import com.example.plugins.util.Response
import io.ktor.http.*

class UserRepositoryImpl(
    // Constructor
    private val userDao: UserDao // User data access in db TODO: include the dependency injection
): UserRepository {
    override suspend fun signUp(params: SignUpParams): Response<AuthResponse> {
        return if (userAlreadyExist(params.email)) {
            Response.Error(
                code = HttpStatusCode.Conflict.value,
                data = AuthResponse(
                    errorMessage = "A user with this email already exist."
                )
            )
        } else {
            val insertedUser = userDao.insert(params)

            if (insertedUser == null) {
                Response.Error(
                    code = HttpStatusCode.InternalServerError.value,
                    data = AuthResponse(
                        errorMessage = "Ooops, sorry we could not register the user, try later"
                    )
                )
            } else {
                Response.Success(
                    data = AuthResponse(
                        data = AuthResponseData(
                            id = insertedUser.id,
                            name = insertedUser.name,
                            bio = insertedUser.bio,
                            email = insertedUser.email,
                            token = generateToken(params.email)
                        )
                    ),
                    code = HttpStatusCode.OK.value
                )
            }
        }
    }

    override suspend fun signIn(params: SignInParams): Response<AuthResponse> {
        val user = userDao.findByEmail(params.email)

        return if (user == null) {
            Response.Error(
                code = HttpStatusCode.NotFound.value,
                data = AuthResponse(
                    errorMessage = "Invalid credentials, no user with this email!"
                )
            )
        } else {
            val hashedPassword = hashPassword(params.password)
            if(user.password == hashedPassword) {
                Response.Success(
                    data = AuthResponse(
                        data = AuthResponseData(
                            id = user.id,
                            name = user.name,
                            bio = user.bio,
                            email = user.email,
                            token = generateToken(params.email)
                        )
                    )
                )
            } else {
                Response.Error(
                    code = HttpStatusCode.Forbidden.value,
                    data = AuthResponse(
                        errorMessage = "Username or password does not match"
                    )
                )
            }
        }
    }

    private suspend fun userAlreadyExist(email: String): Boolean {
        return userDao.findByEmail(email) != null
    }
}