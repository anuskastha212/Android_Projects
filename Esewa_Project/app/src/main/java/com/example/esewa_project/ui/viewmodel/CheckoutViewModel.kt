package com.example.esewa_project.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esewa_project.data.model.CartItem
import com.example.esewa_project.data.model.Order
import com.example.esewa_project.data.model.ShippingAddress
import com.example.esewa_project.data.repository.CartRepository
import com.example.esewa_project.data.repository.FavouriteRepository
import com.example.esewa_project.data.repository.ProductRepository
import com.example.esewa_project.data.repository.UserSessionRepository
import com.example.esewa_project.ui.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CheckoutViewModel(
    private val productRepo: ProductRepository,
    private val cartRepo: CartRepository,
    private val favRepo: FavouriteRepository,
    private val sessionRepo: UserSessionRepository
) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _checkoutState = MutableStateFlow<UiState<List<CartItem>>>(UiState.Loading)
    val checkoutState: StateFlow<UiState<List<CartItem>>> = _checkoutState.asStateFlow()

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
            val snapshot = firestore.collection("users").document(uid)
                .collection("addresses")
                .get()
                .await()
            val addresses = snapshot.toObjects(ShippingAddress::class.java)
            if (addresses.isNotEmpty()) {
                val defaultAddress =
                    addresses.find { it.isDefaultShipping } ?: addresses.first()
                _deliveryAddress.value = defaultAddress.addressLocation
            }
        }
    }

    fun saveDeliveryAddress(addressLocation: String) {
        _deliveryAddress.value = addressLocation
        val uid = sessionRepo.getUid() ?: return
        viewModelScope.launch {
            firestore.collection("users").document(uid)
                .set("address" to addressLocation, SetOptions.merge())
        }
    }

    fun loadCartItems() {
        val uid = sessionRepo.getUid() ?: return
        viewModelScope.launch {
            _checkoutState.value = UiState.Loading
            cartRepo.getCartWithProducts(uid).collect { cartMap ->
                val items = cartMap.map { (cart, prod) ->
                    CartItem(
                        prod.id,
                        prod.title,
                        prod.price,
                        cart.quantity,
                        prod.thumbnail,
                        prod.categoryName
                    )
                }
                _checkoutState.value =
                    if (items.isEmpty()) {
                        UiState.Empty
                    } else {
                        UiState.Success(items)
                    }
            }
        }
    }

    fun loadSingleProduct(productId: Int) {
        viewModelScope.launch {
            _checkoutState.value = UiState.Loading
            val prod = productRepo.getLocalProductById(productId)
            if (prod != null) {
                _checkoutState.value = UiState.Success(
                    listOf(
                        CartItem(
                            prod.id,
                            prod.title,
                            prod.price,
                            1,
                            prod.thumbnail,
                            prod.categoryName
                        )
                    )
                )
            } else _checkoutState.value = UiState.Error("Product not found")
        }
    }

    fun saveOrder(
        orderId: String,
        items: List<CartItem>,
        total: Double,
        address: String,
        paymentMethod: String,
        singleProductId: Int,
        onComplete: () -> Unit
    ) {
        val uid = sessionRepo.getUid() ?: return
        viewModelScope.launch {
            try {
                val order = Order(
                    orderId = orderId,
                    userId = uid,
                    items = items,
                    totalAmount = total,
                    deliveryAddress = address,
                    paymentMethod = paymentMethod
                )
                firestore.collection("users").document(uid).collection("orders")
                    .document(orderId)
                    .set(order)
                    .await()

                if (singleProductId != -1) {
                    cartRepo.removeFromCart(uid, singleProductId)
                    favRepo.removeFavourite(uid, singleProductId)
                } else {
                    cartRepo.clearCart(uid)
                    items.forEach { favRepo.removeFavourite(uid, it.productId) }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                onComplete()
            }
        }
    }
}