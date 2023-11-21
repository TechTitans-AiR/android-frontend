package com.example.ttpay.network__accountManagement

import retrofit2.Call
import com.example.ttpay.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ServiceAccountManagement {

    @GET("/api/app/v1/getUsers")
    fun getAllUsers(): Call<List<User>>

    @POST("/api/app/v1/createUser")  //endpoint for creating new user
    fun createUser(@Body user: User): Call<User> // Method for sending user data to backend
}