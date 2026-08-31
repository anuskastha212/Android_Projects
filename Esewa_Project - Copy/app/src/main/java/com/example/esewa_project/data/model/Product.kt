package com.example.esewa_project.data.model

data class Product(
    val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val category: ProductCategory,
    val thumbnail: String,
    val stock: Int,
    val images: List<String>,
    val options: Map<String, List<String>>,
    val rating: Double,
    val reviewCount: Int,
    val discountPercentage: Double ? = null
)