package com.example.esewa_project.data.model

data class CartItem(
    val productId: Int,
    val title: String,
    val price: Double,
    val quantity: Int,
    val thumbnail: String,
    val categoryName: String
)
