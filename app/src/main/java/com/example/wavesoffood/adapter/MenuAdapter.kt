package com.example.wavesoffood.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood.FoodDetailsActivity
import com.example.wavesoffood.databinding.MenuItemBinding
import com.example.wavesoffood.model.MenuItem

class MenuAdapter(
private val menuItems:List<MenuItem>,
    private val requireContext : Context
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
                    val position=adapterPosition
                    if (position!=RecyclerView.NO_POSITION){
                        openDetailsActivity(position)
                    }
//                    val intent=Intent (requireContext, FoodDetailsActivity::class.java)
//                    intent.putExtra("MenuItemName", menuItems.get(position))
//                    intent.putExtra("MenuImage", menuItemImage.get(position))
//                    requireContext.startActivity(intent)
                }
            }


        private fun openDetailsActivity(position: Int) {
            val menuItem=menuItems[position]

            //A intent to open details activity and pass data
            val intent=Intent(requireContext,FoodDetailsActivity::class.java).apply {
                putExtra("MenuItemName",menuItem.foodName)
                putExtra("MenuItemImage",menuItem.foodImage)
                putExtra("MenuItemDescription",menuItem.foodDescription)
                putExtra("MenuItemIngredients",menuItem.foodIngredient)
                putExtra("MenuItemPrice",menuItem.foodPrice)
            }
            requireContext.startActivity(intent)
        }
        fun bind(position: Int) {

            val menuItem=menuItems[position]
            binding.apply {
                menufoodname.text=menuItem.foodName
                menuitemprice.text=menuItem.foodPrice
                val uri=Uri.parse(menuItem.foodImage)
                Glide.with(requireContext).load(uri).into(menuimage)

            }




        }
    }


//    interface OnClickListener {
//        fun onItemClick(position: Int)
//
//    }
}


