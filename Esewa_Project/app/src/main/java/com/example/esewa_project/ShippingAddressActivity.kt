package com.example.esewa_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import com.example.esewa_project.data.local.AppDatabase
import com.example.esewa_project.data.repository.CartRepository
import com.example.esewa_project.data.repository.ProductRepository
import com.example.esewa_project.data.repository.UserSessionRepository
import com.example.esewa_project.ui.compose.ShippingAddressForm
import com.example.esewa_project.ui.compose.MapLocation
import com.example.esewa_project.ui.compose.ShippingAddressScreen
import com.example.esewa_project.ui.compose.getReadableAddress
import com.example.esewa_project.ui.viewmodel.CheckoutViewModel
import com.example.esewa_project.ui.viewmodel.CheckoutViewModelFactory
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

enum class AddressRoute {
    LIST,
    ADD_NEW,
    MAP_PICKER
}

class ShippingAddressActivity : ComponentActivity() {

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

        checkoutViewModel.loadSavedAddress()

        setContent {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()

            val savedAddresses by checkoutViewModel.savedAddresses.collectAsState()

            var currentRoute by remember { mutableStateOf(AddressRoute.LIST) }
            var pickedAddressForForm by remember { mutableStateOf("") }

            when (currentRoute) {
                AddressRoute.LIST -> {
                    ShippingAddressScreen(
                        addresses = savedAddresses,
                        onBackClick = { finish() },
                        onAddAddressClick = {
                            currentRoute = AddressRoute.ADD_NEW
                        },
                        onAddressSelected = { address ->
                            checkoutViewModel.selectAddress(address)
                            finish()
                        }
                    )
                }

                AddressRoute.ADD_NEW -> {
                    ShippingAddressForm(
                        pickedAddressLocation = pickedAddressForForm,
                        onOpenMapPick = {
                            currentRoute = AddressRoute.MAP_PICKER
                        },
                        onSave = { newAddress ->
                            checkoutViewModel.addNewAddress(newAddress)
                            currentRoute = AddressRoute.LIST
                        },
                        onClose = {
                            currentRoute = AddressRoute.LIST
                        }
                    )
                }

                AddressRoute.MAP_PICKER -> {
                    MapLocation(
                        onLocationConfirmed = { lat, lng ->
                            coroutineScope.launch {
                                pickedAddressForForm = getReadableAddress(context, lat, lng)
                                currentRoute = AddressRoute.ADD_NEW
                            }
                        },
                        onClose = {
                            currentRoute = AddressRoute.ADD_NEW
                        }
                    )
                }
            }
        }
    }
}