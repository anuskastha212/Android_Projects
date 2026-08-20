package com.example.esewa_project.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.esewa_project.ui.viewmodel.CartViewModel
import com.example.esewa_project.ui.viewmodel.FavouriteViewModel

@Composable
fun FavouriteScreen(
    favouriteViewModel: FavouriteViewModel,
    cartViewModel: CartViewModel,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit
) {
    val products by favouriteViewModel.favouriteProducts.collectAsState(initial = emptyList())
    val favouriteCount by favouriteViewModel.favouriteCount.collectAsState(initial = 0)
    val cartCount by cartViewModel.cartCount.collectAsState(initial = 0)

    FavouriteScreenContent(
        products = products,
        cartCount = cartCount,
        favouriteCount = favouriteCount,
        onBackClick = onBackClick,
        onCartClick = onCartClick
    )
}