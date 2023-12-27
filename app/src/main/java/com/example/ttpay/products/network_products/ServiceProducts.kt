package com.example.ttpay.products.network_products

import com.example.ttpay.model.Article
import com.example.ttpay.model.Service
import retrofit2.Call
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path

interface ServiceProducts {
    @GET("/api/v1/articles")
    fun getArticles(): Call<List<Article>>

    @GET("/api/v1/services")
    fun getServices(): Call<List<Service>>

    @DELETE("/api/v1/articles/delete/{itemId}")//delete article
    fun deleteArticle(@Path("itemId") itemId: String?): Call<Void>

    @DELETE("/api/v1/articles/delete/{serviceId}")//delete service
    fun deleteService(@Path("serviceId") itemId: String?): Call<Void>
}