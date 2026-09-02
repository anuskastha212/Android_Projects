package com.example.esewa_project

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import com.example.esewa_project.data.model.ShippingAddress
import com.example.esewa_project.ui.compose.ShippingAddressForm
import com.example.esewa_project.ui.compose.MapLocation
import com.example.esewa_project.ui.compose.ShippingAddressScreen
import com.example.esewa_project.ui.compose.getReadableAddress
import kotlinx.coroutines.launch
import androidx.compose.ui.platform.LocalContext

enum class AddressRoute {
    LIST,
    ADD_NEW,
    MAP_PICKER
}

class ShippingAddressActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val context = LocalContext.current
            val coroutineScope = rememberCoroutineScope()

            var savedAddresses by remember { mutableStateOf<List<ShippingAddress>>(emptyList()) }

            var currentRoute by remember { mutableStateOf(AddressRoute.LIST) }
            var pickedAddressForForm by remember { mutableStateOf("") }

            when (currentRoute) {
                AddressRoute.LIST -> {
                    ShippingAddressScreen(
                        addresses = savedAddresses,
                        onBackClick = { finish() },
                        onAddAddressClick = { currentRoute = AddressRoute.ADD_NEW },
                        onAddressSelected = {  }
                    )
                }
                AddressRoute.ADD_NEW -> {
                    ShippingAddressForm(
                        pickedAddressLocation = pickedAddressForForm,
                        onOpenMapPick = { currentRoute = AddressRoute.MAP_PICKER },
                        onSave = { newAddress ->
                            savedAddresses = savedAddresses + newAddress
                            currentRoute = AddressRoute.LIST
                        },
                        onClose = { currentRoute = AddressRoute.LIST }
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
                        onClose = { currentRoute = AddressRoute.ADD_NEW }
                    )
                }
            }
        }
    }
}