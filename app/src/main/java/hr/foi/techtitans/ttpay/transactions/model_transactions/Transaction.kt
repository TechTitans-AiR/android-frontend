package hr.foi.techtitans.ttpay.transactions.model_transactions

data class Transaction (
    val id: String,
    val merchantId: String,
    val description: String,
    val amount: Double,
    val currency: String,
    val createdAt: String,
    val updatedAt: String
)