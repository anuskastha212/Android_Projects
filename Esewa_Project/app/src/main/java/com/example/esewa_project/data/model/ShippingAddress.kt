package com.example.esewa_project.data.model

import java.util.UUID

data class ShippingAddress(
    val id: String = UUID.randomUUID().toString(),
    val fullName: String,
    val mobileNumber: String,
    val addressLocation: String,
    val label: String, // "Home", "Office", "Other"
    val isDefaultShipping: Boolean,
    val isDefaultBilling: Boolean,
    val isSelected: Boolean = false
)