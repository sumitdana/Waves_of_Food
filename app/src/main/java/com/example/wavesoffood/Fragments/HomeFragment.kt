package com.example.wavesoffood.Fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.wavesoffood.MenuBottomSheetFragment
import com.example.wavesoffood.R
import com.example.wavesoffood.RestaurantMenuActivity
import com.example.wavesoffood.adapter.RestaurantAdapter
import com.example.wavesoffood.databinding.FragmentHomeBinding
import com.example.wavesoffood.model.RestaurantModel
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var database: DatabaseReference
    private lateinit var restaurantAdapter: RestaurantAdapter
    private val restaurantList = mutableListOf<RestaurantModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = FirebaseDatabase.getInstance().reference.child("admin")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        binding.viewallmenu.setOnClickListener {
            val bottomSheetDialog = MenuBottomSheetFragment()
            bottomSheetDialog.show(parentFragmentManager, "Test")
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Image slider setup
        val imageList = arrayListOf(
            SlideModel(R.drawable.banner1, ScaleTypes.FIT),
            SlideModel(R.drawable.banner2, ScaleTypes.FIT),
            SlideModel(R.drawable.banner3, ScaleTypes.FIT)
        )
        binding.imageSlider.setImageList(imageList, ScaleTypes.FIT)

        // RecyclerView setup
        binding.restaurantrecyclerview.layoutManager = LinearLayoutManager(requireContext())
        restaurantAdapter = RestaurantAdapter(restaurantList) { restaurant ->
            Log.d("HomeFragment", "Opening menu for UID: ${restaurant.uid}")
            val intent = Intent(requireContext(), RestaurantMenuActivity::class.java)
            intent.putExtra("uid", restaurant.uid)
            startActivity(intent)
        }
        binding.restaurantrecyclerview.adapter = restaurantAdapter

        loadRestaurantData()
    }

    private fun loadRestaurantData() {
        database.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                restaurantList.clear()
                for (dataSnap in snapshot.children) {
                    val uid = dataSnap.key ?: continue
                    val name = dataSnap.child("nameOfRestaurant").getValue(String::class.java) ?: ""
                    val owner = dataSnap.child("userName").getValue(String::class.java) ?: ""
                    val contact = dataSnap.child("contactNumber").getValue(String::class.java) ?: ""
                    val address = dataSnap.child("address").getValue(String::class.java) ?: ""

                    val model = RestaurantModel(
                        nameOfRestaurant = name,
                        userName = owner,
                        contactNumber = contact,
                        address = address,
                        uid = uid
                    )
                    restaurantList.add(model)
                }
                restaurantAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(requireContext(), "Failed to load restaurants", Toast.LENGTH_SHORT).show()
            }
        })
    }
}