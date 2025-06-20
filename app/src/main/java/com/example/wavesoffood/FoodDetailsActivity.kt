package com.example.wavesoffood

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.wavesoffood.databinding.ActivityFoodDetailsBinding
import com.example.wavesoffood.model.CartItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class FoodDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFoodDetailsBinding

    private var foodName: String? = null
    private var foodImage: String? = null
    private var foodDescription: String? = null
    private var foodPrice: String? = null
    private var foodIngredients: String? = null
    private var foodQuantity: Int = 0

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFoodDetailsBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        foodName = intent.getStringExtra("MenuItemName")
        foodPrice = intent.getStringExtra("MenuItemPrice")
        foodDescription = intent.getStringExtra("MenuItemDescription")
        foodIngredients = intent.getStringExtra("MenuItemIngredients")
        foodImage = intent.getStringExtra("MenuItemImage")

        Glide.with(this@FoodDetailsActivity)
            .load(Uri.parse(foodImage))
            .into(binding.detailfoodimage)

        binding.apply {
            detailFoodName.text = foodName
            detailfooddescription.text = foodDescription
            detailfoodingredients.text = foodIngredients
        }

        binding.imageButton.setOnClickListener {
            finish()
        }

        binding.addtocartbutton.setOnClickListener {
            addItemToCart()
        }

        binding.morebutton.setOnClickListener {
            foodQuantity++
            updateQtyDisplay()
            updateCartQty()
        }

        binding.lessbutton.setOnClickListener {
            if (foodQuantity > 0) {
                foodQuantity--
                updateQtyDisplay()
                updateCartQty()
            }
        }

        fetchInitialCartQty()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun updateQtyDisplay() {
        binding.itemqty.text = foodQuantity.toString()
    }

    private fun fetchInitialCartQty() {
        val userId = auth.currentUser?.uid ?: return
        val cartRef = database.child("user").child(userId).child("CartItems")

        cartRef.orderByChild("foodName").equalTo(foodName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val item = child.getValue(CartItems::class.java)
                    if (item != null) {
                        foodQuantity = item.foodQuantity ?: 0
                        updateQtyDisplay()
                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FoodDetailsActivity, "Error fetching quantity", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateCartQty() {
        val userId = auth.currentUser?.uid ?: return
        val cartRef = database.child("user").child(userId).child("CartItems")

        cartRef.orderByChild("foodName").equalTo(foodName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    if (foodQuantity == 0) {
                        child.ref.removeValue()
                    } else {
                        child.ref.child("foodQuantity").setValue(foodQuantity)
                    }
                    return
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FoodDetailsActivity, "Error updating cart", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addItemToCart() {
        val userId = auth.currentUser?.uid ?: return
        val cartRef = database.child("user").child(userId).child("CartItems")

        cartRef.orderByChild("foodName").equalTo(foodName).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        val existing = child.getValue(CartItems::class.java)
                        foodQuantity = (existing?.foodQuantity ?: 0) + 1
                        child.ref.child("foodQuantity").setValue(foodQuantity)
                        updateQtyDisplay()
                        Toast.makeText(this@FoodDetailsActivity, "Item quantity increased", Toast.LENGTH_SHORT).show()
                        return
                    }
                } else {
                    foodQuantity = 1
                    val cartItem = CartItems(
                        foodName = foodName,
                        foodPrice = foodPrice,
                        foodImage = foodImage,
                        foodDescription = foodDescription,
                        foodIngredient = foodIngredients,
                        foodQuantity = foodQuantity
                    )
                    cartRef.push().setValue(cartItem).addOnSuccessListener {
                        updateQtyDisplay()
                        Toast.makeText(this@FoodDetailsActivity, "Item added to cart", Toast.LENGTH_SHORT).show()
                    }.addOnFailureListener {
                        Toast.makeText(this@FoodDetailsActivity, "Item not added", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FoodDetailsActivity, "Error accessing cart", Toast.LENGTH_SHORT).show()
            }
        })
    }
}