package com.example.esewa_project.ui.compose

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esewa_project.data.model.ShippingAddress

@Composable
fun ShippingAddressScreen(
    addresses: List<ShippingAddress>,
    onBackClick: () -> Unit,
    onAddAddressClick: () -> Unit,
    onAddressSelected: (ShippingAddress) -> Unit
) {
    Scaffold(
        topBar = {
            CommonTopBar(
                title = "Shipping Address",
                onBackClick = onBackClick
            )
        },
        floatingActionButton = {
            if (addresses.isNotEmpty()) {
                ExtendedFloatingActionButton(
                    onClick = onAddAddressClick,
                    containerColor = Color(0xFF2ABB00),
                    contentColor = Color.White,
                    icon = {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add"
                        )
                    },
                    text = {
                        Text(
                            "ADD ADDRESS",
                            fontWeight = FontWeight.Bold
                        )
                    }
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
                .padding(paddingValues)
        ) {
            if (addresses.isEmpty()) {
                EmptyAddressState(onAddAddressClick)
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(addresses) { address ->
                        AddressItemCard(
                            address = address,
                            onClick = { onAddressSelected(address) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyAddressState(onAddClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .background(Color(0xFFF1F1F5), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        modifier = Modifier.size(50.dp),
                        tint = Color(0xFF2ABB00)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "No address added yet!",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF292A40)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "You have not added any shipping\naddress yet.",
                    fontSize = 14.sp,
                    color = Color(0xFF717282),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ABB00)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(
                        "ADD ADDRESS NOW",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun AddressItemCard(address: ShippingAddress, onClick: () -> Unit) {
    val borderColor = if (address.isSelected) Color(0xFF2ABB00) else Color.Transparent
    val iconBgColor = if (address.isSelected) Color(0xFF2ABB00) else Color(0xFFEAF9E6)
    val iconColor = if (address.isSelected) Color.White else Color(0xFF2ABB00)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, borderColor),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(iconBgColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = iconColor,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = address.fullName,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF292A40),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF2ABB00), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = address.label.uppercase(),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = address.addressLocation,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF292A40),
                    fontSize = 13.sp
                )
                Text(
                    text = "Nearby location ${address.addressLocation}",
                    color = Color(0xFFA8AABB),
                    fontSize = 12.sp
                )
            }
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More options",
                tint = Color.Gray
            )
        }
    }
}
