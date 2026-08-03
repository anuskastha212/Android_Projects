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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.esewa_project.ProductDetailActivity
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

class HomeFragment : Fragment(R.layout.fragment_home){

    private val viewModel: HomeViewModel by viewModels()
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

    private fun createProductAdapter(): AllProductAdapter {
        return AllProductAdapter(

            onClick = { product ->
                val intent = Intent(requireContext(), ProductDetailActivity::class.java)
                intent.putExtra("product_id", product.id)
                startActivity(intent)
            },
            onAddClick = { product, pos, itemBinding ->
                itemBinding.apply{
                    layoutAdd.animate()
                        .translationY(-100f)
                        .alpha(0f)
                        .setDuration(150)
                        .withEndAction {
                            layoutAdd.visibility = View.GONE

                            addSub.visibility = View.VISIBLE
                            addSub.alpha = 0f
                            addSub.translationY = 100f

                            addSub.animate()
                                .translationY(0f)
                                .alpha(1f)
                                .setDuration(150)
                                .start()
                        }
                        .start()
                }
            },
            onPlusClick = { product, pos ->
                Toast.makeText(
                    requireContext(),
                    "Increased ${product.title}",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onMinusClick = { product, pos ->
                Toast.makeText(
                    requireContext(),
                    "Decreased ${product.title}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}