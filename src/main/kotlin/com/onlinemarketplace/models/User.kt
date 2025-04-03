package com.onlinemarketplace.models

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class User(
    @BsonId
    val id: String = ObjectId().toString(),
    val email: String,
    val password: String,
    val name: String,
    val role: UserRole,
    val profile: UserProfile? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class UserProfile(
    val bio: String? = null,
    val location: String? = null,
    val socialMedia: Map<String, String> = emptyMap(),
    val profilePicture: String? = null,
    val portfolio: List<String> = emptyList()
)

@Serializable
enum class UserRole {
    ARTISAN,
    CUSTOMER,
    ADMIN
} 