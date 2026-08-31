package com.example.esewa_project.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.esewa_project.data.local.entity.CartEntity
import com.example.esewa_project.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface CartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertCartItem(cartItem: CartEntity)

    @Query("""
        SELECT * FROM cart_items
        INNER JOIN products ON cart_items.productId = products.id 
        WHERE userId = :userId
    """)
    fun getCartWithProducts(userId: String): Flow<Map<CartEntity, ProductEntity>>

    @Query("DELETE FROM cart_items WHERE userId = :uId")
    suspend fun clearCart(uId: String)

    @Query("DELETE FROM cart_items WHERE userId = :uId AND productId = :pId")
    suspend fun removeFromCart(uId: String, pId: Int)

    @Query("SELECT SUM(quantity) FROM cart_items WHERE userId = :userId")
    fun getCartCount(userId: String): Flow<Int?>


}