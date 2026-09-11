package com.example.esewa_project.data.model

data class AddressFormState(
    val address: ShippingAddress = ShippingAddress(),

    val nameError: String? = null,
    val mobileError: String? = null,
    val addressError: String? = null,
    val isSaving: Boolean = false
)