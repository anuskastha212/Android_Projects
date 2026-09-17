package com.example.esewa_project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModelProvider
import com.example.esewa_project.data.local.AppDatabase
import com.example.esewa_project.data.model.PaymentMethod
import com.example.esewa_project.data.repository.CartRepository
import com.example.esewa_project.data.repository.FavouriteRepository
import com.example.esewa_project.data.repository.ProductRepository
import com.example.esewa_project.data.repository.UserSessionRepository
import com.example.esewa_project.ui.compose.ConfirmationScreen
import com.example.esewa_project.ui.util.UiState
import com.example.esewa_project.ui.viewmodel.CheckoutViewModel
import com.example.esewa_project.ui.viewmodel.CheckoutViewModelFactory
import java.util.Locale

class ConfirmationActivity : ComponentActivity() {
    private lateinit var checkoutViewModel: CheckoutViewModel
    private var pendingOrderId: String = ""
    private var pendingGrandTotal: Double = 0.0
    private var pendingDeliveryAddress: String = ""
    private var pendingPaymentMethodName: String = ""

    private val paymentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val singleProductId = intent.getIntExtra("product_id", -1)
            val currentState = checkoutViewModel.checkoutState.value
            if (currentState is UiState.Success) {
                checkoutViewModel.saveOrder(
                    pendingOrderId,
                    currentState.data,
                    pendingGrandTotal,
                    pendingDeliveryAddress,
                    pendingPaymentMethodName,
                    singleProductId
                ) {
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
            val uiState by checkoutViewModel.checkoutState.collectAsState()

            ConfirmationScreen(
                uiState = uiState,
                deliveryAddress = deliveryAddress,
                paymentMethod = paymentMethod,
                discount = discount,
                onBackClick = { finish() },
                onConfirmClick = { confirmedItems ->
                    val subTotal = confirmedItems.sumOf { it.price * it.quantity }
                    val shipping = 1.0
                    val grandTotal = (subTotal + shipping) - discount
                    val newOrderId = "ORD_${System.currentTimeMillis()}"

                    if (paymentMethod == PaymentMethod.COD) {
                        // CASH ON DELIVERY FLOW
                        checkoutViewModel.saveOrder(
                            orderId = newOrderId,
                            items = confirmedItems,
                            total = grandTotal,
                            address = deliveryAddress,
                            paymentMethod = paymentMethod.title,
                            singleProductId = productId
                        ) {
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
                        pendingOrderId = newOrderId
                        pendingGrandTotal = grandTotal
                        pendingDeliveryAddress = deliveryAddress
                        pendingPaymentMethodName = paymentMethod.title

                        val intent = Intent(this, PaymentActivity::class.java).apply {
                            putExtra("amount", String.format(Locale.US, "%.2f", grandTotal))
                            putExtra("product_name", "Order from eSewa Market")
                            putExtra("product_id", newOrderId)
                        }
                        paymentLauncher.launch(intent)
                    }
                }
            )

        }
    }
}