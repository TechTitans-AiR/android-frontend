package hr.foi.techtitans.ttpay.products.network_products

import hr.foi.techtitans.ttpay.products.model_products.Article
import hr.foi.techtitans.ttpay.products.model_products.Service
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

    @DELETE("/api/v1/services/delete/{serviceId}")//delete service
    fun deleteService(@Path("serviceId") itemId: String?): Call<Void>

    @GET("/api/v1/services/{serviceId}")
    fun getServiceDetails(@Path("serviceId") serviceId: String?): Call<Service>
}