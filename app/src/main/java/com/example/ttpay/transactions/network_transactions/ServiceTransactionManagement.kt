package com.example.ttpay.transactions.network_transactions

import com.example.ttpay.catalogItemManagement.network_catalogItemManagement.NewCatalog
import com.example.ttpay.model.Catalog
import com.example.ttpay.model.Transaction
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ServiceTransactionManagement {

    @POST("/api/v1/transactions/create")//create new transaction for selling items
    fun createTransaction(@Body new: NewTransaction):Call<NewTransaction>
}