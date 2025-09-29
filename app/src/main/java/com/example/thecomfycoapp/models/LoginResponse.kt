package com.example.thecomfycoapp.models

// Response after login
data class LoginResponse(
    val token: String,
    val userDetails: UserDetails
)