package com.example.esewa_project.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.example.esewa_project.ProductDetailActivity
import com.example.esewa_project.data.source.LocalDataStore
import com.example.esewa_project.databinding.FragmentHomeBinding
import com.example.esewa_project.ui.adapter.BannerAdapter
import com.example.esewa_project.ui.adapter.CategoryAdapter
import com.example.esewa_project.ui.adapter.MostPopularAdapter
import com.example.esewa_project.ui.adapter.AllProductAdapter
import com.example.esewa_project.ui.viewmodel.HomeViewModel
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlin.collections.take
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.launch

class HomeFragment : Fragment(R.layout.fragment_home){

    private val viewModel: HomeViewModel by viewModels()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var featuredProductAdapter: AllProductAdapter
    private lateinit var mostPopularAdapter: MostPopularAdapter
    private lateinit var hotDealsAdapter: AllProductAdapter
    private lateinit var popularBrandAdapter: AllProductAdapter
    private lateinit var recommendedAdapter: AllProductAdapter

    private val localDataStore by lazy { LocalDataStore(requireContext()) }

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
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED){
                viewModel.cartQuantities.collect { c ->
                    Log.d("Cart", "${c.keys}")
                }
            }
        }
//        lifecycleScope.launch {
//            viewLifecycleOwner.lifecycleScope(Life
//
//            Log.d("Cart", "${viewModel.cartQuantities.value}")
//        }
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

        viewModel.fetchData()

        viewModel.products.observe(viewLifecycleOwner){ allProducts ->
            featuredProductAdapter.products = allProducts.take(7)
            hotDealsAdapter.products = allProducts.drop(7).take(7)
            popularBrandAdapter.products = allProducts.drop(14).take(4)
            recommendedAdapter.products = allProducts.drop(18).take(30)
        }

        viewModel.popularCategories.observe(viewLifecycleOwner){categories ->
            mostPopularAdapter.mostPopular = categories.take(7)
        }

        lifecycleScope.launch {
            viewModel.cartQuantities.collect { quantities ->
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
        lifecycleScope.launch {
            localDataStore.favouriteIds.collect { ids ->
                featuredProductAdapter.favouriteIds = ids
                featuredProductAdapter.notifyDataSetChanged()

                hotDealsAdapter.favouriteIds = ids
                hotDealsAdapter.notifyDataSetChanged()

                popularBrandAdapter.favouriteIds = ids
                popularBrandAdapter.notifyDataSetChanged()

                recommendedAdapter.favouriteIds = ids
                recommendedAdapter.notifyDataSetChanged()
            }
        }
    }
    private fun setupBanner() {
        val imagesList = viewModel.banners
        binding.viewPagerBanner.adapter = BannerAdapter(imagesList)

        TabLayoutMediator(
            binding.layoutDots,
            binding.viewPagerBanner
        ) { _, _ ->
        }.attach()
    }

    private fun setupCategories() {
        binding.rvCategories.adapter = CategoryAdapter(viewModel.localCategories)
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
                lifecycleScope.launch { localDataStore.toggleFavourite(product.id) }
            },
            onIncrementClick = { product, pos ->
                viewModel.updateQuantity(product.id, 1)
                lifecycleScope.launch {
                    localDataStore.updateCount(1)
                }
            },
            onDecrementClick = { product, pos ->
                val currentQty = viewModel.cartQuantities.value[product.id] ?: 0
                if (currentQty > 0 ) {
                    viewModel.updateQuantity(product.id, -1)
                    lifecycleScope.launch { localDataStore.updateCount(-1) }
                }
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}