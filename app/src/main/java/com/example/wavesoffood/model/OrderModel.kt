package com.example.wavesoffood.model

data class OrderModel(
    val foodName: String? = null,
    val foodPrice: String? = null,
    val foodImage: String? = null,
    val foodDescription: String? = null,
    val foodIngredient: String? = null,
    var orderStatus: String? = null
)