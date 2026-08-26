package com.example.esewa_project.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.esewa_project.data.model.CartItem
import com.example.esewa_project.R
import com.example.esewa_project.databinding.ItemCartBinding

class CartAdapter(
    private val onIncrementClick: (Int) -> Unit,
    private val onDecrementClick: (Int) -> Unit,
    private val onProductClick: (Int) -> Unit
): ListAdapter<CartItem, CartAdapter.CartViewHolder>(DiffCallback) {

    class CartViewHolder(val binding: ItemCartBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(ItemCartBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            titleProduct.text =  item.title
            brandProduct.text = item.categoryName
            priceProduct.text = root.context.getString(
                R.string.product_price,
                item.price
            )
            productQuantity.text = String.format("%02d", item.quantity)

            Glide.with(imgProduct.context)
                .load(item.thumbnail)
                .into(imgProduct)

            addButton.setOnClickListener {
                onIncrementClick(item.productId)
            }

            minusButton.setOnClickListener {
                onDecrementClick(item.productId)
            }

            productImageContainer.setOnClickListener {
                onProductClick(item.productId)
            }

            detailsContainer.setOnClickListener {
                onProductClick(item.productId)
            }
        }
    }
    object DiffCallback : DiffUtil.ItemCallback<CartItem>(){
        override fun areItemsTheSame(
            oldItem: CartItem,
            newItem: CartItem
        ): Boolean {
            return oldItem.productId == newItem.productId
        }

        override fun areContentsTheSame(
            oldItem: CartItem,
            newItem: CartItem
        ): Boolean {
            return oldItem == newItem
        }
    }
}