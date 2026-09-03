package com.example.esewa_project.ui.compose

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esewa_project.R
import com.example.esewa_project.data.model.ShippingAddress

@Composable
fun ShippingAddressScreen(
    addresses: List<ShippingAddress>,
    onBackClick: () -> Unit,
    onAddAddressClick: () -> Unit,
    onAddressSelected: (ShippingAddress) -> Unit,
    onEdit: (ShippingAddress) -> Unit,
    onDelete: (ShippingAddress) -> Unit
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
                    items(addresses, key = { it.id }) { address ->
                        ShippingAddressItemCard(
                            address = address,
                            onClick = { onAddressSelected(address) },
                            onEdit = { onEdit(address) },
                            onDelete = { onDelete(address) }
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
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 48.dp, horizontal = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.empty_shipping_address),
                    contentDescription = "No address empty state",
                    modifier = Modifier.size(180.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))

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
                    textAlign = TextAlign.Center,
                    lineHeight = 22.sp
                )
                Spacer(modifier = Modifier.height(32.dp))

                Button(
                    onClick = onAddClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ABB00)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .height(48.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    Text(
                        "ADD ADDRESS NOW",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }
        }
    }
}