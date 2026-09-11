package com.example.esewa_project

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.esewa_project.data.local.AppDatabase
import com.example.esewa_project.data.repository.CartRepository
import com.example.esewa_project.data.repository.ProductRepository
import com.example.esewa_project.data.repository.UserSessionRepository
import com.example.esewa_project.ui.compose.CheckoutScreen
import com.example.esewa_project.ui.compose.ConfirmationScreen
import com.example.esewa_project.ui.viewmodel.CheckoutViewModel
import com.example.esewa_project.ui.viewmodel.CheckoutViewModelFactory
import com.google.android.libraries.places.api.Places
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.esewa_project.data.model.ShippingAddress
import com.example.esewa_project.data.repository.FavouriteRepository
import com.example.esewa_project.ui.compose.MapLocation
import com.example.esewa_project.ui.compose.ShippingAddressForm
import com.example.esewa_project.ui.compose.ShippingAddressScreen
import java.util.UUID

enum class CheckoutFlowRoute {
    CHECKOUT,
    CONFIRMATION
}

class CheckoutActivity : ComponentActivity() {
    private lateinit var checkoutViewModel: CheckoutViewModel
    private var currentOrderId: String = ""
    private val paymentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val singleProductId = intent.getIntExtra("product_id", -1)
            checkoutViewModel.markOrderAsComplete(currentOrderId, singleProductId)

            Toast.makeText(
                this,
                "Order placed successfully!",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    private val addressLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val addressName = result.data?.getStringExtra("selected_address_name")
                addressName?.let {
                    checkoutViewModel.saveDeliveryAddress(it)
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!Places.isInitialized()) {
            try {
                val applicationInfo =
                    packageManager.getApplicationInfo(
                        packageName,
                        PackageManager.GET_META_DATA
                    )
                val apiKey =
                    applicationInfo.metaData.getString("com.google.android.geo.API_KEY")

                if (!apiKey.isNullOrEmpty()) {
                    Places.initialize(applicationContext, apiKey)
                }
            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }
        }

        val database = AppDatabase.getDatabase(this)
        val productRepo = ProductRepository(database.productDao())
        val cartRepo = CartRepository(database.cartDao())
        val sessionRepo = UserSessionRepository(this)
        val favRepo = FavouriteRepository(database.favouriteDao())

        val factory =
            CheckoutViewModelFactory(productRepo, cartRepo, favRepo, sessionRepo)
        checkoutViewModel =
            ViewModelProvider(this, factory)[CheckoutViewModel::class.java]

        checkoutViewModel.loadSavedAddress()

        val productId = intent.getIntExtra("product_id", -1)
        if (productId != -1) {
            checkoutViewModel.loadSingleProduct(productId)
        } else {
            checkoutViewModel.loadCartItems()
        }

        setContent {
            val checkoutItems by checkoutViewModel.checkoutItems.collectAsState()
            val discount by checkoutViewModel.promoDiscount.collectAsState()
            val deliveryAddress by checkoutViewModel.deliveryAddress.collectAsState()

            var currentRoute by remember { mutableStateOf(CheckoutFlowRoute.CHECKOUT) }

            if (checkoutItems.isNotEmpty()) {
                when (currentRoute) {
                    CheckoutFlowRoute.CHECKOUT -> {
                        CheckoutScreen(
                            items = checkoutItems,
                            checkoutViewModel = checkoutViewModel,
                            onBackClick = { finish() },
                            onProceedClick = {
                                currentRoute = CheckoutFlowRoute.CONFIRMATION
                            },
                            onEditAddressClick = {
                                val intent = Intent(
                                    this,
                                    ShippingAddressActivity::class.java
                                )
                                addressLauncher.launch(intent)
                            }
                        )
                    }

                    CheckoutFlowRoute.CONFIRMATION -> {
                        ConfirmationScreen(
                            items = checkoutItems,
                            deliveryAddress = deliveryAddress ?: "Address Not Set",
                            discount = discount,
                            onBackClick = { currentRoute = CheckoutFlowRoute.CHECKOUT },
                            onConfirmClick = {
                                val subTotal =
                                    checkoutItems.sumOf { it.price * it.quantity }
                                val shipping = 1.0
                                val grandTotal = (subTotal + shipping) - discount
                                val newOrderId = "ORD_${System.currentTimeMillis()}"
                                currentOrderId = newOrderId

                                checkoutViewModel.createPendingOrder(
                                    newOrderId,
                                    checkoutItems,
                                    grandTotal,
                                    deliveryAddress ?: ""
                                )

                                val intent = Intent(
                                    this@CheckoutActivity,
                                    PaymentActivity::class.java
                                ).apply {
                                    putExtra(
                                        "amount",
                                        String.format(
                                            java.util.Locale.US,
                                            "%.2f",
                                            grandTotal
                                        )
                                    )
                                    putExtra("product_name", "Order from eBazar")
                                    putExtra("product_id", newOrderId)
                                }
                                paymentLauncher.launch(intent)
                            }
                        )
                    }
                }
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