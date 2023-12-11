package com.example.ttpay.catalogItemManagement.network_catalogItemManagement

import com.example.ttpay.model.Catalog
import com.example.ttpay.model.Service
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ServiceCatalogItemManagement {

    @GET("/api/v1/catalogs/user/{userId}")
    fun getUserCatalogs(@Path("userId") userId: String): Call<List<Catalog>>

    @GET("/api/v1/catalogs/{catalogId}")
    fun getCatalogDetails(@Path("catalogId") catalogId: String): Call<Catalog>


}