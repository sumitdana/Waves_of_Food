package com.example.wavesoffood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood.databinding.MenuItemBinding
import com.example.wavesoffood.model.MenuItem

class RestaurantMenuAdapter(
    private val context: Context,
    private val menuList: List<MenuItem>,
    private val restaurantName: String
) : RecyclerView.Adapter<RestaurantMenuAdapter.MenuViewHolder>() {

    inner class MenuViewHolder(val binding: MenuItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = menuList[position]
        holder.binding.menufoodname.text = item.foodName
        holder.binding.menuitemprice.text = "₹${item.foodPrice}"
        holder.binding.restaurantname.text = restaurantName
        Glide.with(context).load(item.foodImage).into(holder.binding.menuimage)
    }

    override fun getItemCount(): Int = menuList.size
}