package com.example.esewa_project.data.api

import com.example.esewa_project.data.model.MostPopular
import com.example.esewa_project.data.model.ProductResponse
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET

interface ApiInterface {
    @GET("api/walmartproducts/")
    suspend fun getData(): Response<ProductResponse>

    @GET("products/categories")
    suspend fun getMostPopular(): Response<List<MostPopular>>
}