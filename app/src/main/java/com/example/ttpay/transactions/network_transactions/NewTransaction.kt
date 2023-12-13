package com.example.ttpay.transactions.network_transactions

data class NewTransaction(
    val merchantId: String,
    val description: String,
    val amount: Double,
    val currency: String,
    val createdAt: String, //need to formated time before sending
    val updatedAt: String? = null
)