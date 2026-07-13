package com.example.esewa_project

import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import android.widget.Toast
import android.widget.LinearLayout
import androidx.core.text.HtmlCompat
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2

class MainActivity : AppCompatActivity(){

    private lateinit var viewPager: ViewPager2
    private lateinit var layoutDots: LinearLayout
    private lateinit var dots: Array<ImageView?>

    private val bannerImages = listOf(
        R.drawable.banner1,
        R.drawable.banner2,
        R.drawable.banner3,
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val john = findViewById<TextView>(R.id.john)

        john.text = HtmlCompat.fromHtml(
            getString(R.string.John),
            HtmlCompat.FROM_HTML_MODE_LEGACY
        )

        viewPager = findViewById(R.id.view_pager_banner)
        layoutDots = findViewById(R.id.layout_dots)

        val adapter = BannerAdapter(bannerImages)
        viewPager.adapter=adapter

        setupIndicators(bannerImages.size)

        setCurrentIndicator(0)

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
    }
    private fun setupIndicators(count: Int) {
        dots = arrayOfNulls(count)
        val params = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).apply { setMargins(8, 0, 8, 0) }

        for (i in 0 until count) {
            dots[i] = ImageView(this)
            dots[i]?.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.indicator_inactive))
            layoutDots.addView(dots[i], params)
        }
    }

    private fun setCurrentIndicator(index: Int) {
        val childCount = layoutDots.childCount
        for (i in 0 until childCount) {
            val imageView = layoutDots.getChildAt(i) as ImageView
            if (i == index) {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.indicator_active
                    )
                )
            } else {
                imageView.setImageDrawable(
                    ContextCompat.getDrawable(
                        this,
                        R.drawable.indicator_inactive
                    )
                )
            }
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}