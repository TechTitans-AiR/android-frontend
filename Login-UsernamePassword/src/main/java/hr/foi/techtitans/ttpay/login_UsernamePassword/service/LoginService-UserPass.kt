package hr.foi.techtitans.ttpay.login_UsernamePassword.service


import hr.foi.techtitans.ttpay.core.network.data.LoginRequestData
import hr.foi.techtitans.ttpay.core.network.data.LoginResponseData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface `LoginService-UserPass` {
    @POST("/api/v1/users/login") // endpoint for login
    fun login(@Body userData: LoginRequestData): Call<LoginResponseData>
}