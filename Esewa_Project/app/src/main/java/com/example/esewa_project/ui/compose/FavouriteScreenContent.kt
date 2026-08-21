package com.example.esewa_project.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.esewa_project.data.local.entity.ProductEntity
import com.example.esewa_project.ui.util.FavouriteSnackbar

@Composable
fun FavouriteScreenContent(
    products: List<ProductEntity>,
    cartCount: Int,
    selectedProductIds: Set<Int>,
    onSelectAllClick: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onCartClick: () -> Unit,
    onCheckoutClick: (ProductEntity) -> Unit,
    onDeleteAllClick: () -> Unit,
    onProductClick: (Int) -> Unit,
    onDeleteSwipe: (ProductEntity) -> Unit,
    onContinueShoppingClick: () -> Unit,
    snackbarHostState: SnackbarHostState,
) {
    val allSelected = products.isNotEmpty() && selectedProductIds.size == products.size

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = 40.dp)
            ) { data ->
                FavouriteSnackbar(snackbarData = data)
            }
        },
        topBar = {
            FavouriteTopBar(
                cartCount = cartCount,
                onBackClick = onBackClick,
                onCartClick = onCartClick
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8F9FA))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (products.isNotEmpty()){
                    Checkbox(
                        checked = allSelected,
                        onCheckedChange = onSelectAllClick,
                        colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2ABB00))
                    )
                }

                Text(
                    text = "Items (${products.size})",
                    fontSize = 14.sp,
                    color = Color(0xFF555770),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                if (allSelected && products.isNotEmpty()) {
                    Text(
                        text = "DELETE ALL",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF555770),
                        modifier = Modifier.clickable { onDeleteAllClick() }
                    )
                }
            }

            if(products.isEmpty()){
                FavouriteEmptyState(onContinueShoppingClick = onContinueShoppingClick)
            }else {

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(products, key = { it.id }) { product ->
                        val isSelected = selectedProductIds.contains(product.id)

                        FavouriteProductCard(
                            product = product,
                            isSelected = isSelected,
                            onCheckoutClick = { onCheckoutClick(product) },
                            onProductClick = { onProductClick(product.id) },
                            onDeleteSwipe = { onDeleteSwipe(product) }
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun FavouriteScreenPreview() {
    val mockProducts = listOf(
        ProductEntity(1, "Jacket in nylon", 19500.0, "", "CELINE"),
        ProductEntity(2, "Jacket in nylon", 19500.0, "", "CELINE"),
        ProductEntity(3, "Jacket in nylon", 19500.0, "", "CELINE")
    )
    FavouriteScreenContent(
        products = mockProducts,
        cartCount = 9,
        selectedProductIds = emptySet(),
        onSelectAllClick = {},
        onBackClick = {},
        onCartClick = {},
        onCheckoutClick = {},
        onDeleteAllClick = {},
        onProductClick = {},
        onDeleteSwipe = {},
        onContinueShoppingClick = {},
        snackbarHostState = SnackbarHostState()
    )
}