package hr.foi.techtitans.ttpay.accountManagement.network_accountManagement

import retrofit2.Call
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.newUser
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.updateUser
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ServiceAccountManagement {

    @GET("/api/v1/users")
    fun getUsers(
        @Header("Authorization") token: String
    ): Call<List<User>>

    @GET("/api/v1/users/{id}")
    fun getUserDetails(
        @Header("Authorization") token: String,
        @Path("id") userId: String
    ): Call<User>

    @POST("/api/v1/users/create")
    fun createNewUser(
        @Header("Authorization") token: String,
        @Body user: newUser
    ): Call<ResponseBody> // Method for sending user data to backend

    @DELETE("/api/v1/users/delete/{userId}")
    fun deleteUser(
        @Header("Authorization") token: String,
        @Path("userId") userId: String?
    ): Call<Void>

    @PUT("/api/v1/users/update/{userID}")
    fun updateMerchantData(
        @Header("Authorization") token: String,
        @Path("userID") userID: String,
        @Body updatedData: updateUser
    ): Call<updateUser>

    @PUT("/api/v1/users/userUpdate/{userId}")
    fun updateUserProfile(
        @Path("userId") userId: String,
        @Header("Authorization") token: String,
        @Body updatedFields: Map<String, String>
    ): Call<Void>
}