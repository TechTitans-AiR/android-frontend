package hr.foi.air.login_usernamepassword.data.model

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */
data class LoggedInUser(
    val username: String,
    val token: String,
    val role:String
)