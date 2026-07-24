package com.example.esewa_project.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.esewa_project.data.model.Product
import com.example.esewa_project.databinding.ItemProductBinding
import kotlin.apply

class FeaturedProductAdapter(
    private val onClick: (Product) -> Unit
) : RecyclerView.Adapter<FeaturedProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    //An inner class can access members of the outer FeaturedProductAdapter class if needed.
    private val diffCallback = object : DiffUtil.ItemCallback<Product>(){
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)
    var products: List<Product>
        get() = differ.currentList
        set(value) {differ.submitList(value)}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        return ProductViewHolder(ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        ))
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.binding.apply {
            val product = products[position]
            titleProduct.text = product.title
            brandProduct.text = product.category
            priceProduct.text = product.price.toString()

            root.setOnClickListener {
                onClick(product)
            }

            Glide.with(imgProduct.context)
                .load(product.thumbnail)
                .into(imgProduct)
        }
    }

    override fun getItemCount() = products.size

    fun submitList(list: List<Product>) {
        differ.submitList(list)
    }
}