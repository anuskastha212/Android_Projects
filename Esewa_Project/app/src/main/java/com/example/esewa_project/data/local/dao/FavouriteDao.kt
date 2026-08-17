package com.example.esewa_project.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.esewa_project.data.local.entity.FavouriteEntity
import com.example.esewa_project.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface FavouriteDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavourite(fav: FavouriteEntity)

    @Query("DELETE FROM favourites WHERE userId = :uId AND productId = :pId")
    suspend fun removeFavourite(uId: String, pId: Int)

    @Query("SELECT productId FROM favourites WHERE userId = :userId")
    fun getFavouriteIds(userId: String): Flow<List<Int>>

    @Query("""
    SELECT * FROM favourites 
    INNER JOIN products ON favourites.productId = products.id 
    WHERE userId = :userId
""")
    fun getFavouriteWithProducts(userId: String): Flow<List<ProductEntity>>

    @Query("SELECT COUNT(*) FROM favourites WHERE userId = :userId")
    fun getFavouriteCount(userId: String): Flow<Int>

}