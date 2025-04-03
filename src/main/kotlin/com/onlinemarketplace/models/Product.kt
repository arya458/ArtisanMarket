package com.onlinemarketplace.models

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Product(
    @BsonId
    val id: String = ObjectId().toString(),
    val artisanId: String,
    val name: String,
    val description: String,
    val price: Double,
    val category: ProductCategory,
    val images: List<String>,
    val stock: Int,
    val customizationOptions: List<CustomizationOption> = emptyList(),
    val reviews: List<Review> = emptyList(),
    val rating: Double = 0.0,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class CustomizationOption(
    val name: String,
    val type: CustomizationType,
    val options: List<String>,
    val required: Boolean = false
)

@Serializable
enum class CustomizationType {
    COLOR,
    SIZE,
    TEXT,
    IMAGE
}

@Serializable
data class Review(
    val userId: String,
    val rating: Int,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Serializable
enum class ProductCategory {
    JEWELRY,
    POTTERY,
    TEXTILES,
    ARTWORK,
    WOODWORK,
    METALWORK,
    OTHER
} 