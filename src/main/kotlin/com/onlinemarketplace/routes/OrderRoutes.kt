package com.onlinemarketplace.routes

import com.onlinemarketplace.models.*
import com.onlinemarketplace.repositories.OrderRepository
import com.onlinemarketplace.repositories.ProductRepository
import com.onlinemarketplace.services.AuthService
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.Serializable

@Serializable
data class CreateOrderRequest(
    val items: List<OrderItemRequest>,
    val shippingAddress: Address,
    val paymentMethod: PaymentMethod
)

@Serializable
data class OrderItemRequest(
    val productId: String,
    val quantity: Int,
    val customization: Map<String, String>
)

fun Route.orderRoutes(
    orderRepository: OrderRepository,
    productRepository: ProductRepository,
    authService: AuthService
) {
    route("/orders") {
        authenticate {
            get {
                val userId = call.principal<UserIdPrincipal>()?.name
                    ?: throw IllegalArgumentException("User not authenticated")
                
                val orders = orderRepository.getOrdersByCustomer(userId)
                call.respond(HttpStatusCode.OK, orders)
            }
            
            get("/artisan") {
                val artisanId = call.principal<UserIdPrincipal>()?.name
                    ?: throw IllegalArgumentException("User not authenticated")
                
                val orders = orderRepository.getOrdersByArtisan(artisanId)
                call.respond(HttpStatusCode.OK, orders)
            }
            
            get("/{id}") {
                val userId = call.principal<UserIdPrincipal>()?.name
                    ?: throw IllegalArgumentException("User not authenticated")
                
                val orderId = call.parameters["id"] ?: throw IllegalArgumentException("Order ID is required")
                val order = orderRepository.getOrderById(orderId)
                    ?: throw IllegalArgumentException("Order not found")
                
                if (order.customerId != userId && order.artisanId != userId) {
                    throw IllegalArgumentException("You can only view your own orders")
                }
                
                call.respond(HttpStatusCode.OK, order)
            }
            
            post {
                val customerId = call.principal<UserIdPrincipal>()?.name
                    ?: throw IllegalArgumentException("User not authenticated")
                
                val request = call.receive<CreateOrderRequest>()
                
                // Validate products and calculate total
                var totalAmount = 0.0
                val items = request.items.map { item ->
                    val product = productRepository.getProductById(item.productId)
                        ?: throw IllegalArgumentException("Product not found: ${item.productId}")
                    
                    if (product.stock < item.quantity) {
                        throw IllegalArgumentException("Insufficient stock for product: ${product.name}")
                    }
                    
                    totalAmount += product.price * item.quantity
                    
                    OrderItem(
                        productId = item.productId,
                        quantity = item.quantity,
                        price = product.price,
                        customization = item.customization
                    )
                }
                
                val order = Order(
                    customerId = customerId,
                    artisanId = items.first().let { productRepository.getProductById(it.productId)!!.artisanId },
                    items = items,
                    totalAmount = totalAmount,
                    status = OrderStatus.PENDING,
                    shippingAddress = request.shippingAddress,
                    paymentMethod = request.paymentMethod
                )
                
                val createdOrder = orderRepository.createOrder(order)
                call.respond(HttpStatusCode.Created, createdOrder)
            }
            
            put("/{id}/status") {
                val userId = call.principal<UserIdPrincipal>()?.name
                    ?: throw IllegalArgumentException("User not authenticated")
                
                val orderId = call.parameters["id"] ?: throw IllegalArgumentException("Order ID is required")
                val status = call.receive<OrderStatus>()
                
                val order = orderRepository.getOrderById(orderId)
                    ?: throw IllegalArgumentException("Order not found")
                
                if (order.artisanId != userId) {
                    throw IllegalArgumentException("Only the artisan can update the order status")
                }
                
                val updatedOrder = orderRepository.updateOrderStatus(orderId, status)
                call.respond(HttpStatusCode.OK, updatedOrder)
            }
        }
    }
} 