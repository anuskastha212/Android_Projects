package com.example.esewa_project.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.esewa_project.R
import com.example.esewa_project.data.model.MostPopular

class MostPopularAdapter (
    private var mostPopular: List<MostPopular>,
    private var onItemClick: (String) -> Unit
) : RecyclerView.Adapter<MostPopularAdapter.MostPopularViewHolder>(){

    class MostPopularViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val tvMostPopular: TextView= view.findViewById(R.id.tvMostPopular)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MostPopularViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_catergory,parent,false)
            return MostPopularViewHolder(view)
    }

    override fun onBindViewHolder(holder: MostPopularViewHolder, position: Int) {
        val most = mostPopular[position]
        holder.tvMostPopular.text = mostPopular.tvMostPopular

        holder.itemView.setOnClickListener {
            onItemClick(mostPopular.tvMostPopular)
        }
    }

    override fun getItemCount(): Int = mostPopular.size

    fun updateData(new_mostPopular: List<MostPopular>){
        mostPopular =new_mostPopular
        notifyDataSetChanged()
    }
}