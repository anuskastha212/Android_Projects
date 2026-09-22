package com.example.esewa_project

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.esewa_project.data.model.Product
import com.example.esewa_project.data.source.ColorsData
import com.example.esewa_project.databinding.ActivityProductDetailBinding
import com.example.esewa_project.ui.adapter.ProductColorAdapter
import com.example.esewa_project.ui.adapter.ProductDetailAdapter
import com.example.esewa_project.ui.adapter.ProductSizeAdapter
import com.example.esewa_project.ui.viewmodel.CartViewModel
import com.example.esewa_project.ui.viewmodel.FavouriteViewModel
import com.example.esewa_project.ui.viewmodel.ProductDetailViewModel
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    private val productDetailViewModel: ProductDetailViewModel by viewModels()
    private val cartViewModel: CartViewModel by viewModels()
    private val favouriteViewModel: FavouriteViewModel by viewModels()
    private val colorsData by lazy { ColorsData() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.productDetail) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                systemBars.left,
                0,
                systemBars.right,
                systemBars.bottom
            )
            insets
        }
        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        binding.btnCart.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java).apply {
                putExtra("OPEN_CART", true)
                flags = Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
            }
            startActivity(intent)
            finish()
        }

        val productId = intent.getIntExtra("product_id", -1)
        if (productId == -1) {
            finish()
            return
        }

        productDetailViewModel.loadDetails(productId)
        productDetailViewModel.product.observe(this) { product ->
            showProduct(product)
            setupClickListeners(product)
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                favouriteViewModel.favouriteIds.collectLatest { ids ->
                    val isFav = ids.contains(productId)
                    if (isFav) {
                        binding.favouriteButton.setCardBackgroundColor(
                            ContextCompat.getColor(
                                this@ProductDetailActivity,
                                R.color.green
                            )
                        )
                        binding.favouriteIcon.imageTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                this@ProductDetailActivity,
                                android.R.color.white
                            )
                        )
                    } else {
                        binding.favouriteButton.setCardBackgroundColor(
                            ContextCompat.getColor(
                                this@ProductDetailActivity,
                                R.color.green
                            )
                        )
                        binding.favouriteIcon.imageTintList = ColorStateList.valueOf(
                            ContextCompat.getColor(
                                this@ProductDetailActivity,
                                R.color.light_gray
                            )
                        )
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.cartQuantities.collect { quantities ->
                    val qty = quantities[productId] ?: 0
                    if (qty > 0) {
                        binding.btnAddToCart.visibility = View.GONE
                        binding.layoutQuantitySelector.visibility = View.VISIBLE
                        binding.tvQuantity.text = String.format(Locale.getDefault(), "%02d", qty)
                    } else {
                        binding.btnAddToCart.visibility = View.VISIBLE
                        binding.layoutQuantitySelector.visibility = View.GONE
                    }
                }
            }
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                cartViewModel.onItemAddedFirstTime.collect {
                    Snackbar.make(
                        binding.root,
                        "Added to cart successfully.",
                        Snackbar.LENGTH_LONG
                    ).apply {
                        anchorView = binding.bottomBar
                        setAction("GOTO CART") {
                            binding.btnCart.performClick()
                        }
                        setActionTextColor(
                            ContextCompat.getColor(
                                this@ProductDetailActivity,
                                R.color.green
                            )
                        )
                    }.show()
                }
            }
        }
        setupQuantityListeners(productId)
    }

    private fun setupQuantityListeners(productId: Int) {
        binding.btnIncrease.setOnClickListener {
            cartViewModel.updateQuantity(productId, 1)
        }
        binding.btnDecrease.setOnClickListener {
            cartViewModel.updateQuantity(productId, -1)
        }
    }

    private fun setupClickListeners(product: Product) {
        binding.btnAddToCart.setOnClickListener {
            cartViewModel.updateQuantity(product.id, 1)
        }
        binding.favouriteButton.setOnClickListener {
            favouriteViewModel.toggleFavourite(product.id)
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
            TabLayoutMediator(indicatorProductImage, productImageDet) { _, _ ->

            }.attach()

            rvProductSize.layoutManager = LinearLayoutManager(
                this@ProductDetailActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )
            val sizes = product.options["Size"] ?: emptyList()
            rvProductSize.adapter = ProductSizeAdapter({ selectedSize ->
                Toast.makeText(
                    this@ProductDetailActivity,
                    "Selected Size: $selectedSize",
                    Toast.LENGTH_SHORT
                ).show()
            }, sizes)

            rvProductColors.layoutManager = LinearLayoutManager(
                this@ProductDetailActivity,
                LinearLayoutManager.HORIZONTAL,
                false
            )

            val colors = colorsData.getColorData()
            rvProductColors.adapter = ProductColorAdapter({ selectedColor ->
                Toast.makeText(
                    this@ProductDetailActivity,
                    "Selected Color: $selectedColor",
                    Toast.LENGTH_SHORT
                ).show()
            }, colors)
        }
    }
}