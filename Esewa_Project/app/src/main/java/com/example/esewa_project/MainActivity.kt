package com.example.esewa_project

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.esewa_project.data.api.RetrofitInstance
import com.example.esewa_project.ui.adapter.BannerAdapter
import com.example.esewa_project.ui.adapter.CategoryAdapter
import com.example.esewa_project.ui.adapter.ProductAdapter
import com.example.esewa_project.data.source.CategoryData
import com.example.esewa_project.databinding.ActivityMainBinding
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var productAdapter: ProductAdapter
    private lateinit var viewPager: ViewPager2
    private lateinit var layoutDots: LinearLayout
    private lateinit var dots: Array<ImageView?>

    private val bannerImages = listOf(
        R.drawable.banner1,
        R.drawable.banner2,
        R.drawable.banner3,
    )

    private val bannerIndicator by lazy { BannerIndicator() }
    private val categoryData by lazy { CategoryData() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupFeaturedProductRecyclerView()

        val john = findViewById<TextView>(R.id.john)

        john.text = HtmlCompat.fromHtml(
            getString(R.string.john),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        viewPager = findViewById(R.id.view_pager_banner)
        layoutDots = findViewById(R.id.layout_dots)

        val adapter = BannerAdapter(bannerImages)
        viewPager.adapter = adapter

        dots = bannerIndicator.setupIndicator(
            this@MainActivity,
            bannerImages.size,
            layoutDots
        )
        bannerIndicator.setCurrentIndicator(
            this@MainActivity,
            0,
            layoutDots
        )

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                bannerIndicator.setCurrentIndicator(
                    this@MainActivity,
                    position,
                    layoutDots
                )
            }
        })

        val rvCategories = findViewById<RecyclerView>(R.id.rv_categories)
        rvCategories.adapter = CategoryAdapter(categoryData.getCategoryData())

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.apiInterface.getData()

                if (response.isSuccessful) {
                    val productList = response.body()

                    if (productList != null) {
                        productAdapter.submitList(productList)                    }
                } else {
                    Log.e("MainActivity", "API Error: ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("MainActivity", "Exception: ${e.message}")
            }
        }
    }

    private fun setupFeaturedProductRecyclerView() = binding.rvProducts.apply {

        productAdapter = ProductAdapter { product ->
            Toast.makeText(
                this@MainActivity,
                product.title,
                Toast.LENGTH_SHORT
            ).show()
        }

        adapter = productAdapter

        layoutManager = LinearLayoutManager(
            this@MainActivity,
            LinearLayoutManager.HORIZONTAL,
            false
        )
    }
}