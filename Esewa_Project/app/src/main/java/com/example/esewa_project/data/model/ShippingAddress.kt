package com.example.esewa_project.data.model

import java.util.UUID

data class ShippingAddress(
    val id: String = UUID.randomUUID().toString(),
    val fullName: String = "",
    val mobileNumber: String = "",
    val addressLocation: String = "",
    val label: String = "Home",
    val isDefaultShipping: Boolean = false,
    val isDefaultBilling: Boolean = false,
    var isSelected: Boolean = false
)