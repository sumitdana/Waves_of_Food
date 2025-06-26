package com.example.wavesoffood.model

data class CartItems(
     var foodName: String? = null,
     var foodPrice: String? = null,
     var foodImage: String? = null,
     var foodDescription: String? = null,
     var foodIngredient: String? = null,
     var foodQuantity: Int? = null,
     var uid: String? = null // ✅ Restaurant UID
)


