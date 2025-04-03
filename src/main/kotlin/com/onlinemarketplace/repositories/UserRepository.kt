package com.onlinemarketplace.repositories

import com.onlinemarketplace.database.Database
import com.onlinemarketplace.models.User
import org.litote.kmongo.coroutine.CoroutineCollection
import org.litote.kmongo.eq

interface UserRepository {
    suspend fun createUser(user: User): User
    suspend fun getUserById(id: String): User?
    suspend fun getUserByEmail(email: String): User?
    suspend fun updateUser(user: User): User
    suspend fun deleteUser(id: String): Boolean
}

class UserRepositoryImpl : UserRepository {
    private val collection: CoroutineCollection<User> = Database.database.getCollection()
    
    override suspend fun createUser(user: User): User {
        collection.insertOne(user)
        return user
    }
    
    override suspend fun getUserById(id: String): User? {
        return collection.findOne(User::id eq id)
    }
    
    override suspend fun getUserByEmail(email: String): User? {
        return collection.findOne(User::email eq email)
    }
    
    override suspend fun updateUser(user: User): User {
        collection.updateOne(User::id eq user.id, user)
        return user
    }
    
    override suspend fun deleteUser(id: String): Boolean {
        return collection.deleteOne(User::id eq id).deletedCount > 0
    }
} 