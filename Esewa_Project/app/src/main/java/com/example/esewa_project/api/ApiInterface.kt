package com.example.esewa_project.api

import com.example.esewa_project.data.model.Product
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiInterface {
    @GET("/walmartproducts")
    fun getData(): Response<List<Product>>
}