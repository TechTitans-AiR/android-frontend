package com.example.ttpay.accountManagement.network_accountManagement

import retrofit2.Call
import com.example.ttpay.model.User
import com.example.ttpay.model.newUser
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ServiceAccountManagement {

    @GET("/api/v1/users")
    fun getUsers(): Call<List<User>>

    @POST("/api/v1/users/create")  //endpoint for creating new user
    fun createNewUser(@Body user: newUser): Call<newUser> // Method for sending user data to backend

}