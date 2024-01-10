package hr.foi.techtitans.ttpay.core.network

interface RequestHandler<T> {
    fun sendRequest(responseListener: ResponseListener<T>)
}