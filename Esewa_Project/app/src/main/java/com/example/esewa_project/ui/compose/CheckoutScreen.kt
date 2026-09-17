package com.example.esewa_project.ui.compose

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esewa_project.data.model.CartItem
import com.example.esewa_project.data.model.PaymentMethod
import com.example.esewa_project.ui.util.UiState
import com.example.esewa_project.ui.viewmodel.CheckoutViewModel

@Composable
fun CheckoutScreen(
    uiState: UiState<List<CartItem>>,
    checkoutViewModel: CheckoutViewModel,
    selectedPaymentMethod: PaymentMethod,
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
    onBackClick: () -> Unit,
    onProceedClick: () -> Unit,
    onEditAddressClick: () -> Unit,
    onRetry: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        when (uiState) {
            is UiState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center),
                    color = Color(0xFF2ABB00)
                )
            }

            is UiState.Success -> {
                CheckoutScreenContent(
                    items = uiState.data,
                    checkoutViewModel = checkoutViewModel,
                    selectedPaymentMethod = selectedPaymentMethod,
                    onPaymentMethodSelected = onPaymentMethodSelected,
                    onBackClick = onBackClick,
                    onProceedClick = onProceedClick,
                    onEditAddressClick = onEditAddressClick
                )
            }

            is UiState.Empty -> {
                Text(
                    "No items for checkout",
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            is UiState.Error -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = uiState.message,
                        color = Color.Red
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = onRetry) {
                        Text("Retry")
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreenContent(
    items: List<CartItem>,
    checkoutViewModel: CheckoutViewModel,
    selectedPaymentMethod: PaymentMethod,
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
    onBackClick: () -> Unit,
    onProceedClick: () -> Unit,
    onEditAddressClick: () -> Unit
) {
    val context = LocalContext.current
    val deliveryAddress by checkoutViewModel.deliveryAddress.collectAsState()
    val discount by checkoutViewModel.promoDiscount.collectAsState()

    var showNoAddressSheet by remember { mutableStateOf(false) }
    var showPromoSheet by remember { mutableStateOf(false) }
    var promoCodeInput by remember { mutableStateOf("") }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

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
                            if (deliveryAddress.isNullOrEmpty()) showNoAddressSheet = true
                            else onEditAddressClick()
                        }
                    )
                    Text(
                        text = "Order Summary",
                        fontSize = 14.sp,
                        color = Color(0xFF555770)
                    )
                    items.forEach { item ->
                        CheckoutProductCard(item = item)
                    }
                }
                PromoCodeButton(onClick = { showPromoSheet = true })
                PaymentOptionsCard(
                    selectedMethod = selectedPaymentMethod,
                    onMethodSelected = onPaymentMethodSelected
                )
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
                    onEditAddressClick()
                },
                onCancel = { showNoAddressSheet = false }
            )
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