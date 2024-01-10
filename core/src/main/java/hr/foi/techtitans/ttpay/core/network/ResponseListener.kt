package hr.foi.techtitans.ttpay.core.network

import hr.foi.techtitans.ttpay.core.network.models.ErrorResponseBody
import hr.foi.techtitans.ttpay.core.network.models.SuccessfulResponseBody

interface ResponseListener<T>  {
    fun onSuccessfulResponse(response: SuccessfulResponseBody<T>)
    fun onErrorResponse(response: ErrorResponseBody)
    fun onNetworkFailure(t: Throwable)
}