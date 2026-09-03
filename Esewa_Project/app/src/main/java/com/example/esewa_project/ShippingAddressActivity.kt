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
import com.example.esewa_project.data.model.ShippingAddress
import java.util.UUID

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
            var formAddressLocation by remember { mutableStateOf("") }
            var formFullName by remember { mutableStateOf("") }
            var formMobile by remember { mutableStateOf("") }
            var formLabel by remember { mutableStateOf("Home") }
            var formIsDefaultShipping by remember { mutableStateOf(true) }
            var formIsDefaultBilling by remember { mutableStateOf(false) }
            var editingAddressId by remember { mutableStateOf<String?>(null) }

            when (currentRoute) {
                AddressRoute.LIST -> {
                    ShippingAddressScreen(
                        addresses = savedAddresses,
                        onBackClick = { finish() },
                        onAddAddressClick = {
                            editingAddressId = null
                            formFullName = ""
                            formMobile = ""
                            formAddressLocation = ""
                            formLabel = "Home"
                            formIsDefaultShipping = true
                            formIsDefaultBilling = false
                            currentRoute = AddressRoute.ADD_NEW
                        },
                        onAddressSelected = { address ->
                            checkoutViewModel.selectAddress(address)
                            finish()
                        },
                        onEdit = { address ->
                            editingAddressId = address.id
                            formFullName = address.fullName
                            formMobile = address.mobileNumber
                            formAddressLocation = address.addressLocation
                            formLabel = address.label
                            formIsDefaultShipping = address.isDefaultShipping
                            formIsDefaultBilling = address.isDefaultBilling
                            currentRoute = AddressRoute.ADD_NEW
                        },
                        onDelete = { address ->
                            checkoutViewModel.deleteAddress(address.id)
                        }
                    )
                }

                AddressRoute.ADD_NEW -> {
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
                        onOpenMapPick = { currentRoute = AddressRoute.MAP_PICKER },
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

                            currentRoute = AddressRoute.LIST
                        },
                        onClose = {
                            formFullName = ""
                            formMobile = ""
                            formAddressLocation = ""
                            formLabel = "Home"
                            editingAddressId = null
                            currentRoute = AddressRoute.LIST
                        }
                    )
                }

                AddressRoute.MAP_PICKER -> {
                    MapLocation(
                        onLocationConfirmed = { lat, lng ->
                            coroutineScope.launch {
                                formAddressLocation = getReadableAddress(context, lat, lng)
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