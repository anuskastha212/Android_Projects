package com.example.esewa_project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import com.f1soft.esewapaymentsdk.EsewaConfiguration
import com.f1soft.esewapaymentsdk.EsewaPayment
import com.f1soft.esewapaymentsdk.ui.screens.EsewaPaymentActivity

class PaymentActivity : ComponentActivity() {

    private val requestCodePayment = 101

    private val eSewaConfiguration = EsewaConfiguration(
        clientId = BuildConfig.ESEWA_CLIENT_ID,
        secretKey = BuildConfig.ESEWA_SECRET_KEY,
        environment = EsewaConfiguration.ENVIRONMENT_TEST
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val amount = intent.getStringExtra("amount") ?: "0"
        val productName = intent.getStringExtra("product_name") ?: "Order Payment"
        val productId = intent.getStringExtra("product_id") ?: System.currentTimeMillis().toString()

        initiatePayment(amount, productName, productId)
    }

    private fun initiatePayment(amount: String, name: String, id: String) {
        val esewaPayment = EsewaPayment(amount, name, id, "https://www.google.com")

        val intent = Intent(this, EsewaPaymentActivity::class.java)

        intent.putExtra(EsewaConfiguration.ESEWA_CONFIGURATION, eSewaConfiguration)
        intent.putExtra(EsewaPayment.ESEWA_PAYMENT, esewaPayment)

        @Suppress("DEPRECATION")
        startActivityForResult(intent, requestCodePayment)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == requestCodePayment) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val message = data?.getStringExtra(EsewaPayment.EXTRA_RESULT_MESSAGE)
                    Log.d("eSewaSuccess", "Proof of Payment: $message")
                    setResult(Activity.RESULT_OK)
                }

                Activity.RESULT_CANCELED -> {
                    Toast.makeText(
                        this,
                        "Payment cancelled",
                        Toast.LENGTH_SHORT
                    ).show()
                    setResult(Activity.RESULT_CANCELED)
                }

                else -> {
                    val message = data?.getStringExtra(EsewaPayment.EXTRA_RESULT_MESSAGE)
                    Log.e("eSewaError", "Payment Error: $message")
                    setResult(Activity.RESULT_CANCELED)
                }
            }
            finish()
        }
    }
}

//10:54:26.730  D  Proof of Payment: {"productId":"ORD_1789103357407","productName":"EPAYTEST","totalAmount":"2.29","environment":"test","code":"00","merchantName":"EPAYTEST","message":{"technicalSuccessMessage":"Your transaction has been completed.","successMessage":"Your transaction has been completed."},"transactionDetails":{"status":"COMPLETE","referenceId":"000H2AH","date":"Fri Sep 11 10:54:27 NPT 2026"}}
