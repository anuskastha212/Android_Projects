package com.example.esewa_project.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.esewa_project.data.local.AppDatabase
import com.example.esewa_project.data.local.entity.CartEntity
import com.example.esewa_project.data.local.entity.ProductEntity
import com.example.esewa_project.data.model.CartItem
import com.example.esewa_project.data.repository.CartRepository
import com.example.esewa_project.data.repository.UserSessionRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val cartRepo = CartRepository(AppDatabase.getDatabase(application).cartDao())
    private val userSessionRepo = UserSessionRepository(application)

    private val _navigateToLogin = MutableSharedFlow<Unit>()
    val navigateToLogin = _navigateToLogin.asSharedFlow()

    val userSession: StateFlow<String> = userSessionRepo.currentUserId
        .map { it ?: "" }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = userSessionRepo.getUid() ?: ""
        )

    val cartCount: Flow<Int> = userSession.flatMapLatest { uid ->
        if (uid.isEmpty()) flowOf(0)
        else cartRepo.getCartCount(uid).map { it ?: 0 }
    }

    val cartItems: Flow<List<CartItem>> = userSession.flatMapLatest { uid ->
        if (uid.isEmpty()) flowOf(emptyList())
        else cartRepo.getCartWithProducts(uid).map { itemsMap ->
            itemsMap.map { (cart, product) ->
                CartItem(
                    productId = product.id,
                    title = product.title,
                    price = product.price,
                    quantity = cart.quantity,
                    thumbnail = product.thumbnail,
                    categoryName = product.categoryName
                )
            }
        }
    }

    val totalAmount: Flow<Double> = cartItems.map { items ->
        items.sumOf { item ->
            item.price * item.quantity
        }
    }

    val cartQuantities: StateFlow<Map<Int, Int>> = cartItems.map { items ->
        items.associate { it.productId to it.quantity }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = emptyMap()
    )

    fun updateQuantity(productId: Int, delta: Int) {
        val currentUid = userSession.value
        if (currentUid.isEmpty()) {
            Log.d("tag", "currentUid is null")
            viewModelScope.launch {
                _navigateToLogin.emit(Unit)
            }
            return
        }
        viewModelScope.launch {
            val currentQty = cartQuantities.value[productId] ?: 0
            val newQty = currentQty + delta

            Log.d("tag", "currentQty $currentQty | newQty $newQty")
            if (newQty <= 0) {
                cartRepo.removeFromCart(currentUid, productId)
            } else {
                cartRepo.addToCart(CartEntity(currentUid, productId, newQty))
            }
        }
    }
}