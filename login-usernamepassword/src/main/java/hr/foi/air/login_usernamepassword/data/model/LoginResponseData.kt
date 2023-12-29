package hr.foi.air.login_usernamepassword.data.model

import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.login.Body

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
