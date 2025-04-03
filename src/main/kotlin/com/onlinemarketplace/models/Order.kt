package com.onlinemarketplace.models

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Order(
    @BsonId
    val id: String = ObjectId().toString(),
    val customerId: String,
    val artisanId: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val status: OrderStatus,
    val shippingAddress: Address,
    val paymentMethod: PaymentMethod,
    val trackingNumber: String? = null,
    val estimatedDelivery: Long? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@Serializable
data class OrderItem(
    val productId: String,
    val quantity: Int,
    val price: Double,
    val customization: Map<String, String> = emptyMap()
)

@Serializable
data class Address(
    val street: String,
    val city: String,
    val state: String,
    val country: String,
    val zipCode: String
)

@Serializable
enum class OrderStatus {
    PENDING,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}

@Serializable
enum class PaymentMethod {
    CREDIT_CARD,
    PAYPAL,
    BANK_TRANSFER
} 