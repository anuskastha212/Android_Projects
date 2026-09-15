package com.example.esewa_project.data.model

import com.example.esewa_project.R

enum class PaymentMethod(
    val title: String,
    val iconRes: Int,
    val isEsewa: Boolean = false
) {
    COD("Cash on Delivery", R.drawable.cod),
    ESEWA("Pay with eSewa", R.drawable.esewa_logo, isEsewa = true)
}