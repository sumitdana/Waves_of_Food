package com.example.wavesoffood.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.wavesoffood.databinding.ActivityLoginBinding
import com.example.wavesoffood.databinding.CartItrmBinding

class CartAdapter(private val cartItems:MutableList<String>,
                  private val cartItemPrice:MutableList<String>,
                  private val cartImage:MutableList<Int>):
    RecyclerView.Adapter<CartAdapter.CartViewHolder>() {
    private val ItemQuantities=IntArray(cartItems.size){1}


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItrmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = cartItems.size

    inner class CartViewHolder(private val binding: CartItrmBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.apply {

                val quantity=ItemQuantities[position]
                cartfoodname.text=cartItems[position]
                cartitemprice.text=cartItemPrice[position]
                cartimage.setImageResource(cartImage[position])
                cartItemQuantity.text=quantity.toString()

                minusbutton.setOnClickListener {

                    decreasequantity(position)

                }

                plusbutton.setOnClickListener {
                    increasequantity(position)

                }
                deletebutton.setOnClickListener {
                    val itemposition=adapterPosition
                    if (itemposition!=RecyclerView.NO_POSITION){
                        deleteitems(itemposition)
                    }

                }

            }

        }

        private fun increasequantity(position: Int){
            if (ItemQuantities[position]<10){
                ItemQuantities[position]++
                binding.cartItemQuantity.text=ItemQuantities[position].toString()
            }
        }

        private fun decreasequantity(position: Int){
            if (ItemQuantities[position]>1){
                ItemQuantities[position]--
                binding.cartItemQuantity.text=ItemQuantities[position].toString()
            }
        }

        private fun deleteitems(position: Int){
            cartItems.removeAt(position)
            cartImage.removeAt(position)
            cartItemPrice.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position,cartItems.size)
        }


    }

}