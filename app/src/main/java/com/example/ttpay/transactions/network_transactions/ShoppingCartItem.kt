package com.example.ttpay.transactions.network_transactions

data class ShoppingCartItem(
    val name: String,
    var quantity: Int,
    var unitPrice: Double,
    val isArticle: Boolean
)