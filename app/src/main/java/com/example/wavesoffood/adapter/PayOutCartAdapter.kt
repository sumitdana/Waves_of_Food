package com.example.wavesoffood.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood.databinding.CartItrmBinding
import com.example.wavesoffood.model.CartItems

class PayOutCartAdapter(
    private val context: Context,
    private val cartList: List<CartItems>
) : RecyclerView.Adapter<PayOutCartAdapter.CartViewHolder>() {

    inner class CartViewHolder(val binding: CartItrmBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItrmBinding.inflate(LayoutInflater.from(context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val item = cartList[position]

        holder.binding.apply {
            cartfoodname.text = item.foodName
            cartitemprice.text = "₹${item.foodPrice}"
            cartItemQuantity.text = item.foodQuantity.toString()

            Glide.with(context)
                .load(item.foodImage)
                .into(cartimage)

            // Disable interaction for PayOut view
            plusbutton.isEnabled = false
            minusbutton.isEnabled = false
            deletebutton.isEnabled = false
        }
    }

    override fun getItemCount(): Int = cartList.size
}