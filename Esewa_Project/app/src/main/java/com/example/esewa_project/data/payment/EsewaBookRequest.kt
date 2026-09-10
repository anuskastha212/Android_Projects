package com.example.esewa_project.data.payment

import com.google.gson.annotations.SerializedName

data class EsewaBookRequest(
    @SerializedName("product_code")
    val productCode: String,
    @SerializedName("amount")
    val amount: Double,
    @SerializedName("transaction_uuid")
    val transactionUuid: String,
    @SerializedName("signed_field_names")
    val signedFieldNames: String,
    @SerializedName("signature")
    val signature: String,
    @SerializedName("callback_url")
    val callbackUrl: String,
    @SerializedName("redirect_url")
    val redirectUrl: String,
    @SerializedName("properties")
    val properties: EsewaProperties? = null
)

data class EsewaProperties(
    @SerializedName("customer_id")
    val customerId: String?,
    @SerializedName("remarks")
    val remarks: String?
)