package com.example.wavesoffood.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood.PayOutActivity
import com.example.wavesoffood.databinding.CartItrmBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class CartAdapter(
    private val context: Context,
    private val foodNames: MutableList<String>,
    private val foodPrices: MutableList<String>,
    private val foodDescriptions: MutableList<String>,
    private val foodImagesUri: MutableList<String>,
    private val foodQuantity: MutableList<Int>,
    private val foodIngredients: MutableList<String>
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    private val userId = auth.currentUser?.uid ?: ""
    private val cartItemReference = FirebaseDatabase.getInstance().reference
        .child("user").child(userId).child("CartItems")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = CartItrmBinding.inflate(LayoutInflater.from(context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int = foodNames.size

    fun getUpdatedItemQuantities(): MutableList<Int> {
        return foodQuantity.toMutableList()
    }

    fun calculateTotalAmount(): Int {
        var total = 0
        for (i in foodPrices.indices) {
            val price = foodPrices[i].toIntOrNull() ?: 0
            val qty = foodQuantity.getOrNull(i) ?: 0
            total += price * qty
        }
        return total
    }

    inner class CartViewHolder(private val binding: CartItrmBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            binding.cartfoodname.text = foodNames[position]
            binding.cartitemprice.text = foodPrices[position]
            binding.cartItemQuantity.text = foodQuantity[position].toString()

            Glide.with(context).load(Uri.parse(foodImagesUri[position])).into(binding.cartimage)

            binding.plusbutton.setOnClickListener {
                updateQuantity(position, 1)
            }

            binding.minusbutton.setOnClickListener {
                if (foodQuantity[position] > 1) {
                    updateQuantity(position, -1)
                } else {
                    Toast.makeText(context, "Quantity cannot be less than 1", Toast.LENGTH_SHORT).show()
                }
            }

            binding.deletebutton.setOnClickListener {
                removeItem(position)
            }
        }

        private fun updateQuantity(position: Int, delta: Int) {
            getUniqueKeyAtPosition(position) { uniqueKey ->
                val newQuantity = foodQuantity[position] + delta
                cartItemReference.child(uniqueKey).child("foodQuantity").setValue(newQuantity)
                    .addOnSuccessListener {
                        foodQuantity[position] = newQuantity
                        notifyItemChanged(position)
                        Toast.makeText(context, "Quantity updated", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to update quantity", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        private fun removeItem(position: Int) {
            getUniqueKeyAtPosition(position) { uniqueKey ->
                cartItemReference.child(uniqueKey).removeValue().addOnSuccessListener {
                    foodNames.removeAt(position)
                    foodPrices.removeAt(position)
                    foodDescriptions.removeAt(position)
                    foodImagesUri.removeAt(position)
                    foodQuantity.removeAt(position)
                    foodIngredients.removeAt(position)
                    notifyItemRemoved(position)
                    Toast.makeText(context, "Item removed", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(context, "Failed to remove item", Toast.LENGTH_SHORT).show()
                }
            }
        }

        private fun getUniqueKeyAtPosition(positionRetrieve: Int, onComplete: (String) -> Unit) {
            cartItemReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var index = 0
                    for (dataSnapshot in snapshot.children) {
                        if (index == positionRetrieve) {
                            onComplete(dataSnapshot.key ?: return)
                            break
                        }
                        index++
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}