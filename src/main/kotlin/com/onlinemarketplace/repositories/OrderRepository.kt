package com.onlinemarketplace.repositories

import com.onlinemarketplace.database.Database
import com.onlinemarketplace.models.Order
import com.onlinemarketplace.models.OrderStatus
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq

interface OrderRepository {
    suspend fun createOrder(order: Order): Order
    suspend fun getOrderById(id: String): Order?
    suspend fun getOrdersByCustomer(customerId: String): List<Order>
    suspend fun getOrdersByArtisan(artisanId: String): List<Order>
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Order
    suspend fun updateOrder(order: Order): Order
}

class OrderRepositoryImpl : OrderRepository {
    private val collection: CoroutineCollection<Order> = Database.database.getCollection()
    
    override suspend fun createOrder(order: Order): Order {
        collection.insertOne(order)
        return order
    }
    
    override suspend fun getOrderById(id: String): Order? {
        return collection.findOne(Order::id eq id)
    }
    
    override suspend fun getOrdersByCustomer(customerId: String): List<Order> {
        return collection.find(Order::customerId eq customerId).toList()
    }
    
    override suspend fun getOrdersByArtisan(artisanId: String): List<Order> {
        return collection.find(Order::artisanId eq artisanId).toList()
    }
    
    override suspend fun updateOrderStatus(orderId: String, status: OrderStatus): Order {
        val order = getOrderById(orderId) ?: throw IllegalArgumentException("Order not found")
        val updatedOrder = order.copy(
            status = status,
            updatedAt = System.currentTimeMillis()
        )
        return updateOrder(updatedOrder)
    }
    
    override suspend fun updateOrder(order: Order): Order {
        collection.updateOne(Order::id eq order.id, order)
        return order
    }
} 