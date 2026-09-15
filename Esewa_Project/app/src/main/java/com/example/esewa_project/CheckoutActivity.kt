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
import com.example.esewa_project.ui.viewmodel.CheckoutViewModel
import com.example.esewa_project.ui.viewmodel.CheckoutViewModelFactory
import com.google.android.libraries.places.api.Places
import android.content.pm.PackageManager
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import com.example.esewa_project.data.model.PaymentMethod
import com.example.esewa_project.data.repository.FavouriteRepository

class CheckoutActivity : ComponentActivity() {
    private lateinit var checkoutViewModel: CheckoutViewModel
    private val addressLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val addressName = result.data?.getStringExtra("selected_address_name")
                addressName?.let {
                    checkoutViewModel.saveDeliveryAddress(it)
                }
            }
        }

    private val confirmationLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                setResult(Activity.RESULT_OK)
                finish()
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
            var selectedPaymentMethod by remember { mutableStateOf(PaymentMethod.COD) }

            if (checkoutItems.isNotEmpty()) {
                CheckoutScreen(
                    items = checkoutItems,
                    checkoutViewModel = checkoutViewModel,
                    selectedPaymentMethod = selectedPaymentMethod,
                    onBackClick = { finish() },
                    onProceedClick = {
                        val intent = Intent(this, ConfirmationActivity::class.java).apply {
                            putExtra("delivery_address", deliveryAddress ?: "Address Not Set")
                            putExtra("payment_method", selectedPaymentMethod.name)
                            putExtra("discount", discount)
                            putExtra("product_id", productId)
                        }
                        confirmationLauncher.launch(intent)
                    },
                    onEditAddressClick = {
                        val intent = Intent(
                            this,
                            ShippingAddressActivity::class.java
                        )
                        addressLauncher.launch(intent)
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