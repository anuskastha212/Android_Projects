package com.example.esewa_project.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://fakestoreapi.noksha.dev/api/walmartproducts/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val apiInterface by lazy {
        retrofit.create((ApiInterface::class.java))
    }
}