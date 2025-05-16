package com.example.wavesoffood.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.CongratsBottomSheetFragment
import com.example.wavesoffood.PayOutActivity
import com.example.wavesoffood.R
import com.example.wavesoffood.adapter.CartAdapter
import com.example.wavesoffood.databinding.CartItrmBinding
import com.example.wavesoffood.databinding.FragmentCartBinding


class CartFragment : Fragment() {
   private lateinit var binding: FragmentCartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentCartBinding.inflate(inflater,container,false)
        val cartFoodName= listOf("Burger","Sandwich","Momo", "Item")
        val CartItemPrice= listOf("$5","$4","$7","$8")
        val cartImage= listOf(
            R.drawable.menu4,
            R.drawable.menu5,
            R.drawable.menu6,
            R.drawable.menu7
        )
        val adapter=CartAdapter(ArrayList(cartFoodName),ArrayList(CartItemPrice),ArrayList(cartImage))
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