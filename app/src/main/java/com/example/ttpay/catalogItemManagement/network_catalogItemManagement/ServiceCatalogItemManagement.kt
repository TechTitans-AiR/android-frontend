package com.example.ttpay.catalogItemManagement.network_catalogItemManagement

import com.example.ttpay.model.Catalog
import com.example.ttpay.model.Service
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ServiceCatalogItemManagement {

    @GET("/api/v1/catalogs/{userId}")
    fun getUserCatalogs(@Path("userId") userId: String): Call<List<Catalog>>

    @POST("/api/v1/catalogs/create")//create new catalog for user
    fun createNewCatalogForUser(@Body new:NewCatalog):Call<NewCatalog>
}