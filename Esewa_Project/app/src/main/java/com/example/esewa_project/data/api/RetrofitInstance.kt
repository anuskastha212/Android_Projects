package com.example.esewa_project.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {

    private const val DUMMY_BASE_URL = "https://dummyjson.com/"
    private const val PRODUCT_BASE_URL = "http://10.19.16.53:8080/"

    val api: ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(DUMMY_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }

    val productApi: ApiInterface by lazy {
        Retrofit.Builder()
            .baseUrl(PRODUCT_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiInterface::class.java)
    }
}