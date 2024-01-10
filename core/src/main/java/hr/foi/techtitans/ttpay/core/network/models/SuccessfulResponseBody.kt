package hr.foi.techtitans.ttpay.core.network.models

class SuccessfulResponseBody<T>(
    val headers: Map<String, String>,
    val body: ResponseBody,
    val statusCode: String,
    val statusCodeValue: Int
    )