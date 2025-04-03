package com.onlinemarketplace.routes

import com.onlinemarketplace.models.UserRole
import com.onlinemarketplace.services.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String,
    val role: UserRole
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class AuthResponse(
    val token: String,
    val user: UserResponse
)

@Serializable
data class UserResponse(
    val id: String,
    val email: String,
    val name: String,
    val role: UserRole
)

fun Route.authRoutes(authService: AuthService) {
    route("/auth") {
        post("/register") {
            val request = call.receive<RegisterRequest>()
            try {
                val user = authService.registerUser(
                    email = request.email,
                    password = request.password,
                    name = request.name,
                    role = request.role
                )
                
                val token = authService.login(request.email, request.password)
                call.respond(
                    HttpStatusCode.Created,
                    AuthResponse(
                        token = token,
                        user = UserResponse(
                            id = user.id,
                            email = user.email,
                            name = user.name,
                            role = user.role
                        )
                    )
                )
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to e.message))
            }
        }
        
        post("/login") {
            val request = call.receive<LoginRequest>()
            try {
                val token = authService.login(request.email, request.password)
                val user = authService.userRepository.getUserByEmail(request.email)!!
                
                call.respond(
                    HttpStatusCode.OK,
                    AuthResponse(
                        token = token,
                        user = UserResponse(
                            id = user.id,
                            email = user.email,
                            name = user.name,
                            role = user.role
                        )
                    )
                )
            } catch (e: IllegalArgumentException) {
                call.respond(HttpStatusCode.Unauthorized, mapOf("error" to "Invalid email or password"))
            }
        }
    }
} 