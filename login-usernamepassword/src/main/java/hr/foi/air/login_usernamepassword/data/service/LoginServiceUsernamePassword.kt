package hr.foi.air.login_usernamepassword.data.service

import hr.foi.air.login_usernamepassword.data.model.LoginRequestData
import hr.foi.air.login_usernamepassword.data.model.LoginResponseData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginServiceUsernamePassword {
    @POST("/api/v1/users/login") // endpoint for login
    fun login(@Body userData: LoginRequestData): Call<LoginResponseData>

}