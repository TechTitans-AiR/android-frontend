package com.example.ttpay.catalogItemManagement.network_catalogItemManagement

import com.example.ttpay.model.Catalog
import com.example.ttpay.model.Service
import com.example.ttpay.model.User
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ServiceCatalogItemManagement {

    @GET("/api/v1/catalogs/user/{userId}")
    fun getUserCatalogs(@Path("userId") userId: String): Call<List<Catalog>>

    @POST("/api/v1/catalogs/create")//create new catalog for user
    fun createNewCatalogForUser(@Body new:NewCatalog):Call<NewCatalog>

    @GET("/api/v1/catalogs/{catalogId}")
    fun getCatalogDetails(@Path("catalogId") catalogId: String): Call<Catalog>

    @GET("/api/v1/catalogs")
    fun getCatalogs(): Call<List<Catalog>>

    @PATCH("/api/v1/catalogs/disable/{catalogId}")
    fun disableCatalog(@Path("catalogId") catalogId: String): Call<Catalog>

    @PATCH("/api/v1/catalogs/enable/{catalogId}")
    fun enableCatalog(@Path("catalogId") catalogId: String): Call<Catalog>


}