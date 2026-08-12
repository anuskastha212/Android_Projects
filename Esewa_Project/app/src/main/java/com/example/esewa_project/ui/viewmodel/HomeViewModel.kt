package com.example.esewa_project.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import com.example.esewa_project.data.local.AppDatabase
import com.example.esewa_project.data.model.Product
import com.example.esewa_project.data.model.ProductCategory
import com.example.esewa_project.data.repository.BannerRepository
import com.example.esewa_project.data.repository.CartRepository
import com.example.esewa_project.data.repository.CategoryRepository
import com.example.esewa_project.data.repository.ProductRepository
import com.example.esewa_project.data.local.entity.ProductEntity
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val productRepo = ProductRepository(AppDatabase.getDatabase(application).productDao())
    private val categoryRepo = CategoryRepository()
    private val bannerRepo = BannerRepository()
    private val cartRepo = CartRepository(AppDatabase.getDatabase(application).cartDao())
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products
    private val _popularCategories = MutableLiveData<List<ProductCategory>>()
    val popularCategories : LiveData<List<ProductCategory>> = _popularCategories
    val banners = bannerRepo.getBanners()
    val localCategories = categoryRepo.getCategories()

    fun fetchData(){
        viewModelScope.launch {
            try{
                val apiProducts = productRepo.getAllProducts()
                _products.value = apiProducts

                productRepo.cacheProducts(apiProducts.map {
                    ProductEntity(it.id, it.title, it.price, it.thumbnail, it.category.name)
                })

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