package com.example.ttpay.network__accountManagement

import retrofit2.Call
import com.example.ttpay.model.User
import retrofit2.http.GET

interface ServiceAccountManagement {
    @GET("/api/v1/users")
    fun getUsers(): Call<List<User>>
}