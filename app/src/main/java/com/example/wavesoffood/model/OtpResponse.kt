package com.example.wavesoffood.model


data class OtpResponse(
    val success: Boolean,
    val message: String,
    val otp: String? = null
)
