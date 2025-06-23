package com.example.wavesoffood.adapter

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood.databinding.CartItrmBinding
import com.example.wavesoffood.model.CartItems

class PayOutCartAdapter(
    private val context: Context,
    private val cartList: MutableList<CartItems>,
    private val onCartUpdated: () -> Unit // lambda to notify changes
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

            val unitPrice = item.foodPrice?.toIntOrNull() ?: 0
            val quantity = item.foodQuantity ?: 1
            val totalPrice = unitPrice * quantity

            cartitemprice.text = "₹$unitPrice x $quantity = ₹$totalPrice"
            cartItemQuantity.text = quantity.toString()

            Glide.with(context)
                .load(Uri.parse(item.foodImage))
                .into(cartimage)

            plusbutton.setOnClickListener {
                item.foodQuantity = quantity + 1
                notifyItemChanged(position)
                onCartUpdated()
            }

            minusbutton.setOnClickListener {
                if (quantity > 1) {
                    item.foodQuantity = quantity - 1
                    notifyItemChanged(position)
                    onCartUpdated()
                } else {
                    Toast.makeText(context, "Minimum quantity is 1", Toast.LENGTH_SHORT).show()
                }
            }

            deletebutton.setOnClickListener {
                cartList.removeAt(position)
                notifyItemRemoved(position)
                onCartUpdated()
            }
        }
    }

    override fun getItemCount(): Int = cartList.size

    fun getTotalAmount(): Int {
        var total = 0
        for (item in cartList) {
            val price = item.foodPrice?.toIntOrNull() ?: 0
            val quantity = item.foodQuantity ?: 0
            total += price * quantity
        }
        return total
    }

    fun getCartList(): List<CartItems> = cartList
}