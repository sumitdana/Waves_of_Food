package com.example.wavesoffood

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.adapter.RestaurantMenuAdapter
import com.example.wavesoffood.databinding.ActivityRestaurantMenuBinding
import com.example.wavesoffood.model.MenuItem
import com.google.firebase.database.*

class RestaurantMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRestaurantMenuBinding
    private lateinit var adapter: RestaurantMenuAdapter
    private val menuList = mutableListOf<MenuItem>()
    private lateinit var database: DatabaseReference

    private lateinit var restaurantName: String
    private lateinit var ownerUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get UID and Restaurant Name from Intent
        ownerUid = intent.getStringExtra("uid") ?: ""
        restaurantName = intent.getStringExtra("restaurantName") ?: "Restaurant"

        Log.d("RestaurantMenuActivity", "Received UID: $ownerUid")
        Log.d("RestaurantMenuActivity", "Restaurant Name: $restaurantName")

        if (ownerUid.isEmpty()) {
            Toast.makeText(this, "No restaurant ID found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        database = FirebaseDatabase.getInstance().reference.child("menu")

        binding.restaurantmenurecyclerview.layoutManager = LinearLayoutManager(this)
        adapter = RestaurantMenuAdapter(this, menuList, restaurantName)
        binding.restaurantmenurecyclerview.adapter = adapter

        fetchMenuData()
    }

    private fun fetchMenuData() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menuList.clear()
                for (menuSnap in snapshot.children) {
                    val item = menuSnap.getValue(MenuItem::class.java)
                    if (item != null && item.uid == ownerUid) {
                        menuList.add(item)
                    }
                }
                Log.d("RestaurantMenuActivity", "Menu size: ${menuList.size}")
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@RestaurantMenuActivity, "Failed to load menu", Toast.LENGTH_SHORT).show()
            }
        })
    }
}