package com.example.esewa_project.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.esewa_project.data.local.AppDatabase
import com.example.esewa_project.data.local.entity.CartEntity
import com.example.esewa_project.data.repository.CartRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val cartRepo = CartRepository(AppDatabase.getDatabase(application).cartDao())
    private val auth = FirebaseAuth.getInstance()
    private val userId get() = auth.currentUser?.uid ?: ""

    val cartCount: Flow<Int> = cartRepo.getCartCount(userId).map { it ?: 0 }

    val cartQuantities: StateFlow<Map<Int, Int>> = cartRepo.getCartWithProducts(userId).map{itemsMap ->
        itemsMap.keys.associate { it.productId to it.quantity }
    }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun updateQuantity(productId:Int, delta:Int){
        viewModelScope.launch {
            val currentQty = cartQuantities.value[productId] ?:0
            val newQty = currentQty + delta

            if (newQty <=0){
                cartRepo.removeFromCart(userId,productId)
            } else{
                cartRepo.addToCart(CartEntity(userId, productId, newQty))
            }
        }
    }
}