package hr.foi.air.login_pin.service


import hr.foi.techtitans.ttpay.core.network.data.LoginRequestData
import hr.foi.techtitans.ttpay.core.network.data.LoginRequestDataPIN
import hr.foi.techtitans.ttpay.core.network.data.LoginResponseData
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface LoginServicePIN {
    @POST("/api/v1/users/login/pin") // endpoint for login
    fun login(@Body userData: LoginRequestDataPIN): Call<LoginResponseData>
}