package com.example.esewa_project.data.repository

import com.example.esewa_project.data.source.BannerImages

class BannerRepository {
    private val bannerImages = BannerImages()

     fun getBanners() = bannerImages.getBannerImages()
}