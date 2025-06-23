package com.example.wavesoffood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.wavesoffood.adapter.NotificationAdapter
import com.example.wavesoffood.databinding.FragmentNotificationBottomBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class NotificationBottomFragment : BottomSheetDialogFragment() {

    private lateinit var binding: FragmentNotificationBottomBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNotificationBottomBinding.inflate(inflater, container, false)

        // 🔔 Only 2 notifications: Confirmed & Cancelled
        val notifications = listOf(
            "Your order has been confirmed ",
            "Your order has been cancelled "
        )

        val notificationImages = listOf(
            R.drawable.confirm,     // drawable for confirmation
            R.drawable.cancelled    // drawable for cancellation
        )

        val adapter = NotificationAdapter(
            ArrayList(notifications),
            ArrayList(notificationImages)
        )

        binding.notificationRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationRecyclerView.adapter = adapter

        return binding.root
    }
}