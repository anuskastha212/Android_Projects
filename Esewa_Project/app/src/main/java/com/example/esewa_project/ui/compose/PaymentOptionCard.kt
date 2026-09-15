package com.example.esewa_project.ui.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esewa_project.data.model.PaymentMethod

@Composable
fun PaymentOptionsCard(
    selectedMethod: PaymentMethod = PaymentMethod.COD,
    onMethodSelected: (PaymentMethod) -> Unit = {}
) {
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
                PaymentMethod.values().forEachIndexed { index, method ->
                    PaymentOptionRow(
                        method = method,
                        isSelected = selectedMethod == method,
                        onClick = { onMethodSelected(method) }
                    )
                    if (index < PaymentMethod.values().size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = Color(0xFFF1F1F5)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentOptionRow(
    method: PaymentMethod,
    isSelected: Boolean,
    onClick: () -> Unit
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
                painter = painterResource(id = method.iconRes),
                contentDescription = null,
                tint = if (method.isEsewa) Color.Unspecified else Color(0xFF2ABB00),
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                method.title,
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