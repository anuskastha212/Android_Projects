package com.example.esewa_project.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.esewa_project.data.local.AppDatabase
import com.example.esewa_project.data.model.Product
import com.example.esewa_project.data.repository.ProductRepository
import com.example.esewa_project.data.repository.ProductSizeColorRepository
import kotlinx.coroutines.launch

class ProductDetailViewModel (application: Application) : AndroidViewModel(application) {

    private val productRepo = ProductRepository(AppDatabase.getDatabase(application).productDao())
    private val sizeColorRepo = ProductSizeColorRepository()

    private val _product = MutableLiveData<Product>()
    val product: LiveData<Product> = _product

    val availableColors = sizeColorRepo.getProductColors()

    fun loadDetails(id: Int) {
        viewModelScope.launch {
            try {
                val result = productRepo.getProductById(id)
                _product.value = result
                productRepo.cacheProducts(listOf(
                    com.example.esewa_project.data.local.entity.ProductEntity(
                        result.id, result.title, result.price, result.thumbnail, result.category.name
                    )
                ))
            }catch (e: Exception){
                Log.e("ViewModel", "Error: ${e.message}")
            }
        }
    }
}