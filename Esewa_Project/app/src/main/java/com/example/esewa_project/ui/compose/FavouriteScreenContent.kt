package com.example.esewa_project.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.esewa_project.R
import com.example.esewa_project.data.local.entity.ProductEntity
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
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
    onProductClick: (Int) -> Unit
) {
    val allSelected = products.isNotEmpty() && selectedProductIds.size == products.size

    Scaffold(
        topBar = {
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
                                        text = String.format(
                                            Locale.getDefault(),
                                            "%02d",
                                            cartCount
                                        ),
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
                Checkbox(
                    checked = allSelected,
                    onCheckedChange = onSelectAllClick,
                    colors = CheckboxDefaults.colors(checkedColor = Color(0xFF2ABB00))
                )

                Text(
                    text = "Items (${products.size})",
                    fontSize = 14.sp,
                    color = Color(0xFF555770),
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )

                if (allSelected) {
                    Text(
                        text = "DELETE ALL",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF555770),
                        modifier = Modifier.clickable { onDeleteAllClick() }
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(products) { product ->
                    val isSelected = selectedProductIds.contains(product.id)

                    FavouriteItemRow(
                        product = product,
                        isSelected = isSelected,
                        onCheckoutClick = { onCheckoutClick(product) },
                        onProductClick = { onProductClick(product.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun FavouriteItemRow(
    product: ProductEntity,
    isSelected: Boolean,
    onCheckoutClick: () -> Unit,
    onProductClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
                .clickable { onProductClick() },
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFFF1F1F1)),
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = product.thumbnail,
                        contentDescription = null,
                        modifier = Modifier.size(70.dp),
                        contentScale = ContentScale.Fit
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 16.dp)
                ) {
                    Text(
                        text = product.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF292A40),
                        maxLines = 1
                    )
                    Text(
                        text = product.categoryName.uppercase(),
                        fontSize = 11.sp,
                        color = Color(0xFFA8AABB),
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Rs. ${String.format(Locale.getDefault(), "%,.2f", product.price)}",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF292A40)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.End,
                    modifier = Modifier.fillMaxHeight(),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.option),
                        contentDescription = "Options",
                        tint = Color(0xFFA8AABB),
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    if (isSelected) {
                        IconButton(
                            onClick = onCheckoutClick,
                            modifier = Modifier
                                .size(44.dp)
                                .background(Color(0xFF2ABB00), shape = RoundedCornerShape(10.dp))
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.checkout),
                                contentDescription = "Checkout",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    } else {
                        Spacer(modifier = Modifier.size(44.dp))
                    }
                }
            }
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .background(Color(0xFF2ABB00), shape = CircleShape)
                    .border(2.dp, Color.White, CircleShape)
                    .align(Alignment.TopStart),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
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
        onProductClick = {}
    )
}