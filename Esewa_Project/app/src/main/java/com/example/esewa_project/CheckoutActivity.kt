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
    SHIPPING_ADDRESS_LIST,
    ADD_NEW_ADDRESS,
    MAP_PICKER,
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
        val favRepo = FavouriteRepository(database.favouriteDao())

        val factory = CheckoutViewModelFactory(productRepo, cartRepo,favRepo, sessionRepo)
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
            val checkoutItems by checkoutViewModel.checkoutItems.collectAsState()
            val discount by checkoutViewModel.promoDiscount.collectAsState()
            val savedAddresses by checkoutViewModel.savedAddresses.collectAsState()
            val isAddressLoading by checkoutViewModel.isAddressLoading.collectAsState()
            val deliveryAddress by checkoutViewModel.deliveryAddress.collectAsState()

            var currentRoute by remember { mutableStateOf(CheckoutFlowRoute.CHECKOUT) }

            var isSaving by remember { mutableStateOf(false) }
            var editingAddressId by remember { mutableStateOf<String?>(null) }
            var formFullName by remember { mutableStateOf("") }
            var formMobile by remember { mutableStateOf("") }
            var formAddressLocation by remember { mutableStateOf("") }
            var formLabel by remember { mutableStateOf("Home") }
            var formIsDefaultShipping by remember { mutableStateOf(true) }
            var formIsDefaultBilling by remember { mutableStateOf(false) }
            var pendingSnackbarMessage by remember { mutableStateOf<String?>(null) }
            var deletedAddressForUndo by remember { mutableStateOf<ShippingAddress?>(null) }

            var fullNameError by remember { mutableStateOf<String?>(null) }
            var mobileError by remember { mutableStateOf<String?>(null) }
            var addressError by remember { mutableStateOf<String?>(null) }
            var currentOrder by remember { mutableStateOf("") }

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
                            deliveryAddress = deliveryAddress ?: "Address Not Set",
                            discount = discount,
                            onBackClick = { currentRoute = CheckoutFlowRoute.CHECKOUT },
                            onConfirmClick = {
                                val subTotal = checkoutItems.sumOf { it.price * it.quantity }
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

                                val intent = android.content.Intent(
                                    this@CheckoutActivity,
                                    PaymentActivity::class.java
                                ).apply {
                                    putExtra(
                                        "amount",
                                        String.format(java.util.Locale.US, "%.2f", grandTotal)
                                    )
                                    putExtra("product_name", "Order from Esewa Market")
                                    putExtra("product_id", newOrderId)
                                }
                                paymentLauncher.launch(intent)
                            }
                        )
                    }

                    CheckoutFlowRoute.SHIPPING_ADDRESS_LIST -> {
                        ShippingAddressScreen(
                            addresses = savedAddresses,
                            isLoading = isAddressLoading,
                            pendingSnackbarMessage = pendingSnackbarMessage,
                            onSnackbarMessageShown = { pendingSnackbarMessage = null },
                            deletedAddressForUndo = deletedAddressForUndo,
                            onUndoSnackbarShown = { deletedAddressForUndo = null },
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
                            isEditing = editingAddressId != null,
                            isSaving = isSaving,
                            fullName = formFullName,
                            onFullNameChange = {
                                formFullName = it
                                fullNameError = null
                            },
                            fullNameError = fullNameError,
                            mobileNumber = formMobile,
                            onMobileChange = {
                                formMobile = it
                                mobileError = null
                            },
                            mobileNumberError = mobileError,
                            pickedAddressLocation = formAddressLocation,
                            addressError = addressError,
                            selectedLabel = formLabel,
                            onLabelChange = { formLabel = it },
                            isDefaultShipping = formIsDefaultShipping,
                            onDefaultShippingChange = { formIsDefaultShipping = it },
                            isDefaultBilling = formIsDefaultBilling,
                            onDefaultBillingChange = { formIsDefaultBilling = it },
                            onOpenMapPick = { currentRoute = CheckoutFlowRoute.MAP_PICKER },
                            onSave = {
                                if (!isSaving) {
                                    isSaving = true
                                    var isValid = true
                                    if (formFullName.isBlank()) {
                                        fullNameError = "Full name is required"
                                        isValid = false
                                    } else if (formFullName.length < 3 || !formFullName.matches(
                                            Regex("^[a-zA-Z\\s]+$")
                                        )
                                    ) {
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
                                        currentRoute = CheckoutFlowRoute.SHIPPING_ADDRESS_LIST
                                    }
                                } else {
                                    isSaving = false
                                }
                            },
                            onDelete = {
                                editingAddressId?.let { id ->
                                    val addressToUndo = ShippingAddress(
                                        id = id,
                                        fullName = formFullName,
                                        mobileNumber = formMobile,
                                        addressLocation = formAddressLocation,
                                        label = formLabel,
                                        isDefaultShipping = formIsDefaultShipping,
                                        isDefaultBilling = formIsDefaultBilling
                                    )
                                    checkoutViewModel.deleteAddress(id)
                                    deletedAddressForUndo = addressToUndo
                                }

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
                            onLocationConfirmed = { lat, lng, addressName ->
                                formAddressLocation = addressName
                                currentRoute = CheckoutFlowRoute.ADD_NEW_ADDRESS
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