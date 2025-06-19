package com.example.wavesoffood.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.location.LocationRequestCompat.Quality
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.CongratsBottomSheetFragment
import com.example.wavesoffood.PayOutActivity
import com.example.wavesoffood.R
import com.example.wavesoffood.adapter.CartAdapter
import com.example.wavesoffood.databinding.CartItrmBinding
import com.example.wavesoffood.databinding.FragmentCartBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase


class CartFragment : Fragment() {
   private lateinit var binding: FragmentCartBinding
   private lateinit var auth: FirebaseAuth
   private lateinit var database: FirebaseDatabase
   private lateinit var foodNames:MutableList<String>
   private lateinit var foodPrices:MutableList<String>
   private lateinit var foodDescriptions:MutableList<String>
   private lateinit var foodImagesUri:MutableList<String>
   private lateinit var foodIngredients:MutableList<String>
   private lateinit var quantity:MutableList<Int>
   private lateinit var adapter: CartAdapter
   private lateinit var userId:String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCartBinding.inflate(inflater,container,false)






        binding.cartrecyclerview.layoutManager=LinearLayoutManager(requireContext())
        binding.cartrecyclerview.adapter=adapter
        binding.proceedButton.setOnClickListener {
            val intent=Intent(requireContext(), PayOutActivity::class.java)
            startActivity(intent)
        }




        return binding.root
    }

    companion object {


    }
}