package com.example.esewa_project

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.esewa_project.ui.adapter.BannerAdapter
import com.example.esewa_project.ui.adapter.CategoryAdapter
import com.example.esewa_project.ui.adapter.ProductAdapter
import com.example.esewa_project.data.source.CategoryData
import com.example.esewa_project.databinding.ActivityMainBinding

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

//        setupIndicators(bannerImages.size)

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
    }

    private fun setupFeaturedProductRecyclerView() = binding.rvProducts.apply {
        productAdapter = ProductAdapter()
        adapter = productAdapter
        layoutManager = LinearLayoutManager(this@MainActivity)
    }

//    private fun setupIndicators(count: Int) {
//        dots = arrayOfNulls(count)
//        val params = LinearLayout.LayoutParams(
//            LinearLayout.LayoutParams.WRAP_CONTENT,
//            LinearLayout.LayoutParams.WRAP_CONTENT
//        ).apply { setMargins(8, 0, 8, 0) }
//
//        for (i in 0 until count) {
//            dots[i] = ImageView(this)
//            dots[i]?.setImageDrawable(
//                ContextCompat.getDrawable(
//                    this,
//                    R.drawable.indicator_inactive
//                )
//            )
//            layoutDots.addView(dots[i], params)
//        }
//    }

//    private fun setCurrentIndicator(index: Int) {
//        val childCount = layoutDots.childCount
//        for (i in 0 until childCount) {
//            val imageView = layoutDots.getChildAt(i) as ImageView
//            if (i == index) {
//                imageView.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        this,
//                        R.drawable.indicator_active
//                    )
//                )
//            } else {
//                imageView.setImageDrawable(
//                    ContextCompat.getDrawable(
//                        this,
//                        R.drawable.indicator_inactive
//                    )
//                )
//            }
//        }
//    }


}