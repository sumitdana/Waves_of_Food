package com.example.wavesoffood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.databinding.RestaurantListItemBinding
import com.example.wavesoffood.model.RestaurantModel

class RestaurantAdapter(
    private val restaurantList: List<RestaurantModel>,
    private val onItemClick: (RestaurantModel) -> Unit
) : RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder>() {

    inner class RestaurantViewHolder(val binding: RestaurantListItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = RestaurantListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val restaurant = restaurantList[position]
        holder.binding.restaurantname.text = restaurant.nameOfRestaurant
        holder.binding.ownername.text = restaurant.userName
        holder.binding.phonenumber.text = restaurant.contactNumber
        holder.binding.address.text = restaurant.address

        holder.itemView.setOnClickListener {
            onItemClick(restaurant) // ✅ trigger click listener
        }
    }

    override fun getItemCount() = restaurantList.size
}