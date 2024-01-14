package hr.foi.techtitans.ttpay.core.network.data

data class LoginResponseData(
    val headers: Map<String, String>,
    val body: ResponseBody,
    val statusCode: String,
    val statusCodeValue: Int
)

data class ResponseBody(
    val message: String,
    val token: String
)
