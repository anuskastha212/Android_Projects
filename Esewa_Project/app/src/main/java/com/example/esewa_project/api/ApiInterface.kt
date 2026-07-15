package com.example.esewa_project.api

import retrofit2.Call
import retrofit2.http.GET

interface ApiInterface {
    @GET("Canon EOS Rebel T100")
    fun getData(): Call<Product>
}