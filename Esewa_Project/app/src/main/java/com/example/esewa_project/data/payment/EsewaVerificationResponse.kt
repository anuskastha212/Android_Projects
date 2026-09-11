package com.example.esewa_project.data.payment

import com.google.gson.annotations.SerializedName

data class EsewaVerificationResponse(
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("product_name")
    val productName: String,
    @SerializedName("total_amount")
    val totalAmount: String,
    @SerializedName("code")
    val code: String,
    @SerializedName("message")
    val message: EsewaMessage,
    @SerializedName("transaction_details")
    val transactionDetails: EsewaTransactionDetails,
    @SerializedName("merchant_name")
    val merchantName: String
)

data class EsewaMessage(
    @SerializedName("technical_success_message")
    val technicalSuccessMessage: String?,
    @SerializedName("success_message")
    val successMessage: String?
)

data class EsewaTransactionDetails(
    val date: String,
    @SerializedName("reference_id")
    val referenceId: String,
    val status: String
)