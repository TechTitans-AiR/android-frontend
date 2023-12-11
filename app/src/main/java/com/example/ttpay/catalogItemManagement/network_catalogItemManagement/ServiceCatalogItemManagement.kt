package com.example.ttpay.catalogItemManagement.network_catalogItemManagement

import com.example.ttpay.model.Catalog
import com.example.ttpay.model.Service
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ServiceCatalogItemManagement {

    @GET("/api/v1/catalogs/userID")
    fun getUserCatalogs(@Path("userID") userId: String): Call<List<Catalog>>
}