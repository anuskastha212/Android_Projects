package com.example.esewa_project.data.repository

import com.example.esewa_project.data.local.dao.CartDao
import com.example.esewa_project.data.local.entity.CartEntity
import com.example.esewa_project.data.local.entity.ProductEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class CartRepository(private val cartDao: CartDao) {
    private val firestore = FirebaseFirestore.getInstance()
    fun getCartWithProducts(userId: String): Flow<Map<CartEntity, ProductEntity>>{
        return cartDao.getCartWithProducts(userId)
    }

    fun getCartCount(userId: String): Flow<Int?> {
        return cartDao.getCartCount(userId)
    }

    suspend fun addToCart(cartItem: CartEntity){
        cartDao.upsertCartItem(cartItem)
        firestore.collection("users").document(cartItem.userId)
            .collection("cart").document(cartItem.productId.toString())
            .set(cartItem).await()
    }

    suspend fun removeFromCart(userId: String, productId: Int) {
        cartDao.removeFromCart(userId, productId)
        firestore.collection("users").document(userId)
            .collection("cart").document(productId.toString())
            .delete().await()
    }

    suspend fun syncCartFromCloud(userId: String) {
        try {
            val snapshot = firestore.collection("users").document(userId).collection("cart").get().await()
            snapshot.toObjects(CartEntity::class.java).forEach { cartDao.upsertCartItem(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun clearCart(userId: String) {
        cartDao.clearCart(userId)
    }
}