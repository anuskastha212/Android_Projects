package com.example.esewa_project.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esewa_project.data.model.CartItem

@Composable
fun CheckoutScreen(
    items: List<CartItem>,
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Checkout",
                onBackClick = onBackClick
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
                    .padding(horizontal = 16.dp, vertical = 24.dp),
                // Adds a clean 24dp gap between the address and the summary section
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // 1. Delivery Address Card
                CheckoutDelivery()

                // 2. Order Summary Section
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Order Summary",
                        fontSize = 14.sp,
                        color = Color(0xFF555770),
                        fontWeight = FontWeight.Medium
                    )

                    items.forEach { item ->
                        CheckoutProductCard(item = item)
                    }                }
            }
        }
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
        items = mockItems  ,
        onBackClick = {}
    )
}