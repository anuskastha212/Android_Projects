package com.example.esewa_project.ui.compose

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esewa_project.data.model.CartItem
import java.util.Locale

@Composable
fun CheckoutBottomBar(
    items: List<CartItem>,
    discount: Double = 0.0,
    onProceedClick: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    val subTotal = items.sumOf { it.price * it.quantity }
    val tax = 1500.0
    val shipping = 50.0
    val grandTotal = (subTotal + tax + shipping) - discount
    val totalItems = items.sumOf { it.quantity }

    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                .background(Color.White)
                .padding(horizontal = 24.dp, vertical = 24.dp)
                .padding(bottom = 16.dp)
        ) {
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(300)),
                exit = shrinkVertically(animationSpec = tween(300))
            ) {
                Column(modifier = Modifier.padding(bottom = 16.dp)) {
                    PriceRow("Sub Total ($totalItems Items)", subTotal)
                    Spacer(modifier = Modifier.height(8.dp))
                    if (discount > 0) {
                        PriceRow("Promo Discount", -discount)
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    PriceRow("Tax", tax)
                    Spacer(modifier = Modifier.height(8.dp))
                    PriceRow("Shipping Charge", shipping)
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onProceedClick,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ABB00)),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            text = "PROCEED",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = Color.White
                        )
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Grand Total ",
                        fontSize = 14.sp,
                        color = Color(0xFF292A40)
                    )
                    Text(
                        "*included TAX",
                        fontSize = 10.sp,
                        color = Color(0xFFA8AABB)
                    )
                }
                Text(
                    text = "Rs. ${String.format(Locale.getDefault(), "%,.2f", grandTotal)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color(0xFF2ABB00)
                )
            }
        }

        // Floating Green Toggle Button
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color(0xFF2ABB00), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = { isExpanded = !isExpanded }) {
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                    contentDescription = "Toggle Details",
                    tint = Color.White
                )
            }
        }
    }
}

@Composable
fun PriceRow(label: String, amount: Double) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, fontSize = 14.sp, color = Color(0xFF555770))
        Text(
            text = "Rs. ${String.format(Locale.getDefault(), "%,.2f", amount)}",
            fontSize = 14.sp,
            color = Color(0xFF292A40)
        )
    }
}