package com.example.ttpay.products.network_catalogItemManagement

import com.example.ttpay.model.Article
import com.example.ttpay.model.Service
import retrofit2.Call
import retrofit2.http.GET

interface ServiceCatalogItemManagement {
    @GET("/api/v1/articles")
    fun getArticles(): Call<List<Article>>

    @GET("/api/v1/services")
    fun getServices(): Call<List<Service>>

    // Add other methods as needed for article management, e.g., createArticle, updateArticle, deleteArticle
}