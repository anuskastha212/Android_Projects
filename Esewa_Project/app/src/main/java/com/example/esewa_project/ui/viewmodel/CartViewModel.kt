package com.example.esewa_project.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.esewa_project.data.local.AppDatabase
import com.example.esewa_project.data.local.entity.CartEntity
import com.example.esewa_project.data.repository.CartRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalCoroutinesApi::class)
class CartViewModel(application: Application) : AndroidViewModel(application) {
    private val cartRepo = CartRepository(AppDatabase.getDatabase(application).cartDao())
    private val auth = FirebaseAuth.getInstance()

    private val _navigateToLogin = MutableSharedFlow<Unit>()
    val navigateToLogin = _navigateToLogin.asSharedFlow()

    private val userSession: Flow<String> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid ?: ""
            trySend(uid)
        }
        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 1)

    val cartCount: Flow<Int> = userSession.flatMapLatest { uid ->
        if (uid.isEmpty()) flowOf(0)
        else cartRepo.getCartCount(uid).map { it ?: 0 }
    }

    val cartQuantities: StateFlow<Map<Int, Int>> = userSession.flatMapLatest { uid ->
        if (uid.isEmpty()) flowOf(emptyMap())
        else cartRepo.getCartWithProducts(uid).map { itemsMap ->
            itemsMap.keys.associate { it.productId to it.quantity }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyMap())

    fun updateQuantity(productId: Int, delta: Int) {
        val currentUid = auth.currentUser?.uid
        if (currentUid == null) {
            viewModelScope.launch { _navigateToLogin.emit(Unit) }
            return
        }
        viewModelScope.launch {
            val currentQty = cartQuantities.value[productId] ?: 0
            val newQty = currentQty + delta

            if (newQty <= 0) {
                cartRepo.removeFromCart(currentUid, productId)
            } else {
                cartRepo.addToCart(CartEntity(currentUid, productId, newQty))
            }
        }
    }
}