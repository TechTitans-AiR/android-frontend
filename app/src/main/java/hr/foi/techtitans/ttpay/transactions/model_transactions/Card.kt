package hr.foi.techtitans.ttpay.transactions.model_transactions

data class Card(
    val cardNumber: String,
    val expirationDate: String,
    val balance: Double,
    val cvc: Int
) {

    // Constructor without conditions
    constructor() : this("", "", 0.0, 0)

    init {
        require(cardNumber.matches(Regex("\\d{4} \\d{4} \\d{4} \\d{4}"))) {
            "Card number must be in the format 'zzzz zzzz zzzz zzzz', where z is a digit."
        }

        require(expirationDate.matches(Regex("\\d{4}-\\d{2}"))) {
            "Expiration date must be in the format 'yyyy-mm'."
        }

        require(cvc.toString().length == 3) {
            "CVC must have exactly three numbers."
        }
    }
}