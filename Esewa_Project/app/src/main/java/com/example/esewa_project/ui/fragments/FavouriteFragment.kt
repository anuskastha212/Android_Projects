package com.example.esewa_project.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.example.esewa_project.MainActivity
import com.example.esewa_project.R
import com.example.esewa_project.ui.compose.FavouriteScreen
import com.example.esewa_project.ui.viewmodel.CartViewModel
import com.example.esewa_project.ui.viewmodel.FavouriteViewModel

class FavouriteFragment : Fragment() {
    private val favViewModel: FavouriteViewModel by viewModels()
    private val cartViewModel: CartViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                var selectedProductIds by remember { mutableStateOf(emptySet<Int>()) }
                val products by favViewModel.favouriteProducts.collectAsState()

                FavouriteScreen(
                    favouriteViewModel = favViewModel,
                    cartViewModel = cartViewModel,
                    selectedProductIds = selectedProductIds,
                    onSelectAllClick = { isChecked ->
                        selectedProductIds = if (isChecked) {
                            products?.map { it.id }?.toSet() ?: emptySet()
                        } else {
                            emptySet()
                        }
                    },

                    onBackClick = {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    },
                    onCartClick = {
                        val mainActivity = requireActivity() as? MainActivity
                        mainActivity?.findViewById<View>(R.id.navItemCart)?.performClick()
                    },
                    onCheckoutClick = { product ->
                        cartViewModel.updateQuantity(product.id, 1)
                        Toast.makeText(
                            context,
                            "Added",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    onDeleteAllClick = {
                        favViewModel.clearAllFavourites()
                        selectedProductIds = emptySet()
                    },
                    onProductClick = { productId ->
                        selectedProductIds = if (selectedProductIds.contains(productId)) {
                            selectedProductIds - productId
                        } else {
                            selectedProductIds + productId
                        }
                    },
                    onDeleteSwipe = { product ->
                        favViewModel.removeFavourite(product.id)
                        selectedProductIds = selectedProductIds - product.id
                    },
                    onUndoDelete = { restoredProducts ->
                        restoredProducts.forEach { product ->
                            favViewModel.toggleFavourite(product.id)
                        }
                    },
                    onContinueShoppingClick = {
                        val mainActivity = requireActivity() as? MainActivity
                        mainActivity?.findViewById<View>(R.id.navItemShop)?.performClick()
                    }
                )
            }
        }
    }
}