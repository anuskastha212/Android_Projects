package com.example.esewa_project.data.model

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val items: List<CartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val deliveryAddress: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = ""
)
