package com.onlinemarketplace.routes

import com.onlinemarketplace.models.CustomizationOption
import com.onlinemarketplace.models.Product
import com.onlinemarketplace.models.ProductCategory
import com.onlinemarketplace.models.Review
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
data class CreateProductRequest(
    val name: String,
    val description: String,
    val price: Double,
    val category: ProductCategory,
    val images: List<String>,
    val stock: Int,
    val customizationOptions: List<CustomizationOption>
)

@Serializable
data class ReviewRequest(
    val rating: Int,
    val comment: String
)

fun Route.productRoutes(productRepository: ProductRepository, authService: AuthService) {
    route("/products") {
        get {
            val category = call.request.queryParameters["category"]?.let { ProductCategory.valueOf(it) }
            val search = call.request.queryParameters["search"]
            
            val products = when {
                category != null -> productRepository.getProductsByCategory(category)
                search != null -> productRepository.searchProducts(search)
                else -> productRepository.getAllProducts()
            }
            
            call.respond(HttpStatusCode.OK, products)
        }
        
        get("/{id}") {
            val id = call.parameters["id"] ?: throw IllegalArgumentException("Product ID is required")
            val product = productRepository.getProductById(id)
                ?: throw IllegalArgumentException("Product not found")
            
            call.respond(HttpStatusCode.OK, product)
        }
        
        authenticate {
            post {
                val artisanId = call.principal<UserIdPrincipal>()?.name
                    ?: throw IllegalArgumentException("User not authenticated")
                
                val request = call.receive<CreateProductRequest>()
                val product = Product(
                    artisanId = artisanId,
                    name = request.name,
                    description = request.description,
                    price = request.price,
                    category = request.category,
                    images = request.images,
                    stock = request.stock,
                    customizationOptions = request.customizationOptions
                )
                
                val createdProduct = productRepository.createProduct(product)
                call.respond(HttpStatusCode.Created, createdProduct)
            }
            
            post("/{id}/reviews") {
                val userId = call.principal<UserIdPrincipal>()?.name
                    ?: throw IllegalArgumentException("User not authenticated")
                
                val productId = call.parameters["id"] ?: throw IllegalArgumentException("Product ID is required")
                val request = call.receive<ReviewRequest>()
                
                val review = Review(
                    userId = userId,
                    rating = request.rating,
                    comment = request.comment
                )
                
                val updatedProduct = productRepository.addReview(productId, review)
                call.respond(HttpStatusCode.OK, updatedProduct)
            }
            
            put("/{id}") {
                val artisanId = call.principal<UserIdPrincipal>()?.name
                    ?: throw IllegalArgumentException("User not authenticated")
                
                val productId = call.parameters["id"] ?: throw IllegalArgumentException("Product ID is required")
                val product = productRepository.getProductById(productId)
                    ?: throw IllegalArgumentException("Product not found")
                
                if (product.artisanId != artisanId) {
                    throw IllegalArgumentException("You can only update your own products")
                }
                
                val request = call.receive<CreateProductRequest>()
                val updatedProduct = product.copy(
                    name = request.name,
                    description = request.description,
                    price = request.price,
                    category = request.category,
                    images = request.images,
                    stock = request.stock,
                    customizationOptions = request.customizationOptions,
                    updatedAt = System.currentTimeMillis()
                )
                
                val savedProduct = productRepository.updateProduct(updatedProduct)
                call.respond(HttpStatusCode.OK, savedProduct)
            }
            
            delete("/{id}") {
                val artisanId = call.principal<UserIdPrincipal>()?.name
                    ?: throw IllegalArgumentException("User not authenticated")
                
                val productId = call.parameters["id"] ?: throw IllegalArgumentException("Product ID is required")
                val product = productRepository.getProductById(productId)
                    ?: throw IllegalArgumentException("Product not found")
                
                if (product.artisanId != artisanId) {
                    throw IllegalArgumentException("You can only delete your own products")
                }
                
                productRepository.deleteProduct(productId)
                call.respond(HttpStatusCode.NoContent)
            }
        }
    }
} 