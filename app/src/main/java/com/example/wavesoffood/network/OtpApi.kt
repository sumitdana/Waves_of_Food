package com.example.wavesoffood.network

import com.example.wavesoffood.model.OtpRequest
import com.example.wavesoffood.model.OtpResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface OtpApi {
    @Headers("Content-Type: application/json")
    @POST("sendOtpEmail")
    fun sendOtp(@Body request: OtpRequest): Call<OtpResponse>
}