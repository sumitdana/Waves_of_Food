package com.example.wavesoffood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.adapter.MenuAdapter
import com.example.wavesoffood.databinding.FragmentMenuBottomSheetBinding
import com.example.wavesoffood.model.MenuItem
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.firebase.database.*

class MenuBottomSheetFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentMenuBottomSheetBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var menuItems: MutableList<MenuItem>
    private val restaurantMap = mutableMapOf<String, String>() // UID -> Restaurant Name

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBottomSheetBinding.inflate(inflater, container, false)

        binding.buttonback.setOnClickListener {
            dismiss()
        }

        database = FirebaseDatabase.getInstance()
        menuItems = mutableListOf()

        loadRestaurantNames()

        return binding.root
    }

    private fun loadRestaurantNames() {
        val adminRef = database.reference.child("admin")
        adminRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                restaurantMap.clear()
                for (adminSnap in snapshot.children) {
                    val uid = adminSnap.key ?: continue
                    val restaurantName = adminSnap.child("nameOfRestaurant").getValue(String::class.java)
                    if (restaurantName != null) {
                        restaurantMap[uid] = restaurantName
                    }
                }
                android.util.Log.d("RestaurantMapDebug", "Final restaurantMap: $restaurantMap")
                retrieveMenuItems()
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("RestaurantMapDebug", "Failed to load admin data: ${error.message}")
                // handle error
            }
        })
    }

    private fun retrieveMenuItems() {
        val foodRef = database.reference.child("menu")
        foodRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                menuItems.clear()
                for (foodSnapshot in snapshot.children) {
                    val menuItem = foodSnapshot.getValue(MenuItem::class.java)
                    menuItem?.let {
                        android.util.Log.d("MenuItemDebug", "MenuItem: ${it.foodName}, UID: ${it.uid}")
                        menuItems.add(it) }
                }
                android.util.Log.d("MenuItemDebug", "Total menu items fetched: ${menuItems.size}")
                setAdapter()
            }

            override fun onCancelled(error: DatabaseError) {
                android.util.Log.e("MenuItemDebug", "Failed to load menu items: ${error.message}")
                // handle error
            }
        })
    }

    private fun setAdapter() {
        val adapter = MenuAdapter(menuItems, restaurantMap, requireContext())
        binding.menurecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.menurecyclerview.adapter = adapter
    }
}