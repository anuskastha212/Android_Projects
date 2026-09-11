package com.example.esewa_project

import android.app.Activity
import android.content.Intent
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
import com.example.esewa_project.data.model.AddressFormState
import com.example.esewa_project.data.repository.UserSessionRepository
import com.example.esewa_project.ui.compose.ShippingAddressForm
import com.example.esewa_project.ui.compose.MapLocation
import com.example.esewa_project.ui.compose.ShippingAddressScreen
import com.example.esewa_project.data.model.ShippingAddress
import com.example.esewa_project.ui.viewmodel.ShippingAddressViewModel
import com.example.esewa_project.ui.viewmodel.ShippingAddressViewModelFactory
import java.util.UUID

enum class AddressRoute {
    LIST,
    ADD_NEW,
    MAP_PICKER
}

class ShippingAddressActivity : ComponentActivity() {
    private lateinit var shippingViewModel: ShippingAddressViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val sessionRepo = UserSessionRepository(this)
        val factory = ShippingAddressViewModelFactory(sessionRepo)
        shippingViewModel = ViewModelProvider(this, factory)[ShippingAddressViewModel::class.java]

        setContent {
            var pendingSnackbarMessage by remember { mutableStateOf<String?>(null) }
            var deletedAddressForUndo by remember { mutableStateOf<ShippingAddress?>(null) }

            val savedAddresses by shippingViewModel.savedAddresses.collectAsState()
            val isAddressLoading by shippingViewModel.isLoading.collectAsState()

            var currentRoute by remember { mutableStateOf(AddressRoute.LIST) }
            var editingAddressId by remember { mutableStateOf<String?>(null) }

            var formState by remember { mutableStateOf(AddressFormState()) }

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
                            formState = AddressFormState()
                            currentRoute = AddressRoute.ADD_NEW
                        },
                        onAddressSelected = { address ->
                            val resultIntent = Intent().apply {
                                putExtra("selected_address_name", address.addressLocation)
                            }
                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        },
                        onEdit = { address ->
                            editingAddressId = address.id
                            formState = AddressFormState(address = address)
                            currentRoute = AddressRoute.ADD_NEW
                        },
                        onDelete = { address ->
                            shippingViewModel.deleteAddress(address.id)
                        },
                        onUndoDelete = { address ->
                            shippingViewModel.saveAddress(address) {}
                        }
                    )
                }

                AddressRoute.ADD_NEW -> {
                    ShippingAddressForm(
                        isEditing = editingAddressId != null,
                        isSaving = formState.isSaving,
                        fullName = formState.address.fullName,
                        onFullNameChange = {
                            formState = formState.copy(
                                address = formState.address.copy(fullName = it),
                                nameError = null
                            )
                        },
                        fullNameError = formState.nameError,
                        mobileNumber = formState.address.mobileNumber,
                        onMobileChange = {
                            formState = formState.copy(
                                address = formState.address.copy(mobileNumber = it),
                                mobileError = null
                            )
                        },
                        mobileNumberError = formState.mobileError,
                        pickedAddressLocation = formState.address.addressLocation,
                        addressError = formState.addressError,
                        selectedLabel = formState.address.label,
                        onLabelChange = {
                            formState = formState.copy(address = formState.address.copy(label = it))
                        },
                        isDefaultShipping = formState.address.isDefaultShipping,
                        onDefaultShippingChange = {
                            formState =
                                formState.copy(address = formState.address.copy(isDefaultShipping = it))
                        },
                        isDefaultBilling = formState.address.isDefaultBilling,
                        onDefaultBillingChange = {
                            formState =
                                formState.copy(address = formState.address.copy(isDefaultBilling = it))
                        },
                        onOpenMapPick = { currentRoute = AddressRoute.MAP_PICKER },
                        onSave = {
                            val addr = formState.address
                            var isValid = true
                            var nameErr: String? = null
                            var mobErr: String? = null
                            var locErr: String? = null

                            if (addr.fullName.isBlank()) {
                                nameErr = "Full name is required"
                                isValid = false
                            }
                            if (!addr.mobileNumber.matches(Regex("^[0-9]{10}$"))) {
                                mobErr = "Enter a valid 10-digit number"
                                isValid = false
                            }
                            if (addr.addressLocation.isBlank()) {
                                locErr = "Please pick a location"
                                isValid = false
                            }

                            if (isValid) {
                                formState = formState.copy(isSaving = true)
                                val finalAddress =
                                    addr.copy(id = editingAddressId ?: UUID.randomUUID().toString())

                                shippingViewModel.saveAddress(finalAddress) {
                                    pendingSnackbarMessage =
                                        if (editingAddressId != null) {
                                            "Address has been edited successfully"
                                        } else {
                                            "Address has been added successfully"
                                        }
                                    currentRoute = AddressRoute.LIST
                                }
                            } else {
                                formState = formState.copy(
                                    nameError = nameErr,
                                    mobileError = mobErr,
                                    addressError = locErr
                                )
                            }
                        },
                        onDelete = {
                            editingAddressId?.let { id -> shippingViewModel.deleteAddress(id) }
                            currentRoute = AddressRoute.LIST
                        },
                        onClose = {
                            currentRoute = AddressRoute.LIST
                        }
                    )
                }

                AddressRoute.MAP_PICKER -> {
                    MapLocation(
                        onLocationConfirmed = { lat, lng, addressName ->
                            formState = formState.copy(
                                address = formState.address.copy(addressLocation = addressName),
                                addressError = null
                            )
                            currentRoute = AddressRoute.ADD_NEW

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