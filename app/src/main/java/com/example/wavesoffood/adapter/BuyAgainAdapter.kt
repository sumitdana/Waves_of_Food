package com.example.wavesoffood.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood.FoodDetailsActivity
import com.example.wavesoffood.databinding.BuyAgainItemBinding
import com.example.wavesoffood.model.BuyAgainItem

class BuyAgainAdapter(
    private val context: Context,
    private val itemList: List<BuyAgainItem>
) : RecyclerView.Adapter<BuyAgainAdapter.BuyAgainFoodHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainFoodHolder {
        val binding = BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BuyAgainFoodHolder(binding)
    }

    override fun onBindViewHolder(holder: BuyAgainFoodHolder, position: Int) {
        holder.bind(itemList[position])
    }

    override fun getItemCount(): Int = itemList.size

    inner class BuyAgainFoodHolder(private val binding: BuyAgainItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(buyAgainItem: BuyAgainItem) {
            val item = buyAgainItem.cartItem

            binding.buyAgainFoodName.text = item.foodName ?: ""
            binding.buyAgainFoodPrice.text = "₹${item.foodPrice ?: "0"}"
            Glide.with(binding.root.context).load(item.foodImage).into(binding.buyAgainFoodImage)

            if (buyAgainItem.orderStatus == "Cancelled") {
                binding.orderCancelledText.visibility = View.VISIBLE
                binding.buyAgainFoodButton.visibility = View.GONE
            } else {
                binding.orderCancelledText.visibility = View.GONE
                binding.buyAgainFoodButton.visibility = View.VISIBLE
            }

            binding.buyAgainFoodButton.setOnClickListener {
                val intent = Intent(context, FoodDetailsActivity::class.java).apply {
                    putExtra("MenuItemName", item.foodName)
                    putExtra("MenuItemPrice", item.foodPrice)
                    putExtra("MenuItemImage", item.foodImage)
                    putExtra("MenuItemDescription", item.foodDescription)
                    putExtra("MenuItemIngredients", item.foodIngredient)
                }
                context.startActivity(intent)
            }
        }
    }
}