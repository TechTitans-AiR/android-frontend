package hr.foi.techtitans.ttpay.login_username_password.data

data class LoggedInUser(
    val username :String,
    val token: String,
    val role:String
)
