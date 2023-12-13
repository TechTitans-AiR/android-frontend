package com.example.ttpay.transactions.network_transactions

import java.io.Serializable

data class NewTransaction(
    val merchantId: String,
    val description: String,
    val amount: Double,
    val currency: String,
): Serializable