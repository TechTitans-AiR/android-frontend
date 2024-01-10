package hr.foi.techtitans.ttpay.products.network_products

import hr.foi.techtitans.ttpay.products.model_products.Article
import hr.foi.techtitans.ttpay.products.model_products.Service
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import java.util.Objects

interface ServiceProducts {
    @GET("/api/v1/articles")
    fun getArticles(
        @Header("Authorization") token: String
    ): Call<List<Article>>

    @GET("/api/v1/services")
    fun getServices(
        @Header("Authorization") token: String
    ): Call<List<Service>>

    @DELETE("/api/v1/articles/delete/{itemId}")//delete article
    fun deleteArticle(
        @Header("Authorization") token: String,
        @Path("itemId") itemId: String?
    ): Call<Void>

    @DELETE("/api/v1/services/delete/{serviceId}")//delete service
    fun deleteService(
        @Header("Authorization") token: String,
        @Path("serviceId") itemId: String?
    ): Call<Void>

    @GET("/api/v1/services/{serviceId}")
    fun getServiceDetails(
        @Header("Authorization") token: String,
        @Path("serviceId") serviceId: String?
    ): Call<Service>

    @GET("/api/v1/articles/{articleId}")
    fun getArticleDetails(
        @Header("Authorization") token: String,
        @Path("articleId") articleId: String?
    ): Call<Article>

    @PUT("/api/v1/articles/update/{articleId}")
    fun updateArticle(
        @Path("articleId") articleId: String?,
        @Body updatedArticle: Map<String, @JvmSuppressWildcards Any>,
        @Header("Authorization") token: String
    ): Call<Void>

    @PUT("/api/v1/services/update/{serviceId}")
    fun updateService(
        @Path("serviceId") serviceId: String?,
        @Body updatedService: Map<String, @JvmSuppressWildcards Any>,
        @Header("Authorization") token: String
    ): Call<Void>

    @POST("/api/v1/services/create")
    fun createService(
        @Body newService: NewService,
        @Header("Authorization") token: String
    ): Call<Void>

}