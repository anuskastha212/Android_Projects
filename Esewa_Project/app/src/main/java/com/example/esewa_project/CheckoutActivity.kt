package com.example.esewa_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.example.esewa_project.data.local.AppDatabase
import com.example.esewa_project.data.repository.CartRepository
import com.example.esewa_project.data.repository.ProductRepository
import com.example.esewa_project.data.repository.UserSessionRepository
import com.example.esewa_project.ui.compose.CheckoutScreen
import com.example.esewa_project.ui.compose.ConfirmationScreen
import com.example.esewa_project.ui.viewmodel.CheckoutViewModel
import com.example.esewa_project.ui.viewmodel.CheckoutViewModelFactory

class CheckoutActivity : ComponentActivity() {

    private lateinit var checkoutViewModel: CheckoutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(this)
        val productRepo = ProductRepository(database.productDao())
        val cartRepo = CartRepository(database.cartDao())
        val sessionRepo = UserSessionRepository(this)

        val factory = CheckoutViewModelFactory(productRepo, cartRepo, sessionRepo)
        checkoutViewModel = ViewModelProvider(this, factory)[CheckoutViewModel::class.java]
        val productId = intent.getIntExtra("product_id", -1)

        if (productId != -1) {
            checkoutViewModel.loadSingleProduct(productId)
        } else {
            checkoutViewModel.loadCartItems()
        }

        setContent {
            val checkoutItems by checkoutViewModel.checkoutItems.collectAsState()
            val discount by checkoutViewModel.promoDiscount.collectAsState()
            var isConfirmationScreen by remember { mutableStateOf(false) }

            if (checkoutItems.isNotEmpty()) {
                if (isConfirmationScreen) {
                    ConfirmationScreen(
                        items = checkoutItems,
                        discount = discount,
                        onBackClick = { isConfirmationScreen = false },
                        onConfirmClick = { }
                    )
                } else {
                    CheckoutScreen(
                        items = checkoutItems,
                        checkoutViewModel = checkoutViewModel,
                        onBackClick = { finish() },
                        onProceedClick = { isConfirmationScreen = true }
                    )
                }
            }
        }
    }
}