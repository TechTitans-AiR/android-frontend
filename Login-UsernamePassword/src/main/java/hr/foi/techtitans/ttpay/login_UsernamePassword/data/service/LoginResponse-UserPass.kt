package hr.foi.techtitans.ttpay.login_UsernamePassword.data.service


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
