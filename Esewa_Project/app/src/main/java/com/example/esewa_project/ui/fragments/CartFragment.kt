package com.example.esewa_project.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.esewa_project.LoginActivity
import com.example.esewa_project.ProductDetailActivity
import com.example.esewa_project.R
import com.example.esewa_project.RegisterActivity
import com.example.esewa_project.databinding.FragmentCartBinding
import com.example.esewa_project.ui.adapter.AllProductAdapter
import com.example.esewa_project.ui.adapter.CartAdapter
import com.example.esewa_project.ui.viewmodel.CartViewModel
import com.example.esewa_project.ui.viewmodel.FavouriteViewModel
import com.example.esewa_project.ui.viewmodel.HomeViewModel
import kotlinx.coroutines.launch
import java.util.Locale
import kotlin.text.format

class CartFragment : Fragment(R.layout.fragment_cart) {

    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private val cartViewModel: CartViewModel by viewModels()
    private val favouriteViewModel: FavouriteViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    private lateinit var cartAdapter: CartAdapter
    private lateinit var recommendedAdapter: AllProductAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentCartBinding.bind(view)

        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.loginButton.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
        }

        binding.registerButton.setOnClickListener {
            startActivity(Intent(requireContext(), RegisterActivity::class.java))
        }

        setupAdapters()
        observeData()
        homeViewModel.fetchData()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.userSession.collect { uid ->
                    val isLoggedIn = uid.isNotEmpty()

                    if (isLoggedIn) {
                        binding.layoutGuest.visibility = View.GONE
                        binding.layoutCartContent.visibility = View.VISIBLE
                    } else {
                        binding.layoutGuest.visibility = View.VISIBLE
                        binding.layoutCartContent.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun setupAdapters() {
        cartAdapter = CartAdapter(
            onIncrementClick = { id ->
                cartViewModel.updateQuantity(id, 1) },
            onDecrementClick = { id ->
                cartViewModel.updateQuantity(id, -1) }
        )
        binding.rvCartItems.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        recommendedAdapter = AllProductAdapter(
            onClick = { product ->
                val intent = Intent(requireContext(), ProductDetailActivity::class.java)
                intent.putExtra("product_id", product.id)
                startActivity(intent)
            },
            onFavouriteClick = { product ->
                favouriteViewModel.toggleFavourite(product.id)
            },
            onIncrementClick = { product ->
                cartViewModel.updateQuantity(product.id, 1)
            },
            onDecrementClick = { product ->
                cartViewModel.updateQuantity(product.id, -1)
            }
        )
        binding.rvRecommended.apply {
            adapter = recommendedAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.cartCount.collect { count->
                    binding.cartBadge.apply {
                        text = String.format(Locale.getDefault(), "%02d", count)
                        visibility = if (count > 0) View.VISIBLE else View.GONE
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.cartItems.collect { itemsMap ->
                    val list = itemsMap.toList()
                    cartAdapter.submitList(list)
                    binding.itemCount.text = getString(R.string.items_count, list.size)
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.totalAmount.collect { total: Double ->
                    binding.tvCheckoutTotal.text = getString(R.string.product_price, total)
                }
            }
        }

        homeViewModel.products.observe(viewLifecycleOwner) { products ->
            recommendedAdapter.products = products.drop(18).take(30)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.cartQuantities.collect { quantities ->
                    recommendedAdapter.currentQuantities = quantities
                    recommendedAdapter.notifyDataSetChanged()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}