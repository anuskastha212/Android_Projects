package com.example.esewa_project.ui.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.example.esewa_project.R
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.esewa_project.LoginActivity
import com.example.esewa_project.ProductDetailActivity
import com.example.esewa_project.databinding.FragmentHomeBinding
import com.example.esewa_project.ui.adapter.BannerAdapter
import com.example.esewa_project.ui.adapter.CategoryAdapter
import com.example.esewa_project.ui.adapter.MostPopularAdapter
import com.example.esewa_project.ui.adapter.AllProductAdapter
import com.example.esewa_project.ui.viewmodel.CartViewModel
import com.example.esewa_project.ui.viewmodel.HomeViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlin.collections.take
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home){

    private val homeViewModel: HomeViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var featuredProductAdapter: AllProductAdapter
    private lateinit var mostPopularAdapter: MostPopularAdapter
    private lateinit var hotDealsAdapter: AllProductAdapter
    private lateinit var popularBrandAdapter: AllProductAdapter
    private lateinit var recommendedAdapter: AllProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val menu = binding.homeToolBar.menu
        menu.findItem(R.id.action_cart).isVisible= false

        binding.homeToolBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId){
                R.id.action_notifications -> {
                    Toast.makeText(
                        requireContext(),
                        "Notifications Clicked",
                        Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.action_options -> {
                    Toast.makeText(
                        requireContext(),
                        "Options Clicked",
                        Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }

        setupBanner()
        setupCategories()
        setupRecyclerViews()
        setupMostPopularRecyclerView()

        homeViewModel.fetchData()

        homeViewModel.products.observe(viewLifecycleOwner){ allProducts ->
            featuredProductAdapter.products = allProducts.take(7)
            hotDealsAdapter.products = allProducts.drop(7).take(7)
            popularBrandAdapter.products = allProducts.drop(14).take(4)
            recommendedAdapter.products = allProducts.drop(18).take(30)
        }

        homeViewModel.popularCategories.observe(viewLifecycleOwner){categories ->
            mostPopularAdapter.mostPopular = categories.take(7)
        }

        lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                cartViewModel.cartQuantities.collect { quantities ->
                    featuredProductAdapter.currentQuantities = quantities
                    featuredProductAdapter.notifyDataSetChanged()

                    hotDealsAdapter.currentQuantities = quantities
                    hotDealsAdapter.notifyDataSetChanged()

                    popularBrandAdapter.currentQuantities = quantities
                    popularBrandAdapter.notifyDataSetChanged()

                    recommendedAdapter.currentQuantities = quantities
                    recommendedAdapter.notifyDataSetChanged()
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                cartViewModel.navigateToLogin.collect {
                    showLoginRequiredDialog()
                }
            }
        }
    }

    private fun showLoginRequiredDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Sign In Required")
            .setMessage("You need an account to add items to your shopping cart. Would you like to sign in now?")
            .setPositiveButton("Sign In") { _, _ ->
                startActivity(Intent(requireContext(), LoginActivity::class.java))
            }
            .setNegativeButton("Maybe Later", null)
            .show()
    }
    private fun setupBanner() {
        val imagesList = homeViewModel.banners
        binding.viewPagerBanner.adapter = BannerAdapter(imagesList)

        TabLayoutMediator(
            binding.layoutDots,
            binding.viewPagerBanner
        ) { _, _ ->
        }.attach()
    }

    private fun setupCategories() {
        binding.rvCategories.adapter = CategoryAdapter(homeViewModel.localCategories)
        { category ->
            Toast.makeText(
                requireContext(),
                category.name,
                Toast.LENGTH_SHORT
            ).show()

        }
    }

    private fun setupRecyclerViews(){
        featuredProductAdapter = createProductAdapter()
        binding.rvFeaturedProducts.apply {
            adapter = featuredProductAdapter
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false)
        }

        hotDealsAdapter = createProductAdapter()
        binding.rvHotDeals.apply {
            adapter = hotDealsAdapter
            layoutManager =
                LinearLayoutManager(
                    requireContext(),
                    LinearLayoutManager.HORIZONTAL,
                    false)
        }

        popularBrandAdapter = createProductAdapter()
        binding.rvPopularBrands.apply {
            adapter = popularBrandAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }

        recommendedAdapter = createProductAdapter()
        binding.rvRecommended.apply {
            adapter = recommendedAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    private fun setupMostPopularRecyclerView() {

        val flexboxLayoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }

        mostPopularAdapter = MostPopularAdapter { categoryData ->

            Toast.makeText(
                requireContext(),
                categoryData,
                Toast.LENGTH_SHORT
            ).show()
        }

        binding.rvMostPopular.apply {
            layoutManager = flexboxLayoutManager
            adapter = mostPopularAdapter
            isNestedScrollingEnabled = false
        }
    }


    private fun createProductAdapter(): AllProductAdapter {
        return AllProductAdapter(
            onClick = { product ->
                val intent = Intent(requireContext(), ProductDetailActivity::class.java)
                intent.putExtra("product_id", product.id)
                startActivity(intent)
            },
            onFavouriteClick = { product ->

            },
            onIncrementClick = { product->
                cartViewModel.updateQuantity(product.id, 1)
            },
            onDecrementClick = { product ->
                cartViewModel.updateQuantity(product.id, -1)
            }
        )
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}