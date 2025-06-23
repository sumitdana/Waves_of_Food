package com.example.wavesoffood.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.adapter.BuyAgainAdapter
import com.example.wavesoffood.databinding.FragmentHistoryBinding
import com.example.wavesoffood.model.OrderModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryFragment : Fragment() {

    private lateinit var binding: FragmentHistoryBinding
    private lateinit var adapter: BuyAgainAdapter
    private val orderList = mutableListOf<OrderModel>()
    private lateinit var database: DatabaseReference
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().reference
        userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        setupRecyclerView()
        loadOrders()
        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = BuyAgainAdapter(requireContext(), orderList)
        binding.buyAgainRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.buyAgainRecyclerView.adapter = adapter
    }

    private fun loadOrders() {
        val orderRef = database.child("user").child(userId).child("orders")
        orderList.clear()

        orderRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (orderSnap in snapshot.children) {
                    val items = orderSnap.child("items")
                    for (item in items.children) {
                        val orderItem = item.getValue(OrderModel::class.java)
                        orderItem?.let { orderList.add(it) }
                    }
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load order history", Toast.LENGTH_SHORT).show()
            }
        })
    }
}