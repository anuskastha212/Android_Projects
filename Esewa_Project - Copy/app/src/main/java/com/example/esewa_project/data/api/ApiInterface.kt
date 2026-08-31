package com.example.esewa_project.data.api

import com.example.esewa_project.data.model.PageResponse
import com.example.esewa_project.data.model.Product
import com.example.esewa_project.data.model.ProductCategory
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiInterface {
    @GET("products")
    suspend fun getAllProducts(): List<Product>

    @GET("categories")
    suspend fun getMostPopular(): Response<List<ProductCategory>>

    @GET("sizes")
    suspend fun getProductSizes(): Response<List<PageResponse<String>>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: Int): Product
}