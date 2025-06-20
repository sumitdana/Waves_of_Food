// ApiService.kt
package com.example.wavesoffood.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

data class EmailRequest(val email: String)

interface ApiService {
    @Headers("Content-Type: application/json")
    @POST("sendOtpEmail") // this is the Firebase function endpoint path
    fun sendOtp(@Body request: EmailRequest): Call<Void>
}