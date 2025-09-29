package com.example.thecomfycoapp.network

import com.example.thecomfycoapp.models.LoginRequest
import com.example.thecomfycoapp.models.LoginResponse
import com.example.thecomfycoapp.models.RegisterRequest
import com.example.thecomfycoapp.models.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): LoginResponse

    @POST("api/auth/logout")
    suspend fun logout(): Map<String, String> // { "message": "Logged out" }

    @POST("api/auth/login/google")
    suspend fun loginWithGoogle(@Body request: Map<String, String>): LoginResponse

}
