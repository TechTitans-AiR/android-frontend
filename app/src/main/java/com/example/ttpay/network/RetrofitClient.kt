package com.example.ttpay.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val URL_8080 = "http://10.0.2.2:8080/" //account_management
    private const val URL_8081 = "http://10.0.2.2:8081/" //catalog_item_management
    private const val URL_8082 = "http://10.0.2.2:8082/" //transaction_management

    fun getInstance(port: Int): Retrofit {
        val url = when (port) {
            8080 -> URL_8080
            8081 -> URL_8081
            8082 -> URL_8082
            else -> throw IllegalArgumentException("Unsupported port: $port")
        }

        return Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
}