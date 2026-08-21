package com.example.esewa_project.ui.compose

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.example.esewa_project.data.local.entity.ProductEntity
import com.example.esewa_project.ui.viewmodel.CartViewModel
import com.example.esewa_project.ui.viewmodel.FavouriteViewModel
import kotlinx.coroutines.launch

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
    onProductClick: (Int) -> Unit,
    onDeleteSwipe: (ProductEntity) -> Unit,
    onContinueShoppingClick: () -> Unit,
    onUndoDelete: (List<ProductEntity>) -> Unit,
) {
    val productsState by favouriteViewModel.favouriteProducts.collectAsState()
    val cartCount by cartViewModel.cartCount.collectAsState(initial = 0)

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val products = productsState ?: return
    FavouriteScreenContent(
        products = products,
        cartCount = cartCount,
        selectedProductIds = selectedProductIds,
        snackbarHostState = snackbarHostState,
        onSelectAllClick = onSelectAllClick,
        onBackClick = onBackClick,
        onCartClick = onCartClick,
        onCheckoutClick = onCheckoutClick,
        onProductClick = onProductClick,
        onContinueShoppingClick = onContinueShoppingClick,

        onDeleteSwipe = { product ->
            onDeleteSwipe(product)
            scope.launch {
                snackbarHostState.currentSnackbarData?.dismiss()
                val result = snackbarHostState.showSnackbar(
                    message = "(1) item has been deleted.",
                    actionLabel = "UNDO",
                    duration = SnackbarDuration.Short
                )
                if (result == SnackbarResult.ActionPerformed) {
                    onUndoDelete(listOf(product))

                    val cartResult = snackbarHostState.showSnackbar(
                        message = "Added to cart successfully.",
                        actionLabel = "GOTO CART",
                        duration = SnackbarDuration.Short
                    )
                    if (cartResult == SnackbarResult.ActionPerformed) {
                        onCartClick()
                    }
                }
            }
        },
        onDeleteAllClick = {
            val deletedItems = products.filter { it.id in selectedProductIds }
            if (deletedItems.isNotEmpty()) {
                val count = deletedItems.size
                onDeleteAllClick()

                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    val result = snackbarHostState.showSnackbar(
                        message = "($count) items have been deleted.",
                        actionLabel = "UNDO",
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        onUndoDelete(deletedItems)

                        val cartResult = snackbarHostState.showSnackbar(
                            message = "Added to cart successfully.",
                            actionLabel = "GOTO CART",
                            duration = SnackbarDuration.Short
                        )
                        if (cartResult == SnackbarResult.ActionPerformed) {
                            onCartClick()
                        }
                    }
                }
            }
        }
    )
}