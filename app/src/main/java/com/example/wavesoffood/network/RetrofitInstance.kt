package com.example.wavesoffood.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    val api: OtpApi by lazy {
        Retrofit.Builder()
            .baseUrl("https://us-central1-waves-of-food-6f609.cloudfunctions.net/") // ✅ Replace with your region if different
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(OtpApi::class.java)
    }
}