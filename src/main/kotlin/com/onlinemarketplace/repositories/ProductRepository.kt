package com.onlinemarketplace.repositories

import com.mongodb.client.model.Filters.or
import com.onlinemarketplace.database.Database
import com.onlinemarketplace.models.Product
import com.onlinemarketplace.models.ProductCategory
import com.onlinemarketplace.models.Review
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq
import org.litote.kmongo.`in`
import org.litote.kmongo.regex
import kotlin.text.RegexOption

interface ProductRepository {
    suspend fun createProduct(product: Product): Product
    suspend fun getProductById(id: String): Product?
    suspend fun getProductsByArtisan(artisanId: String): List<Product>
    suspend fun getProductsByCategory(category: ProductCategory): List<Product>
    suspend fun searchProducts(query: String): List<Product>
    suspend fun getAllProducts(): List<Product>
    suspend fun updateProduct(product: Product): Product
    suspend fun deleteProduct(id: String): Boolean
    suspend fun addReview(productId: String, review: Review): Product
}

class ProductRepositoryImpl : ProductRepository {
    private val collection: CoroutineCollection<Product> = Database.database.getCollection()
    
    override suspend fun createProduct(product: Product): Product {
        collection.insertOne(product)
        return product
    }
    
    override suspend fun getProductById(id: String): Product? {
        return collection.findOne(Product::id eq id)
    }
    
    override suspend fun getProductsByArtisan(artisanId: String): List<Product> {
        return collection.find(Product::artisanId eq artisanId).toList()
    }
    
    override suspend fun getProductsByCategory(category: ProductCategory): List<Product> {
        return collection.find(Product::category eq category).toList()
    }
    
    override suspend fun searchProducts(query: String): List<Product> {
        val regex = ".*$query.*".toRegex(RegexOption.IGNORE_CASE)
        return collection.find(
            or(
                Product::name regex regex,
                Product::description regex regex
            )
        ).toList()
    }
    
    override suspend fun getAllProducts(): List<Product> {
        return collection.find().toList()
    }
    
    override suspend fun updateProduct(product: Product): Product {
        collection.updateOne(Product::id eq product.id, product)
        return product
    }
    
    override suspend fun deleteProduct(id: String): Boolean {
        return collection.deleteOne(Product::id eq id).deletedCount > 0
    }
    
    override suspend fun addReview(productId: String, review: Review): Product {
        val product = getProductById(productId) ?: throw IllegalArgumentException("Product not found")
        val updatedReviews = product.reviews + review
        val updatedRating = updatedReviews.map { it.rating }.average()
        
        val updatedProduct = product.copy(
            reviews = updatedReviews,
            rating = updatedRating
        )
        
        return updateProduct(updatedProduct)
    }
} 