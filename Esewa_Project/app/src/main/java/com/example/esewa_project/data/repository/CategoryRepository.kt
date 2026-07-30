package com.example.esewa_project.data.repository

import com.example.esewa_project.data.api.RetrofitInstance
import com.example.esewa_project.data.source.CategoryData

class CategoryRepository {
    private val categoryData = CategoryData()
    private val categoryApi = RetrofitInstance.api

    fun getCategories() = categoryData.getCategoryData()
    suspend fun getMostPopularCategories() = categoryApi.getMostPopular()
}