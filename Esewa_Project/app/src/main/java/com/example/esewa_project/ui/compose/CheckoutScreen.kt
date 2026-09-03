package com.example.esewa_project.ui.compose

import android.content.Context
import android.location.Geocoder
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esewa_project.R
import com.example.esewa_project.data.model.CartItem
import com.example.esewa_project.data.model.ShippingAddress
import com.example.esewa_project.ui.viewmodel.CheckoutViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale
import java.util.UUID

enum class CheckoutRoute {
    CHECKOUT,
    SHIPPING_ADDRESS_LIST,
    ADD_NEW_ADDRESS,
    MAP_PICKER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    items: List<CartItem>,
    checkoutViewModel: CheckoutViewModel,
    onBackClick: () -> Unit,
    onProceedClick: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val deliveryAddress by checkoutViewModel.deliveryAddress.collectAsState()
    val savedAddresses by checkoutViewModel.savedAddresses.collectAsState()
    val discount by checkoutViewModel.promoDiscount.collectAsState()

    var currentRoute by remember { mutableStateOf(CheckoutRoute.CHECKOUT) }

    // HOISTED FORM VARIABLES
    var editingAddressId by remember { mutableStateOf<String?>(null) } // ADDED THIS
    var formAddressLocation by remember { mutableStateOf("") }
    var formFullName by remember { mutableStateOf("") }
    var formMobile by remember { mutableStateOf("") }
    var formLabel by remember { mutableStateOf("Home") }
    var formIsDefaultShipping by remember { mutableStateOf(true) }
    var formIsDefaultBilling by remember { mutableStateOf(false) }

    var showNoAddressSheet by remember { mutableStateOf(false) }
    var showPromoSheet by remember { mutableStateOf(false) }
    var promoCodeInput by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    when (currentRoute) {
        CheckoutRoute.MAP_PICKER -> {
            MapLocation(
                onLocationConfirmed = { lat, lng ->
                    coroutineScope.launch {
                        val realAddress = getReadableAddress(context, lat, lng)
                        formAddressLocation = realAddress
                        currentRoute = CheckoutRoute.ADD_NEW_ADDRESS
                    }
                },
                onClose = {
                    currentRoute = CheckoutRoute.ADD_NEW_ADDRESS
                }
            )
        }

        CheckoutRoute.ADD_NEW_ADDRESS -> {
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
                onOpenMapPick = { currentRoute = CheckoutRoute.MAP_PICKER },
                onSave = {
                    val newAddress = ShippingAddress(
                        id = editingAddressId ?: UUID.randomUUID().toString(), // UPDATED THIS
                        fullName = formFullName,
                        mobileNumber = formMobile,
                        addressLocation = formAddressLocation,
                        label = formLabel,
                        isDefaultShipping = formIsDefaultShipping,
                        isDefaultBilling = formIsDefaultBilling
                    )
                    checkoutViewModel.addNewAddress(newAddress)

                    // Clear form
                    formFullName = ""
                    formMobile = ""
                    formAddressLocation = ""
                    formLabel = "Home"
                    editingAddressId = null // CLEAR ID

                    currentRoute = CheckoutRoute.SHIPPING_ADDRESS_LIST
                },
                onClose = {
                    // Clear form
                    formFullName = ""
                    formMobile = ""
                    formAddressLocation = ""
                    formLabel = "Home"
                    editingAddressId = null // CLEAR ID

                    currentRoute = CheckoutRoute.SHIPPING_ADDRESS_LIST
                }
            )
        }

