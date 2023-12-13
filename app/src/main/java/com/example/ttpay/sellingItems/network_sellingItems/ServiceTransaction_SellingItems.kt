package com.example.ttpay.sellingItems.network_sellingItems

import com.example.ttpay.transactions.network_transactions.NewTransaction
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ServiceTransaction_SellingItems {
    @POST("/api/v1/transactions/create")//create new transaction for selling items
    fun createTransaction(@Body newTransaction: NewTransaction): Call<NewTransaction>
}