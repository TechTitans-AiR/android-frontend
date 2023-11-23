package com.example.ttpay.accountManagement.network_accountManagement

import retrofit2.Call
import com.example.ttpay.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ServiceAccountManagement {

    @GET("/api/v1/users")
    fun getUsers(): Call<List<User>>

    @POST("/api/app/v1/createUser")  //endpoint for creating new user
    fun createNewUser(@Body user: User): Call<User> // Method for sending user data to backend
}