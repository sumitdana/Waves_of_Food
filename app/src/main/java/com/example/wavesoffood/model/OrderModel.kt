package com.example.wavesoffood.model

data class OrderModel(
    val userId: String? = null,
    val restaurantId: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val address: String? = null,
    val paymentMethod: String? = null,
    val totalAmount: String? = null,
    val dateTime: String? = null,
    val status: String? = null,
    val items: List<CartItems>? = null // ✅ Includes list of ordered items
)