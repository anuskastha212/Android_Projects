package com.example.esewa_project.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.esewa_project.data.local.entity.CartEntity
import com.example.esewa_project.data.local.entity.ProductEntity
import com.example.esewa_project.data.model.Product
import com.example.esewa_project.databinding.ItemCartBinding

class CartAdapter(
    private val onIncrementClick: (Int) -> Unit,
    private val onDecrementClick: (Int) -> Unit,
    private val onProductClick: (Int) -> Unit
): ListAdapter<Pair<CartEntity, ProductEntity>, CartAdapter.CartViewHolder>(DiffCallback) {

    class CartViewHolder(val binding: ItemCartBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        return CartViewHolder(ItemCartBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        ))
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val (cart, product) = getItem(position)
        holder.binding.apply {
            titleProduct.text =  product.title
            brandProduct.text = product.categoryName
            priceProduct.text = "Rs. ${product.price}"
            productQuantity.text = String.format("%02d", cart.quantity)

            Glide.with(imgProduct.context).load(product.thumbnail).into(imgProduct)

            addButton.setOnClickListener {
                onIncrementClick(product.id)
            }

            minusButton.setOnClickListener {
                onDecrementClick(product.id)
            }

            productImageContainer.setOnClickListener {
                onProductClick(product.id)
            }

            detailsContainer.setOnClickListener {
                onProductClick(product.id)
            }
        }
    }
    object DiffCallback : DiffUtil.ItemCallback<Pair<CartEntity, ProductEntity>>(){
        override fun areItemsTheSame(
            oldItem: Pair<CartEntity, ProductEntity>,
            newItem: Pair<CartEntity, ProductEntity>
        ): Boolean {
            return oldItem.first.productId == newItem.first.productId
        }

        override fun areContentsTheSame(
            oldItem: Pair<CartEntity, ProductEntity>,
            newItem: Pair<CartEntity, ProductEntity>
        ): Boolean {
            return oldItem == newItem
        }
    }
}