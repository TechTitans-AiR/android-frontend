package hr.foi.techtitans.ttpay.login_username_password.network

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClientLogin {
    private const val URL_8080 = "http://10.0.2.2:8080/" //account_management

    val gson = GsonBuilder().setLenient().create()

    fun getInstance(port: Int): Retrofit {
        val url = when (port) {
            8080 -> URL_8080
            else -> throw IllegalArgumentException("Unsupported port: $port")
        }

        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}