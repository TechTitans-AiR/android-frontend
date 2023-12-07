package com.example.ttpay.navigationBar.activities.login

data class LoginResponse(
    val token: String,
    val role: String,
    val expire: Long,
    val username: String
)

data class LoginRequest(
    val username: String,
    val password: String
    )
