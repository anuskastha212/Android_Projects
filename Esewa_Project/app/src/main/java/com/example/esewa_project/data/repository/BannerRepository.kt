package com.example.esewa_project.data.repository

import com.example.esewa_project.data.api.RetrofitInstance
import com.example.esewa_project.data.source.BannerImages
import com.example.esewa_project.data.source.CategoryData

class BannerRepository {
    private val bannerImages = BannerImages()

     fun getBanners() = bannerImages.getBannerImages()
}