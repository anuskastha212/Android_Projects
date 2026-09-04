package com.example.esewa_project.ui.util

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarData
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun SnackBar(
    snackBarData: SnackbarData,
    modifier: Modifier = Modifier
) {
    Snackbar(
        snackbarData = snackBarData,
        containerColor = Color.Black,
        contentColor = Color.White,
        actionColor = Color(0xFF2ABB00),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier.padding(
            start = 8.dp,
            end = 8.dp,
            bottom = 0.dp
        )
    )
}
