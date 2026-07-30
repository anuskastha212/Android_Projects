package com.example.esewa_project.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esewa_project.data.model.Product
import com.example.esewa_project.data.model.ProductCategory
import com.example.esewa_project.data.repository.BannerRepository
import com.example.esewa_project.data.repository.CategoryRepository
import com.example.esewa_project.data.repository.ProductRepository
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val productRepo = ProductRepository()
    private val categoryRepo = CategoryRepository()
    private val bannerRepo = BannerRepository()

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _popularCategories = MutableLiveData<List<ProductCategory>>()
    val popularCategories : LiveData<List<ProductCategory>> = _popularCategories

    val banners = bannerRepo.getBanners()
    val localCategories = categoryRepo.getCategories()

    fun fetchData(){
        viewModelScope.launch {
            try{
                _products.value = productRepo.getAllProducts()
                val response = categoryRepo.getMostPopularCategories()
                if (response.isSuccessful){
                    _popularCategories.value = response.body()
                }
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }

}