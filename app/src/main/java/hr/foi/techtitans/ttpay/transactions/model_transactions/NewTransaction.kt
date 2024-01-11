package hr.foi.techtitans.ttpay.transactions.model_transactions

import java.io.Serializable

data class NewTransaction(
    val merchantId: String,
    val description: String,
    val amount: Double,
    val card: Card?,
    val currency: String
): Serializable