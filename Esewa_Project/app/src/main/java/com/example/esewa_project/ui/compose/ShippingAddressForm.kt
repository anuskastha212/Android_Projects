package com.example.esewa_project.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShippingAddressForm(
    fullName: String,
    onFullNameChange: (String) -> Unit,
    mobileNumber: String,
    onMobileChange: (String) -> Unit,
    pickedAddressLocation: String,
    selectedLabel: String,
    onLabelChange: (String) -> Unit,
    isDefaultShipping: Boolean,
    onDefaultShippingChange: (Boolean) -> Unit,
    isDefaultBilling: Boolean,
    onDefaultBillingChange: (Boolean) -> Unit,
    onOpenMapPick: () -> Unit,
    onSave: () -> Unit,
    onClose: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    Scaffold(
        modifier = Modifier
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Add your new address",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF292A40)
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            Icons.Default.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = Color(0xFF292A40)
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(32.dp)
                            .background(Color(0xFFF1F1F5), RoundedCornerShape(8.dp))
                            .clickable { onClose() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF292A40),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.White
                )
            )
        },
        bottomBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8F9FA))
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        top = 8.dp,
                        bottom = 46.dp
                    )
            ) {
                Button(
                    onClick = onSave,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2ABB00)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "SAVE",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .background(Color.White, RoundedCornerShape(16.dp))
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Details for shipping",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF292A40)
            )

            // Full Name
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Full Name",
                    fontSize = 12.sp,
                    color = Color(0xFF555770),
                    fontWeight = FontWeight.SemiBold
                )
                TextField(
                    value = fullName,
                    onValueChange = onFullNameChange,
                    placeholder = {
                        Text(
                            "Enter Full Name",
                            color = Color(0xFFA8AABB),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F6F8),
                        unfocusedContainerColor = Color(0xFFF5F6F8),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF2ABB00)
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        color = Color(0xFF292A40)
                    ),
                    singleLine = true
                )
            }

            // Mobile Number
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Mobile Number",
                    fontSize = 12.sp,
                    color = Color(0xFF555770),
                    fontWeight = FontWeight.SemiBold
                )
                TextField(
                    value = mobileNumber,
                    onValueChange = onMobileChange,
                    placeholder = {
                        Text(
                            "Enter mobile No.",
                            color = Color(0xFFA8AABB),
                            fontSize = 14.sp
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F6F8),
                        unfocusedContainerColor = Color(0xFFF5F6F8),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF2ABB00)
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        color = Color(0xFF292A40)
                    ),
                    singleLine = true
                )
            }

            // Address Field with Location Icon
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    "Address",
                    fontSize = 12.sp,
                    color = Color(0xFF555770),
                    fontWeight = FontWeight.SemiBold
                )
                TextField(
                    value = pickedAddressLocation,
                    onValueChange = { },
                    placeholder = {
                        Text(
                            "Enter Address",
                            color = Color(0xFFA8AABB),
                            fontSize = 14.sp
                        )
                    },
                    readOnly = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFFF5F6F8),
                        unfocusedContainerColor = Color(0xFFF5F6F8),
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontSize = 14.sp,
                        color = Color(0xFF292A40)
                    ),
                    trailingIcon = {
                        IconButton(onClick = onOpenMapPick) {
                            Icon(
                                Icons.Outlined.LocationOn,
                                contentDescription = "Pick Location",
                                tint = Color(0xFF292A40)
                            )
                        }
                    }
                )
            }

            // Select a label
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Select a label",
                    fontSize = 12.sp,
                    color = Color(0xFF717282),
                    fontWeight = FontWeight.SemiBold
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Home", "Office", "Other").forEach { label ->
                        val isSelected = selectedLabel == label
                        Box(
                            modifier = Modifier
                                .background(
                                    if (isSelected) Color(0xFF2ABB00) else Color.Transparent,
                                    RoundedCornerShape(8.dp)
                                )
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFF2ABB00) else Color(0xFFE2E2EA),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable { onLabelChange(label) }
                                .padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else Color(0xFF717282),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 4.dp),
                color = Color(0xFFF1F1F5),
                thickness = 1.dp
            )

            // Toggles
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Make this as a default shipping address",
                        fontSize = 13.sp,
                        color = Color(0xFF717282),
                        fontWeight = FontWeight.SemiBold
                    )
                    Switch(
                        checked = isDefaultShipping,
                        onCheckedChange = onDefaultShippingChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF2ABB00),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFF717282),
                            uncheckedBorderColor = Color.Transparent
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
                        fontSize = 13.sp,
                        color = Color(0xFF717282),
                        fontWeight = FontWeight.SemiBold
                    )
                    Switch(
                        checked = isDefaultBilling,
                        onCheckedChange = onDefaultBillingChange,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF2ABB00),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFF717282),
                            uncheckedBorderColor = Color.Transparent
                        )
                    )
                }
            }
        }
    }
}