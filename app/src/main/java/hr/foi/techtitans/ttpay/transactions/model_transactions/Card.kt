package hr.foi.techtitans.ttpay.transactions.model_transactions

import java.io.Serializable

data class Card(
    val cardNumber: String,
    val expirationDate: String,
    val balance: Double,
    val cvc: Int
): Serializable {
    constructor() : this("", "", 0.0, 0)
}