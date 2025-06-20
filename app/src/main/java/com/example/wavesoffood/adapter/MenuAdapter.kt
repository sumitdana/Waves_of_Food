package com.example.wavesoffood.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
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

class MenuAdapter(
    private val menuItems: List<MenuItem>,
    private val restaurantMap: Map<String, String>, // Map<UID, Restaurant Name>
    private val context: Context
) : RecyclerView.Adapter<MenuAdapter.MenuViewHolder>() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

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
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    openDetailsActivity(position)
                }
            }
        }

        private fun openDetailsActivity(position: Int) {
            val menuItem = menuItems[position]
            val intent = Intent(context, FoodDetailsActivity::class.java).apply {
                putExtra("MenuItemName", menuItem.foodName)
                putExtra("MenuItemImage", menuItem.foodImage)
                putExtra("MenuItemDescription", menuItem.foodDescription)
                putExtra("MenuItemIngredients", menuItem.foodIngredient)
                putExtra("MenuItemPrice", menuItem.foodPrice)
            }
            context.startActivity(intent)
        }

        fun bind(position: Int) {
            val menuItem = menuItems[position]
            val restaurantName = restaurantMap[menuItem.uid] ?: "Unknown Restaurant"

            binding.apply {
                menufoodname.text = menuItem.foodName
                menuitemprice.text = "₹${menuItem.foodPrice}"
                restaurantname.text = restaurantName

                Glide.with(context)
                    .load(Uri.parse(menuItem.foodImage))
                    .into(menuimage)

                menuaddtocart.setOnClickListener {
                    addToCart(menuItem)
                }
            }
        }

        private fun addToCart(item: MenuItem) {
            val userId = auth.currentUser?.uid ?: return
            val cartRef = database.child("user").child(userId).child("CartItems")

            cartRef.orderByChild("foodName").equalTo(item.foodName)
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(context, "Item already added", Toast.LENGTH_SHORT).show()
                        } else {
                            val cartItem = CartItems(
                                foodName = item.foodName,
                                foodPrice = item.foodPrice,
                                foodImage = item.foodImage,
                                foodDescription = item.foodDescription,
                                foodIngredient = item.foodIngredient,
                                foodQuantity = 1
                            )
                            cartRef.push().setValue(cartItem)
                                .addOnSuccessListener {
                                    Toast.makeText(context, "Item added to cart", Toast.LENGTH_SHORT).show()
                                }
                                .addOnFailureListener {
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
}