package com.example.esewa_project.data.repository

import com.example.esewa_project.data.api.RetrofitInstance

class ProductRepository {
    private val productApi = RetrofitInstance.api

    suspend fun getAllProducts() = productApi.getAllProducts()
    suspend fun getProductById(id: Int) = productApi.getProductById(id)
}