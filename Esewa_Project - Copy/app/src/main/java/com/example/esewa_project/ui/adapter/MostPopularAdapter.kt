package com.example.esewa_project.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.esewa_project.data.model.ProductCategory
import com.example.esewa_project.databinding.ItemMostpopularBinding

class MostPopularAdapter(
    private val onItemClick: (String) -> Unit
) : RecyclerView.Adapter<MostPopularAdapter.MostPopularViewHolder>() {

    inner class MostPopularViewHolder(
        val binding: ItemMostpopularBinding
    ) : RecyclerView.ViewHolder(binding.root)

    private val diffCallback = object : DiffUtil.ItemCallback<ProductCategory>() {

        override fun areItemsTheSame(
            oldItem: ProductCategory,
            newItem: ProductCategory): Boolean {
            return oldItem.name == newItem.name
        }

        override fun areContentsTheSame(
            oldItem: ProductCategory,
            newItem: ProductCategory): Boolean {
            return oldItem == newItem
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var mostPopular: List<ProductCategory>
        get() = differ.currentList
        set(value) {
            differ.submitList(value)
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MostPopularViewHolder {

        val binding = ItemMostpopularBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return MostPopularViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MostPopularViewHolder, position: Int) {

        val most = mostPopular[position]

        holder.binding.apply {

            tvMostPopular.text = most.name

            root.setOnClickListener {
                onItemClick(most.name)
            }
        }
    }

    override fun getItemCount(): Int = mostPopular.size

    fun submitList(list: List<ProductCategory>) {
        differ.submitList(list)
    }
}