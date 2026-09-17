package com.example.esewa_project.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.esewa_project.data.model.ShippingAddress
import com.example.esewa_project.data.repository.UserSessionRepository
import com.example.esewa_project.ui.util.UiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class ShippingAddressViewModel(
    private val sessionRepo: UserSessionRepository
) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _savedAddresses = MutableStateFlow<List<ShippingAddress>>(emptyList())
    val savedAddresses: StateFlow<List<ShippingAddress>> = _savedAddresses

    private val _addressState = MutableStateFlow<UiState<List<ShippingAddress>>>(UiState.Loading)
    val addressState: StateFlow<UiState<List<ShippingAddress>>> = _addressState.asStateFlow()

    init {
        loadAddresses()
    }

    fun loadAddresses() {
        val uid = sessionRepo.getUid() ?: return
        viewModelScope.launch {
            _addressState.value = UiState.Loading
            try {
                val snapshot = firestore.collection("users").document(uid)
                    .collection("addresses").get().await()
                val list = snapshot.toObjects(ShippingAddress::class.java)

                if (list.isEmpty()) {
                    _addressState.value = UiState.Empty
                } else {
                    _addressState.value = UiState.Success(list)
                }
            } catch (e: Exception) {
                _addressState.value = UiState.Error(e.message ?: "Unknown Error")
            }
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