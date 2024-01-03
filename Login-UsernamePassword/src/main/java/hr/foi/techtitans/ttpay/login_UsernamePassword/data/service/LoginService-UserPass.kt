package hr.foi.techtitans.ttpay.login_UsernamePassword.data.service

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface `LoginService-UserPass` {
    @POST("/api/v1/users/login") // endpoint for login
    fun login(@Body userData: LoginRequestData): Call<LoginResponseData>
}