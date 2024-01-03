package hr.foi.techtitans.ttpay.login_modular.model_login

data class LoginData(
    val username: String? = null,
    val password: String? = null,
    val pin: String? = null //for login with PIN
)
