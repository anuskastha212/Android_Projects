package com.example.esewa_project.ui.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.esewa_project.data.local.entity.ProductEntity
import com.example.esewa_project.ui.viewmodel.CartViewModel
import com.example.esewa_project.ui.viewmodel.FavouriteViewModel

@Composable
fun FavouriteScreen(
    favouriteViewModel: FavouriteViewModel,
    cartViewModel: CartViewModel,
    selectedProductIds: Set<Int>,
    onSelectAllClick: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onCheckoutClick: (ProductEntity) -> Unit,
    onDeleteAllClick: () -> Unit,
    onProductClick: (Int) -> Unit
) {
    val products by favouriteViewModel.favouriteProducts.collectAsState(initial = emptyList())
    val cartCount by cartViewModel.cartCount.collectAsState(initial = 0)

    FavouriteScreenContent(
        products = products,
        cartCount = cartCount,
        selectedProductIds = selectedProductIds,
        onSelectAllClick = onSelectAllClick,
        onBackClick = onBackClick,
        onCartClick = onCartClick,
        onCheckoutClick = onCheckoutClick,
        onDeleteAllClick = onDeleteAllClick,
        onProductClick = onProductClick
    )
}