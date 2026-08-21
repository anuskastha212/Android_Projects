package com.example.esewa_project.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esewa_project.R

@Composable
fun CheckoutDelivery() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.location),
                    contentDescription = "Location",
                    tint = Color(0xFF2ABB00),
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color(0xFFEAF9E6),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Text Column
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Delivery Address",
                    fontSize = 12.sp,
                    color = Color(0xFFA8AABB)
                )
                Text(
                    text = "Pulchowk, Lalitpur-20",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF292A40)
                )
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF2ABB00), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.edit),
                    contentDescription = "Edit",
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}