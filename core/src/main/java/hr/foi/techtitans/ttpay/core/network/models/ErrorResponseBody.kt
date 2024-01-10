package hr.foi.techtitans.ttpay.core.network.models

class ErrorResponseBody (
    token: String,
    message: String,
    val error_code: Int,
    val error_message: String
) : ResponseBody(token, message)