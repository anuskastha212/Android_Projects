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

    private val REQUEST_CODE_PAYMENT = 101

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
        val esewaPayment = EsewaPayment(amount, name, id, "")

        val intent = Intent(this, EsewaPaymentActivity::class.java)

        intent.putExtra(EsewaConfiguration.ESEWA_CONFIGURATION, eSewaConfiguration)
        intent.putExtra(EsewaPayment.ESEWA_PAYMENT, esewaPayment)

        startActivityForResult(intent, REQUEST_CODE_PAYMENT)
    }

    @Suppress("DEPRECATION")
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?,
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PAYMENT) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val message = data?.getStringExtra(EsewaPayment.EXTRA_RESULT_MESSAGE)
                    Log.d("eSewaSuccess", "Proof of Payment: $message")

                    Toast.makeText(
                        this,
                        "Payment successful!",
                        Toast.LENGTH_LONG
                    ).show()
                    finish()
                }

                Activity.RESULT_CANCELED -> {
                    Toast.makeText(
                        this,
                        "Payment cancelled by user",
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                }

                EsewaPayment.RESULT_EXTRAS_INVALID -> {
                    val message = data?.getStringExtra(EsewaPayment.EXTRA_RESULT_MESSAGE)
                    Log.e("eSewaError", "Invalid Extras: $message")
                    finish()
                }
            }
        }
    }
}