package com.example.esewa_project.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.esewa_project.data.model.Product
import com.example.esewa_project.databinding.ItemProductBinding

class ProductAdapter :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(
        val binding: ItemProductBinding
    ) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback =
        object : DiffUtil.ItemCallback<Product>() {

            override fun areItemsTheSame(
                oldItem: Product,
                newItem: Product
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: Product,
                newItem: Product
            ): Boolean {
                return oldItem == newItem
            }
        }

    private val differ = AsyncListDiffer(this, diffCallback)

    var products: List<Product>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun getItemCount() = products.size

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {

        val binding = ItemProductBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {

        val product = products[position]

        holder.binding.apply {

            titleProduct.text = product.title

            desProduct.text = product.des

            Glide.with(imgProduct.context)
                .load(product.image)
                .into(imgProduct)
        }
    }

    fun submitList(list: List<Product>) {
        differ.submitList(list)
    }
}