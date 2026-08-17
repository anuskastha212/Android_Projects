package com.example.esewa_project.data.repository

import com.example.esewa_project.data.local.dao.CartDao
import com.example.esewa_project.data.local.dao.FavouriteDao
import com.example.esewa_project.data.local.entity.CartEntity
import com.example.esewa_project.data.local.entity.FavouriteEntity
import com.example.esewa_project.data.local.entity.ProductEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class FavouriteRepository(private val favouriteDao: FavouriteDao) {
    private val firestore = FirebaseFirestore.getInstance()

    fun getFavouriteIds(userId: String) = favouriteDao.getFavouriteIds(userId)
    fun getFavouriteProducts(userId: String) = favouriteDao.getFavouriteWithProducts(userId)
    fun getFavouriteCount(userId: String) = favouriteDao.getFavouriteCount(userId)

    suspend fun addFavourite(favourite: FavouriteEntity) {
        favouriteDao.addFavourite(favourite)
        firestore.collection("users").document(favourite.userId)
            .collection("favourites").document(favourite.productId.toString())
            .set(favourite).await()
    }

    suspend fun removeFavourite(userId: String, productId: Int) {
        favouriteDao.removeFavourite(userId, productId) // Local
        firestore.collection("users").document(userId)
            .collection("favourites").document(productId.toString())
            .delete().await()
    }

    suspend fun syncFavouritesFromCloud(userId: String) {
        try {
            val snapshot = firestore.collection("users").document(userId).collection("favourites").get().await()
            snapshot.toObjects(FavouriteEntity::class.java).forEach { favouriteDao.addFavourite(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}