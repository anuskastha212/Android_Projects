package com.example.esewa_project

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.esewa_project.data.api.RetrofitInstance
import com.example.esewa_project.data.model.Product
import com.example.esewa_project.databinding.ActivityProductDetailBinding
import kotlinx.coroutines.launch

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()


        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

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

                val product = RetrofitInstance.productApi.getProductById(id)

                showProduct(product)

            } catch (e: Exception) {

                Log.e("ProductDetail", "Error", e)

                Toast.makeText(
                    this@ProductDetailActivity,
                    e.message ?: "Something went wrong",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun showProduct(product: Product) {

        binding.apply {

            tvProductName.text = product.title
            des.text = product.description
            tvProductPrice.text = getString(R.string.product_price, product.price)
            Glide.with(this@ProductDetailActivity)
                .load(product.thumbnail)
                .into(productImageDet)
        }
    }
}