package com.example.esewa_project.ui.fragments

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import com.example.esewa_project.R
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esewa_project.data.local.entity.ProductEntity
import com.example.esewa_project.ui.viewmodel.FavouriteViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreen(
    favouriteViewModel: FavouriteViewModel,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val products by favouriteViewModel.favouriteProducts.collectAsState(initial = emptyList())
    val favouriteCount by favouriteViewModel.favouriteCount.collectAsState(initial = 0)
    FavouriteScreenContent(
        products = products,
        favouriteCount = favouriteCount,
        onBackClick = onBackClick,
        onCartClick = onCartClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteScreenContent(
    products: List<ProductEntity>,
    favouriteCount: Int,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        text = "Favourites",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Back",
                        modifier = Modifier
                            .size(24.dp)
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null,
                                onClick = onBackClick
                            )
                    )
                },
                actions = {
                    BadgedBox(
                        badge = {
                            if (favouriteCount > 0) {
                                Badge(
                                    containerColor = Color(0xFF2ABB00),
                                    contentColor = Color.White
                                ) {
                                    Text(
                                        text = "$favouriteCount",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        },
                        modifier = Modifier.clickable(
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

    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FavouriteScreenPreview() {
    FavouriteScreenContent(
        products = emptyList(),
        favouriteCount = 1,
        onBackClick = {},
        onCartClick = {}
    )
}