package hr.foi.techtitans.ttpay.catalogItemManagement.network_catalogItemManagement

import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog.NewCatalog
import hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement.Catalog
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path

interface ServiceCatalogItemManagement {

    @GET("/api/v1/catalogs/user/{userId}")
    fun getUserCatalogs(@Path("userId") userId: String): Call<List<Catalog>>

    @POST("/api/v1/catalogs/create")
    fun createNewCatalogForUser(
        @Header("Authorization") token: String,
        @Body new: NewCatalog):Call<NewCatalog>

    @GET("/api/v1/catalogs/{catalogId}")
    fun getCatalogDetails(
        @Header("Authorization") token: String,
        @Path("catalogId") catalogId: String
    ): Call<Catalog>

    @GET("/api/v1/catalogs")
    fun getCatalogs(
        @Header("Authorization") token: String
    ): Call<List<Catalog>>

    @PATCH("/api/v1/catalogs/disable/{catalogId}")
    fun disableCatalog(
        @Header("Authorization") token: String,
        @Path("catalogId") catalogId: String
    ): Call<ResponseBody>

    @PATCH("/api/v1/catalogs/enable/{catalogId}")
    fun enableCatalog(
        @Header("Authorization") token: String,
        @Path("catalogId") catalogId: String
    ): Call<ResponseBody>


}