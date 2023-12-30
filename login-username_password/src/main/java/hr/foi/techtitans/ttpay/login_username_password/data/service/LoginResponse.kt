package hr.foi.techtitans.ttpay.login_username_password.data.service

data class LoginResponseData(
    val headers: Map<String, String>,
    val body: Body,
    val statusCode: String,
    val statusCodeValue: Int
)

data class Body(
    val message: String,
    val token: String
)