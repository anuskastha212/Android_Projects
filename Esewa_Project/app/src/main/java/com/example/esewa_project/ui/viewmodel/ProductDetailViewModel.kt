package com.example.esewa_project.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esewa_project.data.model.Product
import com.example.esewa_project.data.repository.ProductRepository
import com.example.esewa_project.data.repository.ProductSizeColorRepository
import kotlinx.coroutines.launch

class ProductDetailViewModel : ViewModel() {

    private val productRepo = ProductRepository()
    private val sizeColorRepo = ProductSizeColorRepository()

    private val _product = MutableLiveData<Product>()
    val product: LiveData<Product> = _product

    val availableSizes = sizeColorRepo.getProductColors()

    fun loadDetails(id: Int) {
        viewModelScope.launch {
            try {
                val result = productRepo.getProductById(id)
                _product.value = result
            }catch (e: Exception){
                Log.e("ViewModel", "Error: ${e.message}")
            }
        }
    }

}