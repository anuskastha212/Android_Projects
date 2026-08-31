package com.example.esewa_project.ui.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esewa_project.data.model.CartItem
import java.util.Locale

@Composable
fun ConfirmationScreen(
    items: List<CartItem>,
    discount: Double = 0.0,
    onBackClick: () -> Unit,
    onConfirmClick: () -> Unit
) {
    val subTotal = items.sumOf { it.price * it.quantity }
    val tax = 1500.0
    val shipping = 50.0
    val grandTotal = (subTotal + tax + shipping) - discount

    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Confirmation",
                onBackClick = onBackClick
            )
        },
        bottomBar = {
            Button(
                onClick = onConfirmClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 24.dp,
                        end = 24.dp,
                        bottom = 45.dp,
                        top = 8.dp
                    )
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ABB00)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    "CONFIRM",
                    fontSize = 16.sp
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Color(0xFFF8F9FA)
                )
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            "Payment Details",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF292A40)
                        )

                        items.forEach { item ->
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    "Product item(1)",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF555770)
                                )
                                ConfirmationRow("Name", item.title)
                                ConfirmationRow(
                                    "Price",
                                    String.format(Locale.getDefault(), "%.2f", item.price)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        HorizontalDivider(color = Color(0xFFF1F1F5))

                        ConfirmationRow("Delivery Address", "Pulchowk")
                        ConfirmationRow("Payment Option", "Cash on Delivery")
                        if (discount > 0) {
                            ConfirmationRow(
                                "Promo Discount",
                                "-${String.format(Locale.getDefault(), "%.2f", discount)}"
                            )
                        }
                        ConfirmationRow("Vehicle Number", "BA 98 PA 8080")
                        ConfirmationRow("Delivery Charge", "200.00")

                        DottedDivider()

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "Total Paying Amount",
                                fontSize = 14.sp,
                                color = Color(0xFF555770)
                            )
                            Text(
                                String.format(Locale.getDefault(), "%,.2f", grandTotal),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF292A40)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmationRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            label,
            fontSize = 14.sp,
            color = Color(0xFF555770)
        )
        Text(
            value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF292A40)
        )
    }
}

@Composable
fun DottedDivider() {
    val pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    Canvas(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
    ) {
        drawLine(
            color = Color(0xFFA8AABB),
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            pathEffect = pathEffect
        )
    }
}