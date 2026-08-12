package com.example.esewa_project.data.repository

import com.example.esewa_project.data.api.RetrofitInstance
import com.example.esewa_project.data.local.dao.ProductDao
import com.example.esewa_project.data.local.entity.ProductEntity

class ProductRepository(private val productDao: ProductDao) {
    private val productApi = RetrofitInstance.api

    suspend fun getAllProducts() = productApi.getAllProducts()
    suspend fun getProductById(id: Int) = productApi.getProductById(id)

    suspend fun cacheProducts(products: List<ProductEntity>) {
        productDao.insertProducts(products)
    }
}