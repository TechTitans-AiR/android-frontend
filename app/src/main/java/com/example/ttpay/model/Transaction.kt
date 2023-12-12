package com.example.ttpay.model

import java.util.Currency

data class Transaction (
    val id: String,
    val merchantId: String,
    val description: String,
    val amount: Double,
    val currency: String,
    val createdAt: String,
    val updatedAt: String
)