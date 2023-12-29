package com.example.ttpay.accountManagement.network_accountManagement

import retrofit2.Call
import com.example.ttpay.accountManagement.model_accountManagement.User
import com.example.ttpay.accountManagement.model_accountManagement.newUser
import com.example.ttpay.accountManagement.model_accountManagement.updateUser
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ServiceAccountManagement {

    @GET("/api/v1/users")
    fun getUsers(): Call<List<User>>

    @GET("/api/v1/users/{id}")
    fun getUserDetails(@Path("id") userId: String): Call<User>

    @POST("/api/v1/users/create")  //endpoint for creating new user
    fun createNewUser(@Body user: newUser): Call<newUser> // Method for sending user data to backend

    @DELETE("/api/v1/users/delete/{userId}")
    fun deleteUser(@Path("userId") userId: String?): Call<Void>

    @PUT("/api/v1/users/update/{userID}")
    fun updateMerchantData(@Path("userID") userID: String, @Body updatedData: updateUser): Call<updateUser>
}