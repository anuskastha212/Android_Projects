package com.example.esewa_project

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.esewa_project.data.api.RetrofitInstance
import com.example.esewa_project.data.model.Product
import com.example.esewa_project.data.source.ColorsData
import com.example.esewa_project.data.source.LocalDataStore
import com.example.esewa_project.databinding.ActivityProductDetailBinding
import com.example.esewa_project.ui.adapter.ProductColorAdapter
import kotlinx.coroutines.launch
import com.example.esewa_project.ui.adapter.ProductDetailAdapter
import com.example.esewa_project.ui.adapter.ProductSizeAdapter
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.first

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding
    private lateinit var productSizeAdapter: ProductSizeAdapter
    private lateinit var productColorAdapter: ProductColorAdapter
    private val colorsData by lazy { ColorsData() }
    private val localDataStore by lazy { LocalDataStore(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

//        ViewCompat.setOnApplyWindowInsetsListener(binding.productDetail) { view, insets ->
//
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//
//            view.setPadding(
//                systemBars.left,
//                systemBars.top,
//                systemBars.right,
//                systemBars.bottom
//            )
//
//            insets
//        }

        val productId = intent.getIntExtra("product_id", -1)

        if (productId == -1) {
            finish()
            return
        }

        getProductById(productId)
    }

    private fun getProductById(id: Int) {

        lifecycleScope.launch {

            try {

                val product = RetrofitInstance.api.getProductById(id)

                showProduct(product)

            } catch (e: Exception) {

                Log.e("ProductDetail", "Error", e)

                Toast.makeText(
                    this@ProductDetailActivity,
                    e.message ?: "Something went wrong",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showProduct(product: Product) {

        binding.apply {
            tvProductName.text = product.title
            bottomProductName.text = product.title
            productDescription.text = product.description
            tvProductPrice.text = getString(R.string.product_price, product.price)
            bottomProductPrice.text = getString(R.string.product_price, product.price)

            val adapter = ProductDetailAdapter(product.images)
            productImageDet.adapter = adapter
            TabLayoutMediator(indicatorProductImage, productImageDet){tab,position ->
            }.attach()

            Log.d("ProductDetail", "Available options: ${product.options.keys}")
            Log.d("ProductDetail", "Tags content: ${product.options["Tags"]}")

            rvProductSize.layoutManager = LinearLayoutManager(
                this@ProductDetailActivity,
                LinearLayoutManager.HORIZONTAL,
                false)

            val sizes = product.options["Size"] ?: emptyList()
            productSizeAdapter = ProductSizeAdapter({ selectedSize ->
                Toast.makeText(
                    this@ProductDetailActivity,
                    "Selected Size: $selectedSize",
                    Toast.LENGTH_SHORT).show()
            }, sizes)
            rvProductSize.adapter = productSizeAdapter

            rvProductColors.layoutManager = LinearLayoutManager(
                this@ProductDetailActivity,
                LinearLayoutManager.HORIZONTAL,
                false)
            val colors = colorsData.getColorData()
            productColorAdapter = ProductColorAdapter({ selectedColor ->
                Toast.makeText(
                    this@ProductDetailActivity,
                    "Selected Color: $selectedColor",
                    Toast.LENGTH_SHORT).show()
            }, colors)
            rvProductColors.adapter = productColorAdapter

            btnAddToCart.setOnClickListener {
                lifecycleScope.launch {
                    val currentMap = localDataStore.cartMap.first().toMutableMap()
                    val currentQty = currentMap[product.id] ?: 0
                    currentMap[product.id] = currentQty + 1
                    localDataStore.saveCart(currentMap)
                    localDataStore.updateCount(1)
                    Toast.makeText(
                        this@ProductDetailActivity,
                        "Added to Cart!",
                        Toast.LENGTH_SHORT).show()
                }
            }

            favouriteButton.setOnClickListener {
                lifecycleScope.launch {
                    localDataStore.toggleFavourite(product.id)
                }
            }

            lifecycleScope.launch{
                localDataStore.favouriteIds.collect{ids->
                    val isFav = ids.contains(product.id.toString())
                    
                    binding.favouriteIcon.imageTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(this@ProductDetailActivity, if (isFav) R.color.white else R.color.light_gray)
                    )
                }
            }
        }
    }
}
