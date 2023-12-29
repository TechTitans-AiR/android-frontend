package hr.foi.air.login_usernamepassword.data.service

import hr.foi.air.login_usernamepassword.data.model.LoginResponseData
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.login.LoginRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginServiceUsernamePassword {
    @POST("/api/v1/users/login") // endpoint for login
    fun login(@Body userData: LoginRequest): Call<LoginResponseData>

}