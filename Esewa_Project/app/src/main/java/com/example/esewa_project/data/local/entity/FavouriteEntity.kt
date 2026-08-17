package com.example.esewa_project.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "favourites",
    primaryKeys = ["userId", "productId"]
)
data class FavouriteEntity(
    val userId: String = "",
    val productId: Int = 0,
    val addedAt: Long = System.currentTimeMillis()
)
