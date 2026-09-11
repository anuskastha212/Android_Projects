package com.example.esewa_project.ui.compose

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable

@Composable
fun MyOrderScreen(
    onBackClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CommonTopBar(
                title = "My Order",
                onBackClick = onBackClick
            )
        }
    ) { }
}