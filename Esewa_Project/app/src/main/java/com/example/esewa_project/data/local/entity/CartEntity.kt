package com.example.esewa_project.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "cart_items",
    primaryKeys = ["userId", "productId"]
)
data class CartEntity(
    val userId: String,
    val productId: Int,
    val quantity: Int
)