package com.example.ttpay.transactions.network_transactions

import com.example.ttpay.model.Catalog
import com.example.ttpay.model.Transaction
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ServiceTransactionManagement {

    @GET("/api/v1/transactions/{merchantID}")
    fun getUserTransactions(@Path("merchantID") merchantID: String): Call<List<Transaction>>
}