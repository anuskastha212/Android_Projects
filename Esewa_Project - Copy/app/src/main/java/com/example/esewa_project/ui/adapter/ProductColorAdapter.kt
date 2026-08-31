package com.example.esewa_project.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.esewa_project.databinding.ItemColorsBinding
import androidx.core.graphics.toColorInt

class ProductColorAdapter(
    private val onClick: (String) -> Unit,
    private val colors: List<String>
) : RecyclerView.Adapter<ProductColorAdapter.ColorViewHolder>() {

     class ColorViewHolder(val binding: ItemColorsBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding = ItemColorsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ColorViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val colorString = colors[position]
        try {
            holder.binding.productColor.setBackgroundColor(colorString.toColorInt())
        } catch (e: IllegalArgumentException) {
            holder.binding.productColor.setBackgroundColor(android.graphics.Color.LTGRAY)
        }

        holder.binding.root.setOnClickListener{
            onClick(colors[position])
        }
    }

    override fun getItemCount(): Int = colors.size

}
