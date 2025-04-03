package com.onlinemarketplace.services

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.onlinemarketplace.models.User
import com.onlinemarketplace.models.UserRole
import com.onlinemarketplace.repositories.UserRepository
import org.mindrot.jbcrypt.BCrypt
import java.util.*

class AuthService(val userRepository: UserRepository) {
    private val secret = System.getenv("JWT_SECRET") ?: "your-secret-key"
    private val algorithm = Algorithm.HMAC256(secret)
    
    suspend fun registerUser(email: String, password: String, name: String, role: UserRole): User {
        if (userRepository.getUserByEmail(email) != null) {
            throw IllegalArgumentException("User with this email already exists")
        }
        
        val hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt())
        val user = User(
            email = email,
            password = hashedPassword,
            name = name,
            role = role
        )
        
        return userRepository.createUser(user)
    }
    
    suspend fun login(email: String, password: String): String {
        val user = userRepository.getUserByEmail(email)
            ?: throw IllegalArgumentException("Invalid email or password")
            
        if (!BCrypt.checkpw(password, user.password)) {
            throw IllegalArgumentException("Invalid email or password")
        }
        
        return generateToken(user)
    }
    
    private fun generateToken(user: User): String {
        return JWT.create()
            .withSubject(user.id)
            .withClaim("email", user.email)
            .withClaim("role", user.role.name)
            .withExpiresAt(Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000)) // 24 hours
            .sign(algorithm)
    }
    
    fun validateToken(token: String): String? {
        return try {
            val verifier = JWT.require(algorithm).build()
            val decoded = verifier.verify(token)
            decoded.subject
        } catch (e: Exception) {
            null
        }
    }
} 