package com.example.esewa_project.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.TextButton


@Composable
fun CheckoutDelivery(
    currentAddress: String?,
    onEditClick: () -> Unit
) {
    val isAddressEmpty = currentAddress.isNullOrEmpty()

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
                modifier = Modifier.size(40.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.location),
                    contentDescription = "Location",
                    tint = Color(0xFF2ABB00),
                    modifier = Modifier
                        .size(40.dp)
                        .background(Color(0xFFEAF9E6), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                if (isAddressEmpty) {
                    Text(
                        text = "Delivery Address Not Set",
                        fontSize = 12.sp,
                        color = Color(0xFF182B3C)
                    )
                    Text(
                        text = "Add Shipping Address",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF292A40)
                    )
                } else {
                    Text(
                        text = "Delivery Address",
                        fontSize = 12.sp,
                        color = Color(0xFFA8AABB)
                    )
                    Text(
                        text = currentAddress!!,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF292A40)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color(0xFF2ABB00), RoundedCornerShape(10.dp))
                    .clickable { onEditClick() },
                contentAlignment = Alignment.Center
            ) {
                if (isAddressEmpty) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Address",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                } else {
                    Icon(
                        painter = painterResource(id = R.drawable.edit),
                        contentDescription = "Edit Address",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun NoAddressBottomSheetContent(
    onSetAddress: () -> Unit,
    onCancel: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(id = R.drawable.no_address),
            contentDescription = null,
            modifier = Modifier.size(170.dp),
            tint = Color.Unspecified
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No address added yet !",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF292A40)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "You have not added any shipping address.",
            fontSize = 14.sp,
            color = Color(0xFF717282)
        )
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onSetAddress,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ABB00)),
            shape = RoundedCornerShape(14.dp)
        ) {
            Text(
                text = "SET ADDRESS",
                color = Color.White,
                letterSpacing = 1.sp,
                fontSize = 14.sp
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        TextButton(onClick = onCancel) {
            Text(
                text = "CANCEL",
                color = Color(0xFF717282),
                letterSpacing = 1.sp,
                fontSize = 14.sp
            )
        }
    }
}