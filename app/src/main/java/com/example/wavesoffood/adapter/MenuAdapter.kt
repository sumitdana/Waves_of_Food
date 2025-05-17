package com.example.wavesoffood.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.FoodDetailsActivity
import com.example.wavesoffood.databinding.MenuItemBinding

class MenuAdapter(
    private val menuItems: MutableList<String>,
    private val menuItemPrice: MutableList<String>,
    private val menuItemImage: MutableList<Int>,
    private val requireContext : Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {
private val itemClickListener: OnClickListener ?=null
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
                    val position=adapterPosition
                    if (position!=RecyclerView.NO_POSITION){
                        itemClickListener?.onItemClick(position)
                    }
                    val intent=Intent (requireContext, FoodDetailsActivity::class.java)
                    intent.putExtra("MenuItemName", menuItems.get(position))
                    intent.putExtra("MenuImage", menuItemImage.get(position))
                    requireContext.startActivity(intent)
                }
            }
        fun bind(position: Int) {
            binding.menufoodname.text = menuItems[position]
            binding.menuitemprice.text = menuItemPrice[position]
            binding.menuimage.setImageResource(menuItemImage[position])


        }
    }

    interface OnClickListener {
        fun onItemClick(position: Int)

    }
}


