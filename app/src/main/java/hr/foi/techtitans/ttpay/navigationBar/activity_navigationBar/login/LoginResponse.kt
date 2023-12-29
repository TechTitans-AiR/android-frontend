package hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.login

data class LoginResponse(
    val headers: Map<String, String>,
    val body: Body,
    val statusCode: String,
    val statusCodeValue: Int
)

data class Body(
    val message: String,
    val token: String
)

data class LoginRequest(
    val username: String,
    val password: String
    )
