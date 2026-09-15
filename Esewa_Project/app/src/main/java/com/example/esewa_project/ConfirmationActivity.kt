package com.example.esewa_project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModelProvider
import com.example.esewa_project.data.local.AppDatabase
import com.example.esewa_project.data.model.PaymentMethod
import com.example.esewa_project.data.repository.CartRepository
import com.example.esewa_project.data.repository.FavouriteRepository
import com.example.esewa_project.data.repository.ProductRepository
import com.example.esewa_project.data.repository.UserSessionRepository
import com.example.esewa_project.ui.compose.ConfirmationScreen
import com.example.esewa_project.ui.viewmodel.CheckoutViewModel
import com.example.esewa_project.ui.viewmodel.CheckoutViewModelFactory
import java.util.Locale

class ConfirmationActivity : ComponentActivity() {
    private lateinit var checkoutViewModel: CheckoutViewModel
    private var currentOrderId: String = ""

    private val paymentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val singleProductId = intent.getIntExtra("product_id", -1)
            checkoutViewModel.markOrderAsComplete(currentOrderId, singleProductId) {
                Toast.makeText(
                    this,
                    "Payment successful!",
                    Toast.LENGTH_LONG
                ).show()
                setResult(Activity.RESULT_OK)
                finish()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val productRepo = ProductRepository(database.productDao())
        val cartRepo = CartRepository(database.cartDao())
        val sessionRepo = UserSessionRepository(this)
        val favRepo = FavouriteRepository(database.favouriteDao())

        val factory =
            CheckoutViewModelFactory(productRepo, cartRepo, favRepo, sessionRepo)
        checkoutViewModel =
            ViewModelProvider(this, factory)[CheckoutViewModel::class.java]

        val productId = intent.getIntExtra("product_id", -1)
        if (productId != -1) {
            checkoutViewModel.loadSingleProduct(productId)
        } else {
            checkoutViewModel.loadCartItems()
        }

        val deliveryAddress = intent.getStringExtra("delivery_address") ?: "Address Not Set"
        val paymentMethodName = intent.getStringExtra("payment_method") ?: PaymentMethod.COD.name
        val paymentMethod = try {
            PaymentMethod.valueOf(paymentMethodName)
        } catch (e: Exception) {
            PaymentMethod.COD
        }
        val discount = intent.getDoubleExtra("discount", 0.0)

        setContent {
            val checkoutItems by checkoutViewModel.checkoutItems.collectAsState()

            if (checkoutItems.isNotEmpty()) {
                ConfirmationScreen(
                    items = checkoutItems,
                    deliveryAddress = deliveryAddress,
                    paymentMethod = paymentMethod,
                    discount = discount,
                    onBackClick = { finish() },
                    onConfirmClick = {
                        val subTotal = checkoutItems.sumOf { it.price * it.quantity }
                        val shipping = 1.0
                        val grandTotal = (subTotal + shipping) - discount
                        val newOrderId = "ORD_${System.currentTimeMillis()}"
                        currentOrderId = newOrderId

                        if (paymentMethod == PaymentMethod.COD) {
                            // CASH ON DELIVERY FLOW
                            checkoutViewModel.createPendingOrder(
                                newOrderId, checkoutItems, grandTotal, deliveryAddress
                            )
                            checkoutViewModel.markOrderAsComplete(newOrderId, productId) {
                                Toast.makeText(
                                    this,
                                    "Order placed successfully!",
                                    Toast.LENGTH_LONG
                                ).show()
                                setResult(Activity.RESULT_OK)
                                finish()
                            }
                        } else {
                            // ESEWA FLOW
                            checkoutViewModel.createPendingOrder(
                                newOrderId, checkoutItems, grandTotal, deliveryAddress
                            )
                            val intent = Intent(this, PaymentActivity::class.java).apply {
                                putExtra("amount", String.format(Locale.US, "%.2f", grandTotal))
                                putExtra("product_name", "Order from eSewa Market")
                                putExtra("product_id", newOrderId)
                            }
                            paymentLauncher.launch(intent)
                        }
                    }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF2ABB00))
                }
            }
        }
    }

}