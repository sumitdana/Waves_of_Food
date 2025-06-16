package com.example.wavesoffood.model

data class RestaurantModel(
    var nameOfRestaurant: String? = null,
    var userName: String? = null,
    var email: String? = null,
    var contactNumber: String? = null,
    var password: String? = null,
    var uid: String? = null // ✅ Make it 'var' so we can assign Firebase key
)