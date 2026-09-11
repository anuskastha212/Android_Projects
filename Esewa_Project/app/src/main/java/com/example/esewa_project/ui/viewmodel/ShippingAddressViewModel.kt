package com.example.esewa_project.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esewa_project.data.model.ShippingAddress
import com.example.esewa_project.data.repository.UserSessionRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ShippingAddressViewModel(
    private val sessionRepo: UserSessionRepository
) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _savedAddresses = MutableStateFlow<List<ShippingAddress>>(emptyList())
    val savedAddresses: StateFlow<List<ShippingAddress>> = _savedAddresses

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        loadAddresses()
    }

    fun loadAddresses() {
        val uid = sessionRepo.getUid() ?: return
        _isLoading.value = true
        viewModelScope.launch {
            firestore.collection("users").document(uid).collection("addresses").get()
                .addOnSuccessListener { snapshot ->
                    _savedAddresses.value = snapshot.toObjects(ShippingAddress::class.java)
                    _isLoading.value = false
                }
                .addOnFailureListener { _isLoading.value = false }
        }
    }

    fun saveAddress(address: ShippingAddress, onComplete: () -> Unit) {
        val uid = sessionRepo.getUid() ?: return
        firestore.collection("users").document(uid).collection("addresses")
            .document(address.id).set(address, SetOptions.merge())
            .addOnCompleteListener {
                loadAddresses()
                onComplete()
            }
    }

    fun deleteAddress(addressId: String) {
        val uid = sessionRepo.getUid() ?: return
        firestore.collection("users").document(uid).collection("addresses")
            .document(addressId).delete()
            .addOnSuccessListener { loadAddresses() }
    }
}