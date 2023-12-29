package com.example.ttpay.transactions.network_transactions

import com.example.ttpay.transactions.model_transactions.NewTransaction
import com.example.ttpay.transactions.model_transactions.Transaction
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ServiceTransactionManagement {

    @GET("/api/v1/transactions/{merchantID}")
    fun getUserTransactions(@Path("merchantID") merchantID: String): Call<List<Transaction>>

    @POST("/api/v1/transactions/create")//create new transaction for selling items
    fun createTransaction(@Body newTransaction: NewTransaction): Call<NewTransaction>
}