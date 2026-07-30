package com.example.esewa_project.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.esewa_project.databinding.ItemSizesBinding

class ProductSizeAdapter(
    private val onClick: (String) -> Unit,
    private val sizes: List<String>
) : RecyclerView.Adapter<ProductSizeAdapter.SizeViewHolder>() {

     class SizeViewHolder(val binding: ItemSizesBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        val binding = ItemSizesBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SizeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        holder.binding.productSize.text = sizes[position]

        holder.binding.root.setOnClickListener{
            onClick(sizes[position])
        }

    }

    override fun getItemCount(): Int = sizes.size

}
