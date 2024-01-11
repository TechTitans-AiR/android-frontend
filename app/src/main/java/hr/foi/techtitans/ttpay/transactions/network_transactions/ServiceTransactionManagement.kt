package hr.foi.techtitans.ttpay.transactions.network_transactions

import hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement.Catalog
import hr.foi.techtitans.ttpay.transactions.model_transactions.NewTransaction
import hr.foi.techtitans.ttpay.transactions.model_transactions.Transaction
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ServiceTransactionManagement {

    @GET("/api/v1/transactions/merchant/{merchantID}")
    fun getUserTransactions(
        @Header("Authorization") token: String,
        @Path("merchantID") merchantID: String
    ): Call<List<Transaction>>

    @GET("/api/v1/transactions")
    fun getTransactions(
        @Header("Authorization") token: String
    ): Call<List<Transaction>>

    @POST("/api/v1/transactions/create")//create new transaction for selling items
    fun createTransactionCash(@Body newTransaction: NewTransaction): Call<NewTransaction>

    @POST("/api/v1/transactions/create/card")
    fun createTransactionCard(@Body newTransaction: NewTransaction): Call<NewTransaction>

    @GET("/api/v1/transactions/{id}")
    fun getTransactionDetails(
        @Header("Authorization") token: String,
        @Path("id") id: String
    ): Call<Transaction>

    @POST("api/v1/transactions/search")
    fun searchTransactions(@Body searchParams: Map<String, String>): Call<List<Transaction>>

}