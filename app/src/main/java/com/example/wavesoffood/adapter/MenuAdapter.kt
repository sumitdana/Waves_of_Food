package com.example.wavesoffood.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood.FoodDetailsActivity
import com.example.wavesoffood.databinding.MenuItemBinding
import com.example.wavesoffood.model.MenuItem

class MenuAdapter(
    private val menuItems: List<MenuItem>,
    private val restaurantMap: Map<String, String>, // Map<UID, Restaurant Name>
    private val context: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = menuItems.size

    inner class MenuViewHolder(private val binding: MenuItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    openDetailsActivity(position)
                }
            }
        }

        private fun openDetailsActivity(position: Int) {
            val menuItem = menuItems[position]
            val intent = Intent(context, FoodDetailsActivity::class.java).apply {
                putExtra("MenuItemName", menuItem.foodName)
                putExtra("MenuItemImage", menuItem.foodImage)
                putExtra("MenuItemDescription", menuItem.foodDescription)
                putExtra("MenuItemIngredients", menuItem.foodIngredient)
                putExtra("MenuItemPrice", menuItem.foodPrice)
            }
            context.startActivity(intent)
        }

        fun bind(position: Int) {
            val menuItem = menuItems[position]
            val restaurantName = restaurantMap[menuItem.uid] ?: "Unknown Restaurant"

            android.util.Log.d("MenuAdapterDebug", "Binding item: ${menuItem.foodName}, UID: ${menuItem.uid}, Restaurant: $restaurantName")
            binding.apply {
                menufoodname.text = menuItem.foodName
                menuitemprice.text = menuItem.foodPrice
                restaurantname.text = restaurantMap[menuItem.uid] ?: "Unknown Restaurant"

                Glide.with(context)
                    .load(Uri.parse(menuItem.foodImage))
                    .into(menuimage)
            }
        }
    }
}