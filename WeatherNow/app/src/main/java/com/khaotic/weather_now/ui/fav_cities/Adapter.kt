package com.khaotic.weather_now.ui.fav_cities

import android.annotation.SuppressLint

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.khaotic.weather_now.R

class LanguageAdapter(var mList: List<City>) :
    RecyclerView.Adapter<LanguageAdapter.LanguageViewHolder>() {

    var onItemClick : ((City)-> Unit)? = null

    inner class LanguageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val logo : ImageView = itemView.findViewById(R.id.logoIv)
        val titleTv : TextView = itemView.findViewById(R.id.titleTv)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(mList: List<City>){
        this.mList = mList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LanguageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item , parent , false)
        return LanguageViewHolder(view)
    }

    override fun onBindViewHolder(holder: LanguageViewHolder, position: Int) {
        val city = mList[position]
        holder.logo.setImageResource(mList[position].logo)
        holder.titleTv.text = mList[position].title

        holder.itemView.setOnClickListener{
            onItemClick?.invoke(city)
        }
    }

    override fun getItemCount(): Int {
        return mList.size
    }
}