package com.example.esewa_project.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esewa_project.data.model.CartItem
import com.example.esewa_project.data.repository.CartRepository
import com.example.esewa_project.data.repository.ProductRepository
import com.example.esewa_project.data.repository.UserSessionRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CheckoutViewModel(
    private val productRepo: ProductRepository,
    private val cartRepo: CartRepository,
    private val sessionRepo: UserSessionRepository
) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()
    private val _checkoutItems = MutableStateFlow<List<CartItem>>(emptyList())
    val checkoutItems: StateFlow<List<CartItem>> = _checkoutItems.asStateFlow()
    private val _promoDiscount = MutableStateFlow(0.0)
    val promoDiscount: StateFlow<Double> = _promoDiscount.asStateFlow()
    private val _deliveryAddress = MutableStateFlow<String?>(null)
    val deliveryAddress: StateFlow<String?> = _deliveryAddress.asStateFlow()

    fun applyPromoCode(code: String): Boolean {
        return if (code.trim().equals("eBazar-33", ignoreCase = true)) {
            _promoDiscount.value = 100.0
            true
        } else {
            _promoDiscount.value = 0.0
            false
        }
    }

    fun loadSavedAddress() {
        val uid = sessionRepo.getUid() ?: return
        viewModelScope.launch {
            try {
                firestore.collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        if (document.exists()) {
                            val savedAddr = document.getString("address")
                            _deliveryAddress.value = savedAddr
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveDeliveryAddress(address: String) {
        val uid = sessionRepo.getUid() ?: return
        viewModelScope.launch {
            try {
                val addressMap = mapOf("address" to address)
                firestore.collection("users").document(uid)
                    .set(addressMap, SetOptions.merge())
                    .addOnSuccessListener {
                        _deliveryAddress.value = address
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadCartItems() {
        viewModelScope.launch {
            val userId = sessionRepo.getUid() ?: ""
            if (userId.isNotEmpty()) {
                cartRepo.getCartWithProducts(userId).collect { cartMap ->
                    val items = cartMap.map { (cartEntity, productEntity) ->
                        CartItem(
                            productId = productEntity.id,
                            title = productEntity.title,
                            price = productEntity.price,
                            quantity = cartEntity.quantity,
                            thumbnail = productEntity.thumbnail,
                            categoryName = productEntity.categoryName
                        )
                    }
                    _checkoutItems.value = items
                }
            }
        }
    }

    fun loadSingleProduct(productId: Int) {
        viewModelScope.launch {
            val product = productRepo.getLocalProductById(productId)
            product?.let {
                _checkoutItems.value = listOf(
                    CartItem(
                        productId = it.id,
                        title = it.title,
                        price = it.price,
                        quantity = 1,
                        thumbnail = it.thumbnail,
                        categoryName = it.categoryName
                    )
                )
            }
        }
    }
}