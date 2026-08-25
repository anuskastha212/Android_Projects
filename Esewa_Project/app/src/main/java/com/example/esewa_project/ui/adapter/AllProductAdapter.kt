package com.example.esewa_project.ui.adapter

import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.esewa_project.R
import com.example.esewa_project.data.model.Product
import com.example.esewa_project.databinding.ItemProductBinding
import kotlin.apply

class AllProductAdapter(
    private val onClick: (Product) -> Unit,
    private val onFavouriteClick: (Product) -> Unit,
    private val onIncrementClick: (Product) -> Unit,
    private val onDecrementClick: (Product) -> Unit
) : RecyclerView.Adapter<AllProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root)

    var currentQuantities: Map<Int, Int> = emptyMap()

    var favouriteIds: Set<Int> = emptySet()
        set(value) {
            field = value
            notifyDataSetChanged()
        }
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
        val product = products[position]
        val qty = currentQuantities[product.id]?: 0
        val isFav = favouriteIds.contains(product.id)

        holder.binding.apply {
            titleProduct.text = product.title
            brandProduct.text = product.category.name
            priceProduct.text = product.price.toString()

            favouriteButton.imageTintList = ColorStateList.valueOf(
                ContextCompat.getColor(root.context, if (isFav) R.color.green else R.color.light_gray)
            )
            if(qty > 0){
                layoutAdd.visibility = View.GONE
                addSub.visibility = View.VISIBLE
                productQuantity.text = String.format("%02d", qty)
            } else {
                layoutAdd.visibility = View.VISIBLE
                addSub.visibility = View.GONE
            }

            imgProduct.setOnClickListener {
                onClick(product)
            }

            layoutAdd.setOnClickListener {
                onIncrementClick(product)
            }

            addButton.setOnClickListener {
                onIncrementClick(product)
            }

            minusButton.setOnClickListener {
                onDecrementClick(product)
            }

            favouriteButton.setOnClickListener {
                onFavouriteClick(product)
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