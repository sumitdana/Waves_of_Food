package com.example.wavesoffood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.databinding.BuyAgainItemBinding

class BuyAgainAdapter(private val buyAgainFoodName:ArrayList<String>, private val buyAgainFoodPrice:ArrayList<String>, private val buyAgainFoodImage:ArrayList<Int>):
    RecyclerView.Adapter<BuyAgainAdapter.BuyAgainFoodHolder>() {
    override fun onBindViewHolder(holder: BuyAgainFoodHolder, position: Int) {
        holder.bind(buyAgainFoodName[position],buyAgainFoodPrice[position], buyAgainFoodImage[position])
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BuyAgainFoodHolder {
        val binding=BuyAgainItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BuyAgainFoodHolder(binding)
    }

    override fun getItemCount(): Int = buyAgainFoodName.size

    class BuyAgainFoodHolder(private val binding: BuyAgainItemBinding):RecyclerView.ViewHolder(binding.root) {
        fun bind(foodName: String, foodPrice: String, foodImage: Int) {
            binding.buyAgainFoodName.text=foodName
            binding.buyAgainFoodPrice.text=foodPrice
            binding.buyAgainFoodImage.setImageResource(foodImage)

        }

    }


}