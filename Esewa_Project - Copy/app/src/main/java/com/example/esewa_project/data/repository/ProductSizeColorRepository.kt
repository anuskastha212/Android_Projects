package com.example.esewa_project.data.repository

import com.example.esewa_project.data.api.RetrofitInstance
import com.example.esewa_project.data.source.ColorsData

class ProductSizeColorRepository {
    private val colorsData = ColorsData()
    private val sizesApi = RetrofitInstance.api

    fun getProductColors() = colorsData.getColorData()
    suspend fun getProductSizes() = sizesApi.getProductSizes()
}