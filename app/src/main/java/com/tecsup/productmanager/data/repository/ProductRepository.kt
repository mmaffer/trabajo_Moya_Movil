// ProductRepository.kt
package com.tecsup.productmanager.data.repository

import com.tecsup.productmanager.data.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class ProductRepository {

    private val db = FirebaseFirestore.getInstance()
    private val productsCollection = db.collection("products")

    fun getProductsRealTime(userId: String): Flow<List<Product>> = callbackFlow {
        val listener = productsCollection
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(cause = error)
                    return@addSnapshotListener
                }

                val products = snapshot?.documents?.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                } ?: emptyList()

                trySend(element = products)
            }

        awaitClose { listener.remove() }
    }

    suspend fun createProduct(product: Product): Result<String> {
        return try {
            val docRef = productsCollection.add(product).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(productId: String, product: Product): Result<Unit> {
        return try {
            productsCollection.document(productId).set(product).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            productsCollection.document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}