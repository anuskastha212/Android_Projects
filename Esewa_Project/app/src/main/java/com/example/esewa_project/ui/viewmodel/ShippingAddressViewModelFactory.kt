package com.example.esewa_project.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.esewa_project.data.repository.UserSessionRepository

class ShippingAddressViewModelFactory(
    private val sessionRepo: UserSessionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ShippingAddressViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ShippingAddressViewModel(sessionRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}