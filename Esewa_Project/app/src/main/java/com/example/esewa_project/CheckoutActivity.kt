package com.example.esewa_project

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.esewa_project.data.model.ShippingAddress
import com.example.esewa_project.ui.compose.MapLocation
import com.example.esewa_project.ui.compose.ShippingAddressForm
import com.example.esewa_project.ui.compose.ShippingAddressScreen
import com.example.esewa_project.ui.compose.getReadableAddress
import kotlinx.coroutines.launch
import java.util.UUID

enum class CheckoutFlowRoute {
    CHECKOUT,
    SHIPPING_ADDRESS_LIST,
    ADD_NEW_ADDRESS,
    MAP_PICKER,
    CONFIRMATION
}

class CheckoutActivity : ComponentActivity() {
    private lateinit var checkoutViewModel: CheckoutViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (!Places.isInitialized()) {
            try {
                val applicationInfo =
                    packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
                val apiKey = applicationInfo.metaData.getString("com.google.android.geo.API_KEY")

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

        val factory = CheckoutViewModelFactory(productRepo, cartRepo, sessionRepo)
        checkoutViewModel = ViewModelProvider(this, factory)[CheckoutViewModel::class.java]

        checkoutViewModel.loadSavedAddress()

        val productId = intent.getIntExtra("product_id", -1)
        if (productId != -1) {
            checkoutViewModel.loadSingleProduct(productId)
        } else {
            checkoutViewModel.loadCartItems()
        }

        setContent {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()
            val checkoutItems by checkoutViewModel.checkoutItems.collectAsState()
            val discount by checkoutViewModel.promoDiscount.collectAsState()
            val savedAddresses by checkoutViewModel.savedAddresses.collectAsState()

            var currentRoute by remember { mutableStateOf(CheckoutFlowRoute.CHECKOUT) }

            var editingAddressId by remember { mutableStateOf<String?>(null) }
            var formFullName by remember { mutableStateOf("") }
            var formMobile by remember { mutableStateOf("") }
            var formAddressLocation by remember { mutableStateOf("") }
            var formLabel by remember { mutableStateOf("Home") }
            var formIsDefaultShipping by remember { mutableStateOf(true) }
            var formIsDefaultBilling by remember { mutableStateOf(false) }


            if (checkoutItems.isNotEmpty()) {
                when (currentRoute) {
                    CheckoutFlowRoute.CHECKOUT -> {
                        CheckoutScreen(
                            items = checkoutItems,
                            checkoutViewModel = checkoutViewModel,
                            onBackClick = { finish() },
                            onProceedClick = { currentRoute = CheckoutFlowRoute.CONFIRMATION },
                            onEditAddressClick = {
                                currentRoute = CheckoutFlowRoute.SHIPPING_ADDRESS_LIST
                            }
                        )
                    }

                    CheckoutFlowRoute.CONFIRMATION -> {
                        ConfirmationScreen(
                            items = checkoutItems,
                            discount = discount,
                            onBackClick = { currentRoute = CheckoutFlowRoute.CHECKOUT },
                            onConfirmClick = {
                                Toast.makeText(
                                    this@CheckoutActivity,
                                    "Order Confirmed Successfully!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                finish()
                            }
                        )
                    }

                    CheckoutFlowRoute.SHIPPING_ADDRESS_LIST -> {
                        ShippingAddressScreen(
                            addresses = savedAddresses,
                            onBackClick = { currentRoute = CheckoutFlowRoute.CHECKOUT },
                            onAddAddressClick = {
                                editingAddressId = null
                                formFullName = ""
                                formMobile = ""
                                formAddressLocation = ""
                                formLabel = "Home"
                                formIsDefaultShipping = true
                                formIsDefaultBilling = false
                                currentRoute = CheckoutFlowRoute.ADD_NEW_ADDRESS
                            },
                            onAddressSelected = { address ->
                                checkoutViewModel.selectAddress(address)
                                currentRoute = CheckoutFlowRoute.CHECKOUT
                            },
                            onEdit = { address ->
                                editingAddressId = address.id
                                formFullName = address.fullName
                                formMobile = address.mobileNumber
                                formAddressLocation = address.addressLocation
                                formLabel = address.label
                                formIsDefaultShipping = address.isDefaultShipping
                                formIsDefaultBilling = address.isDefaultBilling
                                currentRoute = CheckoutFlowRoute.ADD_NEW_ADDRESS
                            },
                            onDelete = { address ->
                                checkoutViewModel.deleteAddress(address.id)
                            },
                            onUndoDelete = { address ->
                                checkoutViewModel.addNewAddress(address)
                            }
                        )
                    }

                    CheckoutFlowRoute.ADD_NEW_ADDRESS -> {
                        ShippingAddressForm(
                            fullName = formFullName,
                            onFullNameChange = { formFullName = it },
                            mobileNumber = formMobile,
                            onMobileChange = { formMobile = it },
                            pickedAddressLocation = formAddressLocation,
                            selectedLabel = formLabel,
                            onLabelChange = { formLabel = it },
                            isDefaultShipping = formIsDefaultShipping,
                            onDefaultShippingChange = { formIsDefaultShipping = it },
                            isDefaultBilling = formIsDefaultBilling,
                            onDefaultBillingChange = { formIsDefaultBilling = it },
                            onOpenMapPick = { currentRoute = CheckoutFlowRoute.MAP_PICKER },
                            onSave = {
                                val newAddress = ShippingAddress(
                                    id = editingAddressId ?: UUID.randomUUID().toString(),
                                    fullName = formFullName,
                                    mobileNumber = formMobile,
                                    addressLocation = formAddressLocation,
                                    label = formLabel,
                                    isDefaultShipping = formIsDefaultShipping,
                                    isDefaultBilling = formIsDefaultBilling
                                )
                                checkoutViewModel.addNewAddress(newAddress)

                                formFullName = ""
                                formMobile = ""
                                formAddressLocation = ""
                                formLabel = "Home"
                                editingAddressId = null

                                currentRoute = CheckoutFlowRoute.SHIPPING_ADDRESS_LIST
                            },
                            onClose = {
                                formFullName = ""
                                formMobile = ""
                                formAddressLocation = ""
                                formLabel = "Home"
                                editingAddressId = null
                                currentRoute = CheckoutFlowRoute.SHIPPING_ADDRESS_LIST
                            }
                        )
                    }

                    CheckoutFlowRoute.MAP_PICKER -> {
                        MapLocation(
                            onLocationConfirmed = { lat, lng ->
                                coroutineScope.launch {
                                    formAddressLocation = getReadableAddress(context, lat, lng)
                                    currentRoute = CheckoutFlowRoute.ADD_NEW_ADDRESS
                                }
                            },
                            onClose = {
                                currentRoute = CheckoutFlowRoute.ADD_NEW_ADDRESS
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