package hr.foi.air.login_pin

import hr.foi.techtitans.ttpay.core.LoginToken

class PINLoginToken(pin: String) : LoginToken {

    //Data of the input fields
    private val authorizers = mapOf(
        "pin" to pin,
    )

    override fun getAuthorizers(): Map<String, String> {
        return authorizers
    }
}