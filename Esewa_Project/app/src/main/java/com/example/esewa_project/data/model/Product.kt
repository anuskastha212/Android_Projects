package com.example.esewa_project.data.model

data class Product(
    val id: Int,
    val brand: String,
    val category: String,
    val des: String,
    val image: String,
    val isNew: Boolean,
    val oldPrice: Int,
    val price: Double,
    val title: String
)