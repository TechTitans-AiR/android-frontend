package com.example.ttpay.navigationBar.activity_navigationBar.login

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginService {

    @POST("/api/v1/users/login") // endpoint for login
    fun login(@Body userData: LoginRequest): Call<LoginResponse>
}