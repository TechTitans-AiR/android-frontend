package hr.foi.techtitans.ttpay.login_username_password.data.service

import retrofit2.Call
import retrofit2.http.POST
import retrofit2.http.Body

interface LoginService {
    @POST("/api/v1/users/login") // endpoint for login
    fun login(@Body userData: LoginRequestData): Call<LoginResponseData>
}