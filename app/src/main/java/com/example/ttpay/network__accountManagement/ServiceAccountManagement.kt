package com.example.ttpay.network__accountManagement

import retrofit2.Call
import com.example.ttpay.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ServiceAccountManagement {

    @GET("/api/app/v1/users")
    fun getUsers(): Call<List<User>>
}