        CheckoutRoute.SHIPPING_ADDRESS_LIST -> {
            ShippingAddressScreen(
                addresses = savedAddresses,
                onBackClick = { currentRoute = CheckoutRoute.CHECKOUT },
                onAddAddressClick = {
                    editingAddressId = null
                    formFullName = ""
                    formMobile = ""
                    formAddressLocation = ""
                    formLabel = "Home"
                    formIsDefaultShipping = true
                    currentRoute = CheckoutRoute.ADD_NEW_ADDRESS
                },
                onAddressSelected = { address ->
                    checkoutViewModel.selectAddress(address)
                    currentRoute = CheckoutRoute.CHECKOUT
                },
                onEdit = { address ->
                    editingAddressId = address.id
                    formFullName = address.fullName
                    formMobile = address.mobileNumber
                    formAddressLocation = address.addressLocation
                    formLabel = address.label
                    formIsDefaultShipping = address.isDefaultShipping
                    formIsDefaultBilling = address.isDefaultBilling

                    currentRoute = CheckoutRoute.ADD_NEW_ADDRESS
                },
                onDelete = { address ->
                    checkoutViewModel.deleteAddress(address.id)
                }
            )
        }
        CheckoutRoute.CHECKOUT -> {
            Scaffold(
                topBar = {
                    CommonTopBar(
                        title = "Checkout",
                        onBackClick = onBackClick
                    )
                },
                bottomBar = {
                    CheckoutBottomBar(
                        items = items,
                        discount = discount,
                        onProceedClick = onProceedClick
                    )
                }
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFFF8F9FA))
                        .padding(paddingValues)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                            .padding(horizontal = 16.dp, vertical = 24.dp)
                            .padding(bottom = 80.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            CheckoutDelivery(
                                currentAddress = deliveryAddress,
                                onEditClick = {
                                    if (deliveryAddress.isNullOrEmpty()) {
                                        showNoAddressSheet = true
                                    } else {
                                        currentRoute = CheckoutRoute.SHIPPING_ADDRESS_LIST
                                    }
                                }
                            )
                            Text(
                                text = "Order Summary",
                                fontSize = 14.sp,
                                color = Color(0xFF555770),
                            )
                            items.forEach { item ->
                                CheckoutProductCard(item = item)
                            }
                        }
                        PromoCodeButton(onClick = { showPromoSheet = true })
                        PaymentOptionsCard()
                    }
                }
            }

            if (showPromoSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showPromoSheet = false },
                    sheetState = sheetState,
                    containerColor = Color.White
                ) {
                    PromoBottomSheetContent(
                        codeValue = promoCodeInput,
                        onCodeChange = { promoCodeInput = it },
                        onApply = {
                            val success = checkoutViewModel.applyPromoCode(promoCodeInput.trim())
                            if (success) {
                                showPromoSheet = false
                                Toast.makeText(
                                    context,
                                    "Promo Code Applied!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Invalid Promo Code",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
            }

            if (showNoAddressSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showNoAddressSheet = false },
                    sheetState = sheetState,
                    containerColor = Color.White
                ) {
                    NoAddressBottomSheetContent(
                        onSetAddress = {
                            showNoAddressSheet = false
                            currentRoute = CheckoutRoute.SHIPPING_ADDRESS_LIST
                        },
                        onCancel = {
                            showNoAddressSheet = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun PromoCodeButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.height(40.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFF2ABB00)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = Color(0xFF2ABB00)
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            "HAVE A PROMO CODE?",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
fun PromoBottomSheetContent(
    codeValue: String,
    onCodeChange: (String) -> Unit,
    onApply: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Enter Promo Code",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF292A40)
        )
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = codeValue,
            onValueChange = onCodeChange,
            placeholder = { Text("Enter code here...") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onApply,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ABB00)),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = "Apply",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }
    }
}

@Composable
fun PaymentOptionsCard() {
    var selectedMethod by remember { mutableStateOf("COD") }
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            "Choose Your Payment Option",
            fontSize = 13.sp,
            color = Color(0xFF555770)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column {
                PaymentOptionRow(
                    iconRes = R.drawable.cod,
                    label = "Cash on Delivery",
                    isSelected = selectedMethod == "COD",
                    onClick = { selectedMethod = "COD" }
                )
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFF1F1F5)
                )
                PaymentOptionRow(
                    iconRes = R.drawable.esewa_logo,
                    label = "Pay with eSewa",
                    isSelected = selectedMethod == "ESEWA",
                    onClick = { selectedMethod = "ESEWA" },
                    isEsewa = true
                )
            }
        }
    }
}

@Composable
fun PaymentOptionRow(
    iconRes: Int,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    isEsewa: Boolean = false
) {
    val contentAlpha = if (isSelected) 1.0f else 0.4f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp)
            .graphicsLayer(alpha = contentAlpha),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                painter = painterResource(id = iconRes),
                contentDescription = null,
                tint = if (isEsewa) Color.Unspecified else Color(0xFF2ABB00),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                label,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold,
                color = Color(0xFF292A40)
            )
        }
        Icon(
            Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = Color(0xFFA8AABB)
        )
    }
}

@Suppress("DEPRECATION")
suspend fun getReadableAddress(context: Context, lat: Double, lng: Double): String {
    return withContext(Dispatchers.IO) {
        try {
            val geocoder = Geocoder(context, Locale.getDefault())
            val addresses = geocoder.getFromLocation(lat, lng, 1)
            if (!addresses.isNullOrEmpty()) {
                val address = addresses[0]
                address.getAddressLine(0)
                    ?: "${address.subLocality ?: ""}, ${address.locality ?: ""}".trim(',', ' ')
            } else {
                "Location ($lat, $lng)"
            }
        } catch (e: Exception) {
            e.printStackTrace()
            "Location ($lat, $lng)"
        }
    }
}