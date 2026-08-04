package com.example.esewa_project.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.MutableLiveData
import com.example.esewa_project.data.model.Product
import com.example.esewa_project.data.model.ProductCategory
import com.example.esewa_project.data.repository.BannerRepository
import com.example.esewa_project.data.repository.CategoryRepository
import com.example.esewa_project.data.repository.ProductRepository
import com.example.esewa_project.data.source.LocalDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(application: Application) : AndroidViewModel(application) {
    private val productRepo = ProductRepository()
    private val categoryRepo = CategoryRepository()
    private val bannerRepo = BannerRepository()
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _popularCategories = MutableLiveData<List<ProductCategory>>()
    val popularCategories : LiveData<List<ProductCategory>> = _popularCategories

    private val _cartQuantities = MutableStateFlow<Map<Int, Int>>(emptyMap())
    val  cartQuantities: StateFlow<Map<Int, Int>> = _cartQuantities

    val banners = bannerRepo.getBanners()
    val localCategories = categoryRepo.getCategories()

    private val localDataStore = LocalDataStore(application)
    val cartCount: Flow<Int> = localDataStore.cartCount
    val favouriteCount: Flow<Int> = localDataStore.favouriteCount

    init {
        viewModelScope.launch {
            localDataStore.cartMap.collectLatest { savedMap ->
                _cartQuantities.value = savedMap
            }
        }
    }

    fun updateQuantity(productId:Int, delta:Int){
        val current = _cartQuantities.value.toMutableMap()
        val qty = (current[productId]?:0) + delta
        val newQty = qty.coerceAtLeast(0)
        if (newQty == 0) {
            current.remove(productId)
        } else {
            current[productId] = newQty
        }
        _cartQuantities.value = current
        viewModelScope.launch {
            localDataStore.saveCart(current)
        }
    }

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