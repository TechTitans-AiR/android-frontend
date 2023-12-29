package hr.foi.air.login_usernamepassword.data.model

import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.login.Body

data class LoginResponseUsernamePassword(
    val headers: Map<String, String>,
    val body: Body,
    val statusCode: String,
    val statusCodeValue: Int
)