package com.example.wavesoffood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.adapter.MenuAdapter
import com.example.wavesoffood.databinding.FragmentMenuBottomSheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MenuBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentMenuBottomSheetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMenuBottomSheetBinding.inflate(inflater, container, false)
        binding.buttonback.setOnClickListener {
            dismiss()
        }

        val menuFoodName = listOf("Herbal Pancake", "Sandwich", "Momo", "Item","Herbal Pancake", "Sandwich", "Momo", "Item")
        val menuItemPrice = listOf("$5", "$4", "$7", "$8","$5", "$4", "$7", "$8")
        val menuImage = listOf(
            R.drawable.menu1,
            R.drawable.menu5,
            R.drawable.menu6,
            R.drawable.menu7,
            R.drawable.menu1,
            R.drawable.menu5,
            R.drawable.menu6,
            R.drawable.menu7
        )

        val adapter = MenuAdapter(
            ArrayList(menuFoodName),
            ArrayList(menuItemPrice),
            ArrayList(menuImage)
        )

        binding.menurecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.menurecyclerview.adapter = adapter
        return binding.root
    }
}