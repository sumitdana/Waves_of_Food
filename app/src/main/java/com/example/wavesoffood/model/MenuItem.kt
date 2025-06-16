package com.example.wavesoffood.model

data class MenuItem(
    val foodName: String = "",
    val foodPrice: String = "",
    val foodDescription: String = "",
    val foodIngredient: String = "",
    val foodImage: String = "",
    val uid: String = "" // ✅ Admin UID (owner)
)
