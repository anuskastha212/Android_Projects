package com.example.esewa_project.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esewa_project.R
import com.example.esewa_project.data.model.CartItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    items: List<CartItem>,
    onBackClick: () -> Unit
) {
    var showPromoSheet by remember { mutableStateOf(false) }
    var showPaymentSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Checkout",
                onBackClick = onBackClick)
        },
        bottomBar = {
            CheckoutBottomBar(items = items)
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
                CheckoutDelivery()
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
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
                PaymentOptionsCard(onPaymentClick = { showPaymentSheet = true })
            }
        }
    }

    if (showPromoSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPromoSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            PromoBottomSheetContent(onApply = { showPromoSheet = false })
        }
    }

    if (showPaymentSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPaymentSheet = false },
            sheetState = sheetState,
            containerColor = Color.White
        ) {
            PaymentBottomSheetContent(onClose = { showPaymentSheet = false })
        }
    }
}

@Composable
fun PromoCodeButton(onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier
            .height(40.dp),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, Color(0xFF2ABB00)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color.Transparent,
            contentColor = Color(0xFF2ABB00)
        ),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Text(
            "HAVE A PROMOCODE?",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
        )
    }
}

@Composable
fun PaymentOptionsCard(onPaymentClick: () -> Unit) {
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPaymentClick() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painterResource(id = R.drawable.cod),
                            contentDescription = null,
                            tint = Color(0xFF2ABB00),
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Cash on Delivery",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF292A40)
                        )
                    }
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFFA8AABB)
                    )
                }
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp), color = Color(0xFFF1F1F5))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onPaymentClick() }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painterResource(id = R.drawable.esewa_logo),
                            contentDescription = null,
                            tint = Color.Unspecified,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Pay with eSewa",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF292A40)
                        )
                    }
                    Icon(
                        Icons.Default.KeyboardArrowRight,
                        contentDescription = null,
                        tint = Color(0xFFA8AABB)
                    )
                }
            }
        }
    }
}

@Composable
fun PromoBottomSheetContent(onApply: () -> Unit) {
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
            value = "",
            onValueChange = {},
            placeholder = { Text("Enter code here...") },
            modifier = Modifier.fillMaxWidth()
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
                "Apply",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp)
        }
    }
}

@Composable
fun PaymentBottomSheetContent(onClose: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Select Payment Method",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF292A40)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Integration for eSewa API and other bank options will go here.",
            color = Color.Gray,
            fontSize = 14.sp)
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun CheckoutScreenPreview() {
    val mockItems = listOf(
        CartItem(
            productId = 1,
            title = "Jacket in nylon",
            price = 19500.0,
            thumbnail = "",
            categoryName = "CELEINE",
            quantity = 1
        )
    )
    CheckoutScreen(
        items = mockItems,
        onBackClick = {}
    )
}