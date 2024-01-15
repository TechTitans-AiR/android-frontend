package hr.foi.techtitans.ttpay.core.network.data

data class LoginResponseData(
    val headers: Map<String, String>,
    val body: ResponseBodyData,
    val statusCode: String,
    val statusCodeValue: Int
)

data class ResponseBodyData(
    val message: String,
    val token: String
)
