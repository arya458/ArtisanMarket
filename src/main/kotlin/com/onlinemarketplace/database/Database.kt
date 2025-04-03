package com.onlinemarketplace.database

import org.litote.kmongo.coroutine.CoroutineClient
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

object Database {
    private const val DATABASE_NAME = "online_marketplace"
    private const val CONNECTION_STRING = "mongodb://localhost:27017"
    
    private val client: CoroutineClient = KMongo.createClient(CONNECTION_STRING).coroutine
    val database: CoroutineDatabase = client.getDatabase(DATABASE_NAME)
    
    fun close() {
        client.close()
    }
} 