package com.example.esewa_project.data.repository

import com.example.esewa_project.data.local.dao.CartDao
import com.example.esewa_project.data.local.entity.CartEntity
import com.example.esewa_project.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {
    fun getCartWithProducts(userId: String): Flow<Map<CartEntity, ProductEntity>>{
        return cartDao.getCartWithProducts(userId)
    }

    fun getCartCount(userId: String): Flow<Int?> {
        return cartDao.getCartCount(userId)
    }

    suspend fun cacheProducts(products:List<ProductEntity>){
        cartDao.insertProducts(products)
    }

    suspend fun addToCart(cartItem: CartEntity){
        cartDao.upsertCartItem(cartItem)
    }

    suspend fun removeFromCart(userId: String, productId: Int) {
        cartDao.removeFromCart(userId, productId)
    }

    suspend fun clearCart(userId: String) {
        cartDao.clearCart(userId)
    }


}