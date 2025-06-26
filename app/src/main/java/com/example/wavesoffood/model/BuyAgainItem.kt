package com.example.wavesoffood.model

data class BuyAgainItem(
    val cartItem: CartItems,
    val orderStatus: String? = null
)