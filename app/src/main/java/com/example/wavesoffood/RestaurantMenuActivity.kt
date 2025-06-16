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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRestaurantMenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val uid = intent.getStringExtra("uid")
        Log.d("RestaurantMenuActivity", "Received UID: $uid")

        if (uid == null) {
            Toast.makeText(this, "No restaurant ID found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        database = FirebaseDatabase.getInstance().reference.child("menu")

        binding.restaurantmenurecyclerview.layoutManager = LinearLayoutManager(this)
        adapter = RestaurantMenuAdapter(this, menuList)
        binding.restaurantmenurecyclerview.adapter = adapter

        fetchMenuData(uid)
    }

    private fun fetchMenuData(ownerUid: String) {
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