package com.example.esewa_project.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esewa_project.R
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteTopBar(
    cartCount: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "Favourites",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF292A40)
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Back",
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        actions = {
            BadgedBox(
                badge = {
                    if (cartCount > 0) {
                        Badge(
                            containerColor = Color(0xFF2ABB00),
                            contentColor = Color.White
                        ) {
                            Text(
                                text = cartCount.toString(),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                },
                modifier = Modifier
                    .padding(end = 16.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = onCartClick
                    )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.nav_cart),
                    contentDescription = "Cart",
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            Color(0xFFF8F8FA),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                )
            }
        }
    )
}