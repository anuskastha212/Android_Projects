package com.example.esewa_project

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class CategoryAdapter(private val categoryList: List<Category>)
    : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>(){
    class CategoryViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView){
            val icon: ImageView = itemView.findViewById(R.id.img_category_icon)
            val name: TextView = itemView.findViewById(R.id.txt_category_name)

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_catergory,parent,false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        holder.icon.setImageResource(category.iconResId)
        holder.name.text = category.name
    }

    override fun getItemCount(): Int = categoryList.size
}