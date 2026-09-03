package com.example.esewa_project.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esewa_project.data.model.ShippingAddress

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingAddressForm(
    pickedAddressLocation: String,
    onOpenMapPick: () -> Unit,
    onSave: (ShippingAddress) -> Unit,
    onClose: () -> Unit
) {
    var fullName by remember { mutableStateOf("") }
    var mobileNumber by remember { mutableStateOf("") }
    var selectedLabel by remember { mutableStateOf("Home") }
    var isDefaultShipping by remember { mutableStateOf(true) }
    var isDefaultBilling by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Add your new address",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 16.dp,
                        bottom = 46.dp
                    )
            ) {
                Button(
                    onClick = {
                        onSave(
                            ShippingAddress(
                                fullName = fullName,
                                mobileNumber = mobileNumber,
                                addressLocation = pickedAddressLocation,
                                label = selectedLabel,
                                isDefaultShipping = isDefaultShipping,
                                isDefaultBilling = isDefaultBilling
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ABB00)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        "SAVE",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Details for shipping",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = mobileNumber,
                onValueChange = { mobileNumber = it },
                label = { Text("Mobile Number") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            )

            OutlinedTextField(
                value = pickedAddressLocation,
                onValueChange = { },
                label = { Text("Address") },
                placeholder = { Text("Enter Address") },
                readOnly = true,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                trailingIcon = {
                    IconButton(onClick = onOpenMapPick) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = "Pick Location",
                            tint = Color.Gray
                        )
                    }
                }
            )

            Text(
                "Select a label",
                fontSize = 14.sp,
                color = Color.Gray
            )
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Home", "Office", "Other").forEach { label ->
                    val isSelected = selectedLabel == label
                    Box(
                        modifier = Modifier
                            .background(
                                if (isSelected) Color(0xFF2ABB00) else Color.Transparent,
                                RoundedCornerShape(16.dp)
                            )
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFF2ABB00) else Color.LightGray,
                                RoundedCornerShape(16.dp)
                            )
                            .clickable { selectedLabel = label }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else Color.Black,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Make this as a default shipping address",
                    fontSize = 14.sp
                )
                Switch(
                    checked = isDefaultShipping,
                    onCheckedChange = { isDefaultShipping = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF2ABB00),
                        checkedTrackColor = Color(0xFFEAF9E6)
                    )
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Make this as a default billing address",
                    fontSize = 14.sp
                )
                Switch(
                    checked = isDefaultBilling,
                    onCheckedChange = { isDefaultBilling = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color(0xFF2ABB00),
                        checkedTrackColor = Color(0xFFEAF9E6)
                    )
                )
            }
        }
    }
}