package com.example.esewa_project.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.esewa_project.ProductDetailActivity
import com.example.esewa_project.R
import com.example.esewa_project.data.api.RetrofitInstance
import com.example.esewa_project.data.source.BannerImages
import com.example.esewa_project.data.source.CategoryData
import com.example.esewa_project.databinding.FragmentHomeBinding
import com.example.esewa_project.ui.adapter.BannerAdapter
import com.example.esewa_project.ui.adapter.CategoryAdapter
import com.example.esewa_project.ui.adapter.MostPopularAdapter
import com.example.esewa_project.ui.adapter.FeaturedProductAdapter
import com.example.esewa_project.ui.adapter.HotDealsAdapter
import com.example.esewa_project.ui.adapter.PopularBrandAdapter
import com.example.esewa_project.ui.adapter.RecommendedAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.launch
import kotlin.collections.take
import com.google.android.material.tabs.TabLayoutMediator


class HomeFragment : Fragment(R.layout.fragment_home){
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val categoryData by lazy { CategoryData() }
    private val bannerImages by lazy { BannerImages() }

    private lateinit var featuredProductAdapter: FeaturedProductAdapter
    private lateinit var mostPopularAdapter: MostPopularAdapter
    private lateinit var hotDealsAdapter: HotDealsAdapter
    private lateinit var popularBrandAdapter: PopularBrandAdapter
    private lateinit var recommendedAdapter: RecommendedAdapter

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

        setupBanner()
        setupCategories()
        setupFeaturedProductRecyclerView()
        setupHotDealsRecyclerView()
        getAllProducts()
        setupPopularBrandRecyclerView()
        setupRecommendedRecyclerView()
        setupMostPopularRecyclerView()
        getMostPopularCategories()
    }
    private fun setupBanner() {
        val imagesList = bannerImages.getBannerImages()
        binding.viewPagerBanner.adapter = BannerAdapter(imagesList)

        TabLayoutMediator(
            binding.layoutDots,
            binding.viewPagerBanner
        ) { tab, position ->
        }.attach()
    }

    private fun setupCategories() {
        binding.rvCategories.adapter = CategoryAdapter(categoryData.getCategoryData())
        { category ->

            Toast.makeText(
                requireContext(),
                category.name,
                Toast.LENGTH_SHORT
            ).show()

        }
    }
    private fun setupFeaturedProductRecyclerView() = binding.rvFeaturedProducts.apply {

        featuredProductAdapter = FeaturedProductAdapter { product ->
            val intent = Intent(requireContext(), ProductDetailActivity::class.java)
            intent.putExtra("product_id", product.id)
            startActivity(intent)
        }

        adapter = featuredProductAdapter

        layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false)
        isNestedScrollingEnabled = false
    }

    private fun setupHotDealsRecyclerView() = binding.rvHotDeals.apply {

        hotDealsAdapter = HotDealsAdapter() { product ->
            val intent = Intent(requireContext(), ProductDetailActivity::class.java)
            intent.putExtra("product_id", product.id)
            startActivity(intent)
        }

        adapter = hotDealsAdapter

        layoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.HORIZONTAL,
            false)
        isNestedScrollingEnabled = false
    }

    private fun setupPopularBrandRecyclerView() = binding.rvPopularBrands.apply {

        popularBrandAdapter = PopularBrandAdapter() { product ->
            val intent = Intent(requireContext(), ProductDetailActivity::class.java)
            intent.putExtra("product_id", product.id)
            startActivity(intent)
        }

        adapter = popularBrandAdapter

        layoutManager = GridLayoutManager(requireContext(),2)
        isNestedScrollingEnabled = false
    }

    private fun setupRecommendedRecyclerView() = binding.rvRecommended.apply {

        recommendedAdapter = RecommendedAdapter() { product ->
            val intent = Intent(requireContext(), ProductDetailActivity::class.java)
            intent.putExtra("product_id", product.id)
            startActivity(intent)
        }

        adapter = recommendedAdapter

        layoutManager = GridLayoutManager(requireContext(),2)
        isNestedScrollingEnabled = false
    }

    private fun getAllProducts() {

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val products = RetrofitInstance.productApi.getAllProducts()

                featuredProductAdapter.products = products.take(7)
                hotDealsAdapter.products = products.drop(7).take(7)
                popularBrandAdapter.products = products.drop(14).take(4)
                recommendedAdapter.products = products.drop(18).take(8)

            } catch (e: Exception) {
                Log.e("API", "Exception", e)
            }
        }
    }

    private fun setupMostPopularRecyclerView(){
        val flexboxLayoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
            mostPopularAdapter = MostPopularAdapter(emptyList()){ categoryData ->
                Toast.makeText(requireContext(),
                    " $categoryData",
                    Toast.LENGTH_SHORT)
                    .show()
            }
            binding.rvMostPopular.apply{
                layoutManager = flexboxLayoutManager
                adapter= mostPopularAdapter
                isNestedScrollingEnabled = false
            }
    }
    private fun getMostPopularCategories() {

        viewLifecycleOwner.lifecycleScope.launch {

            try {
                val response = RetrofitInstance.api.getMostPopular()

                if (response.isSuccessful) {
                    response.body()?.let {categories ->
                        mostPopularAdapter.updateData(categories.take((7)))
                    }
                }
            } catch (e: Exception) {

                Toast.makeText(
                    requireContext(),
                    e.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}