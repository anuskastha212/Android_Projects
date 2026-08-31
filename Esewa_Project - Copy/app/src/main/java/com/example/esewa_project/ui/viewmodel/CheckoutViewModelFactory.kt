package com.example.esewa_project.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.esewa_project.data.repository.CartRepository
import com.example.esewa_project.data.repository.ProductRepository
import com.example.esewa_project.data.repository.UserSessionRepository

class CheckoutViewModelFactory(
    private val productRepo: ProductRepository,
    private val cartRepo: CartRepository,
    private val sessionRepo: UserSessionRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CheckoutViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CheckoutViewModel(productRepo, cartRepo, sessionRepo) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}