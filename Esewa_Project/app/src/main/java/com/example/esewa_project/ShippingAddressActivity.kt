package com.example.esewa_project

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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

            var pendingSnackbarMessage by remember { mutableStateOf<String?>(null) }
            var deletedAddressForUndo by remember { mutableStateOf<ShippingAddress?>(null) }

            val savedAddresses by checkoutViewModel.savedAddresses.collectAsState()
            val isAddressLoading by checkoutViewModel.isAddressLoading.collectAsState()

            var currentRoute by remember { mutableStateOf(AddressRoute.LIST) }
            var formAddressLocation by remember { mutableStateOf("") }
            var formFullName by remember { mutableStateOf("") }
            var formMobile by remember { mutableStateOf("") }
            var formLabel by remember { mutableStateOf("Home") }
            var formIsDefaultShipping by remember { mutableStateOf(true) }
            var formIsDefaultBilling by remember { mutableStateOf(false) }
            var editingAddressId by remember { mutableStateOf<String?>(null) }

            var fullNameError by remember { mutableStateOf<String?>(null) }
            var mobileError by remember { mutableStateOf<String?>(null) }
            var addressError by remember { mutableStateOf<String?>(null) }

            fun clearErrors() {
                fullNameError = null
                mobileError = null
                addressError = null
            }

            when (currentRoute) {
                AddressRoute.LIST -> {
                    ShippingAddressScreen(
                        addresses = savedAddresses,
                        isLoading = isAddressLoading,
                        pendingSnackbarMessage = pendingSnackbarMessage,
                        onSnackbarMessageShown = { pendingSnackbarMessage = null },
                        deletedAddressForUndo = deletedAddressForUndo,
                        onUndoSnackbarShown = { deletedAddressForUndo = null },
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
                        },
                        onUndoDelete = { address ->
                            checkoutViewModel.addNewAddress(address)
                        }
                    )
                }

                AddressRoute.ADD_NEW -> {
                    ShippingAddressForm(
                        isEditing = editingAddressId != null,
                        fullName = formFullName,
                        onFullNameChange = { formFullName = it; fullNameError = null },
                        fullNameError = fullNameError,
                        mobileNumber = formMobile,
                        onMobileChange = { formMobile = it; mobileError = null },
                        mobileNumberError = mobileError,
                        pickedAddressLocation = formAddressLocation,
                        addressError = addressError,
                        selectedLabel = formLabel,
                        onLabelChange = { formLabel = it },
                        isDefaultShipping = formIsDefaultShipping,
                        onDefaultShippingChange = { formIsDefaultShipping = it },
                        isDefaultBilling = formIsDefaultBilling,
                        onDefaultBillingChange = { formIsDefaultBilling = it },
                        onOpenMapPick = { currentRoute = AddressRoute.MAP_PICKER },
                        onSave = {
                            var isValid = true
                            if (formFullName.isBlank()) {
                                fullNameError = "Full name is required"
                                isValid = false
                            } else if (formFullName.length < 3 || !formFullName.matches(Regex("^[a-zA-Z\\s]+$"))) {
                                fullNameError = "Enter a valid full name"
                                isValid = false
                            }
                            if (formMobile.isBlank()) {
                                mobileError = "Mobile number is required"
                                isValid = false
                            } else if (!formMobile.matches(Regex("^[0-9]{10}$"))) {
                                mobileError = "Enter a valid 10-digit number"
                                isValid = false
                            }
                            if (formAddressLocation.isBlank()) {
                                addressError = "Please pick a location from map"
                                isValid = false
                            }

                            if (isValid) {
                                val isEditing = editingAddressId != null
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
                                pendingSnackbarMessage = if (isEditing) {
                                    "Address has been edited successfully"
                                } else {
                                    "Address has been added successfully"
                                }
                                currentRoute = AddressRoute.LIST
                            }
                        },
                        onDelete = {
                            editingAddressId?.let { id ->
                                checkoutViewModel.deleteAddress(id)
                                deletedAddressForUndo = ShippingAddress(
                                    id,
                                    formFullName,
                                    formMobile,
                                    formAddressLocation,
                                    formLabel,
                                    formIsDefaultShipping,
                                    formIsDefaultBilling
                                )
                            }
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
                                formAddressLocation = getReadableAddress(context, lat, lng)
                                addressError = null
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

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (ev?.action == MotionEvent.ACTION_DOWN) {
            val v = currentFocus
            if (v != null) {
                val outRect = Rect()
                v.getGlobalVisibleRect(outRect)
                if (!outRect.contains(ev.rawX.toInt(), ev.rawY.toInt())) {
                    v.clearFocus()
                    val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.hideSoftInputFromWindow(v.windowToken, 0)
                }
            }
        }
        return super.dispatchTouchEvent(ev)
    }
}