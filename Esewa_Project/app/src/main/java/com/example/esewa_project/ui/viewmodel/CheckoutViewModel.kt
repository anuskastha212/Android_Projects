package com.example.esewa_project.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esewa_project.data.model.CartItem
import com.example.esewa_project.data.model.ShippingAddress
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
    private val _savedAddresses = MutableStateFlow<List<ShippingAddress>>(emptyList())
    val savedAddresses: StateFlow<List<ShippingAddress>> = _savedAddresses.asStateFlow()

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
                firestore.collection("users").document(uid)
                    .collection("addresses")
                    .get()
                    .addOnSuccessListener { snapshot ->
                        val addresses = snapshot.toObjects(ShippingAddress::class.java)
                        if (addresses.isNotEmpty()) {
                            val defaultAddress =
                                addresses.find { it.isDefaultShipping } ?: addresses.first()
                            val syncedAddresses = addresses.map {
                                it.copy(isSelected = it.id == defaultAddress.id)
                            }
                            _savedAddresses.value = syncedAddresses
                            _deliveryAddress.value = defaultAddress.addressLocation
                        }
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addNewAddress(address: ShippingAddress) {
        val uid = sessionRepo.getUid() ?: return
        viewModelScope.launch {
            try {
                firestore.collection("users").document(uid)
                    .collection("addresses").document(address.id)
                    .set(address, SetOptions.merge())
                    .addOnSuccessListener {
                        val currentList = _savedAddresses.value.toMutableList()
                        val existingIndex = currentList.indexOfFirst { it.id == address.id }
                        if (existingIndex != -1) {
                            currentList[existingIndex] = address
                        } else {
                            currentList.add(address)
                        }
                        if (address.isDefaultShipping || currentList.size == 1) {
                            currentList.forEachIndexed { index, item ->
                                currentList[index] = item.copy(isSelected = (item.id == address.id))
                            }
                            _deliveryAddress.value = address.addressLocation
                            saveDeliveryAddress(address.addressLocation)
                        }
                        _savedAddresses.value = currentList
                    }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun selectAddress(address: ShippingAddress) {
        val updatedList = _savedAddresses.value.map {
            it.copy(isSelected = it.id == address.id)
        }
        _savedAddresses.value = updatedList
        _deliveryAddress.value = address.addressLocation
        saveDeliveryAddress(address.addressLocation)
    }

    fun saveDeliveryAddress(addressLocation: String) {
        val uid = sessionRepo.getUid() ?: return
        viewModelScope.launch {
            try {
                val addressMap = mapOf("address" to addressLocation)
                firestore.collection("users").document(uid)
                    .set(addressMap, SetOptions.merge())
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteAddress(addressId: String) {
        val uid = sessionRepo.getUid() ?: return
        viewModelScope.launch {
            try {
                firestore.collection("users").document(uid)
                    .collection("addresses").document(addressId)
                    .delete()
                    .addOnSuccessListener {
                        val currentList = _savedAddresses.value.toMutableList()
                        val removedAddress = currentList.find { it.id == addressId }
                        currentList.removeAll { it.id == addressId }
                        _savedAddresses.value = currentList
                        if (removedAddress?.isSelected == true) {
                            if (currentList.isNotEmpty()) {
                                selectAddress(currentList.first())
                            } else {
                                _deliveryAddress.value = null
                                saveDeliveryAddress("")
                            }
                        }
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