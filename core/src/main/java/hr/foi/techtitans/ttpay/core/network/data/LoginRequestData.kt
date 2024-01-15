package hr.foi.techtitans.ttpay.core.network.data

data class LoginRequestData(
    val username: String?,
    val password: String?
)

data class LoginRequestDataPIN(
    val pin: String
)