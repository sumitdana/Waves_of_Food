package com.example.wavesoffood.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.wavesoffood.FoodDetailsActivity
import com.example.wavesoffood.databinding.MenuItemBinding
import com.example.wavesoffood.model.CartItems
import com.example.wavesoffood.model.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RestaurantMenuAdapter(
    private val context: Context,
    private val menuList: List<MenuItem>,
    private val restaurantName: String
) : RecyclerView.Adapter<RestaurantMenuAdapter.MenuViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    inner class MenuViewHolder(val binding: MenuItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            // Redirect to FoodDetailsActivity when card clicked
            binding.root.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = menuList[position]
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

            // Add to cart button click
            binding.menuaddtocart.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = menuList[position]
                    addToCart(item)
                }
            }
        }

        private fun addToCart(item: MenuItem) {
            val userId = auth.currentUser?.uid ?: return
            val cartRef = database.child("user").child(userId).child("CartItems")

            cartRef.orderByChild("foodName").equalTo(item.foodName).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Already in cart -> update quantity
                        for (child in snapshot.children) {
                            val existing = child.getValue(CartItems::class.java)
                            val newQty = (existing?.foodQuantity ?: 1) + 1
                            child.ref.child("foodQuantity").setValue(newQty)
                            Toast.makeText(context, "Item quantity increased", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        // Not in cart -> add new item
                        val newItem = CartItems(
                            foodName = item.foodName,
                            foodPrice = item.foodPrice,
                            foodImage = item.foodImage,
                            foodDescription = item.foodDescription,
                            foodIngredient = item.foodIngredient,
                            foodQuantity = 1
                        )
                        cartRef.push().setValue(newItem).addOnSuccessListener {
                            Toast.makeText(context, "Item added to cart", Toast.LENGTH_SHORT).show()
                        }.addOnFailureListener {
                            Toast.makeText(context, "Failed to add item", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val binding = MenuItemBinding.inflate(LayoutInflater.from(context), parent, false)
        return MenuViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = menuList[position]
        holder.binding.menufoodname.text = item.foodName
        holder.binding.menuitemprice.text = "₹${item.foodPrice}"
        holder.binding.restaurantname.text = restaurantName
        Glide.with(context).load(Uri.parse(item.foodImage)).into(holder.binding.menuimage)
    }

    override fun getItemCount(): Int = menuList.size
}