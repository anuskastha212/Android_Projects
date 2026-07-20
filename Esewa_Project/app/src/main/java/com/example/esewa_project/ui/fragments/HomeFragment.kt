package com.example.esewa_project.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.esewa_project.BannerIndicator
import com.example.esewa_project.R
import com.example.esewa_project.data.api.RetrofitInstance
import com.example.esewa_project.data.source.BannerImages
import com.example.esewa_project.data.source.CategoryData
import com.example.esewa_project.databinding.FragmentHomeBinding
import com.example.esewa_project.ui.adapter.BannerAdapter
import com.example.esewa_project.ui.adapter.CategoryAdapter
import com.example.esewa_project.ui.adapter.MostPopularAdapter
import com.example.esewa_project.ui.adapter.ProductAdapter
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import kotlinx.coroutines.launch
import kotlin.collections.take


class HomeFragment : Fragment(R.layout.fragment_home){
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewPager: ViewPager2
    private lateinit var layoutDots: LinearLayout
    private lateinit var dots: Array<ImageView?>

    private val bannerIndicator by lazy { BannerIndicator() }
    private val categoryData by lazy { CategoryData() }
    private val bannerImages by lazy { BannerImages() }

    private lateinit var productAdapter: ProductAdapter
    private lateinit var mostPopularAdapter: MostPopularAdapter

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
        getProducts()
        setupMostPopularRecyclerView()
        fetchCategories()
    }
    private fun setupBanner() {
        viewPager = binding.viewPagerBanner
        layoutDots = binding.layoutDots

        val imagesList = bannerImages.getBannerImages()
        viewPager.adapter = BannerAdapter(imagesList)

        dots = bannerIndicator.setupIndicator(requireContext(), imagesList.size, layoutDots)
        bannerIndicator.setCurrentIndicator(requireContext(), 0, layoutDots)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bannerIndicator.setCurrentIndicator(requireContext(), position, layoutDots)
            }
        })
    }

    private fun setupCategories() {
        binding.rvCategories.adapter = CategoryAdapter(categoryData.getCategoryData())
    }
    private fun setupFeaturedProductRecyclerView() = binding.rvProducts.apply {

        productAdapter = ProductAdapter { product ->
            Toast.makeText(
                requireContext(),
                product.title,
                Toast.LENGTH_SHORT
            ).show()
        }

        adapter = productAdapter

        layoutManager = GridLayoutManager(    requireContext(),2)
    }

    private fun getProducts() {

        viewLifecycleOwner.lifecycleScope.launch {

            try {

                val response = RetrofitInstance.apiInterface.getData()

                if (response.isSuccessful) {

                    val apiResponse = response.body()
                    val actualProducts = apiResponse?.data

                    if (actualProducts != null) {
                        productAdapter.products = actualProducts.take(4)
                    }

                } else {
                    Log.e("HomeFragment", "API Error: ${response.message()}")
                }

            } catch (e: Exception) {
                Log.e("HomeFragment", "Exception: ${e.message}")
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
                    "Clicked: $categoryData",
                    Toast.LENGTH_SHORT)
                    .show()
            }
            binding.rvMostPopular.apply{
                layoutManager = flexboxLayoutManager
                adapter= mostPopularAdapter
            }
    }

    private fun fetchCategories() {

        viewLifecycleOwner.lifecycleScope.launch {

            try {
                val response = RetrofitInstance.apiInterface.getMostPopular()

                if (response.isSuccessful) {
                    response.body()?.let {
                        mostPopularAdapter.updateData(it)
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