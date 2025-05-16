package com.example.wavesoffood.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.R
import com.example.wavesoffood.adapter.BuyAgainAdapter
import com.example.wavesoffood.databinding.FragmentHistoryBinding


class HistoryFragment : Fragment() {
    private lateinit var binding: FragmentHistoryBinding
    private lateinit var buyAgainAdapter: BuyAgainAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentHistoryBinding.inflate(layoutInflater,container,false)
        setupRecyclerView()
        // Inflate the layout for this fragment
        return binding.root
    }
    private fun setupRecyclerView(){
        val buyAgainFoodName= arrayListOf("Food 1", "Food 2", "Food 3")
        val buyAgainFoodPrice= arrayListOf("$10", "$20", "$30")
        val buyAgainFoodImage= arrayListOf(R.drawable.menu1,R.drawable.menu2,R.drawable.menu3
        )

        buyAgainAdapter= BuyAgainAdapter(buyAgainFoodName,buyAgainFoodPrice,buyAgainFoodImage)
        binding.buyAgainRecyclerView.adapter=buyAgainAdapter
        binding.buyAgainRecyclerView.layoutManager=LinearLayoutManager(requireContext())
    }

    companion object {

    }
}