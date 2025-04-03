package com.onlinemarketplace

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.onlinemarketplace.database.Database
import com.onlinemarketplace.repositories.*
import com.onlinemarketplace.routes.*
import com.onlinemarketplace.services.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    // Initialize repositories
    val userRepository = UserRepositoryImpl()
    val productRepository = ProductRepositoryImpl()
    val orderRepository = OrderRepositoryImpl()
    
    // Initialize services
    val authService = AuthService(userRepository)
    
    // Configure authentication
    install(Authentication) {
        jwt {
            val secret = System.getenv("JWT_SECRET") ?: "your-secret-key"
            val issuer = "online-marketplace"
            val audience = "users"
            
            verifier(
                JWT
                    .require(Algorithm.HMAC256(secret))
                    .withAudience(audience)
                    .withIssuer(issuer)
                    .build()
            )
            
            validate { credential ->
                if (credential.payload.audience.contains(audience)) {
                    JWTPrincipal(credential.payload)
                } else {
                    null
                }
            }
        }
    }
    
    // Configure CORS
    install(CORS) {
        anyHost()
        allowHeader("Content-Type")
        allowHeader("Authorization")
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
    }
    
    // Configure content negotiation
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
        })
    }
    
    // Configure routing
    routing {
        authRoutes(authService)
        productRoutes(productRepository, authService)
        orderRoutes(orderRepository, productRepository, authService)
    }
    
    // Shutdown hook
    environment.monitor.subscribe(ApplicationStopping) {
        Database.close()
    }
} 