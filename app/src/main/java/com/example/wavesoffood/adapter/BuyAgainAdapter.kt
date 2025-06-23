package com.example.wavesoffood.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood.FoodDetailsActivity
import com.example.wavesoffood.databinding.BuyAgainItemBinding
import com.example.wavesoffood.model.OrderModel

class BuyAgainAdapter(
    private val context: Context,
    private val orderList: List<OrderModel>
) : RecyclerView.Adapter<BuyAgainAdapter.BuyAgainFoodHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainFoodHolder {
        val binding = BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BuyAgainFoodHolder(binding)
    }

    override fun onBindViewHolder(holder: BuyAgainFoodHolder, position: Int) {
        holder.bind(orderList[position])
    }

    override fun getItemCount(): Int = orderList.size

    inner class BuyAgainFoodHolder(private val binding: BuyAgainItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(order: OrderModel) {
            binding.buyAgainFoodName.text = order.foodName ?: ""
            binding.buyAgainFoodPrice.text = "₹${order.foodPrice ?: "0"}"

            Glide.with(binding.root.context)
                .load(order.foodImage)
                .into(binding.buyAgainFoodImage)

            binding.buyAgainFoodButton.setOnClickListener {
                val intent = Intent(context, FoodDetailsActivity::class.java)
                intent.putExtra("MenuItemName", order.foodName)
                intent.putExtra("MenuItemPrice", order.foodPrice)
                intent.putExtra("MenuItemImage", order.foodImage)
                intent.putExtra("MenuItemDescription", order.foodDescription ?: "No Description")
                intent.putExtra("MenuItemIngredients", order.foodIngredient ?: "No Ingredients")
                context.startActivity(intent)
            }
        }
    }
}