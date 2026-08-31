package com.example.esewa_project.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.esewa_project.data.model.CartItem
import java.util.Locale

@Composable
fun CheckoutProductCard(item: CartItem) {
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
                    .size(80.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color(0xFFF1F1F1)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = item.thumbnail,
                    contentDescription = item.title,
                    modifier = Modifier.size(60.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = item.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF292A40),
                    maxLines = 1
                )
                Text(
                    text = "Qty: ${item.quantity}",
                    fontSize = 12.sp,
                    color = Color(0xFF555770)
                )

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = item.categoryName.uppercase(),
                    fontSize = 11.sp,
                    color = Color(0xFFA8AABB),
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = String.format(Locale.getDefault(), "Rs. %,.2f", item.price),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF2ABB00)
                )
            }
        }
    }
}