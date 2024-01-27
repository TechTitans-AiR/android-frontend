package hr.foi.techtitans.ttpay.login_UsernamePassword

import hr.foi.techtitans.ttpay.core.LoginToken

class UsernamePasswordLoginToken(username: String, password: String) : LoginToken {

    //Data of the input fields
    private val authorizers = mapOf(
        "username" to username,
        "password" to password,
    )

    override fun getAuthorizers(): Map<String, String> {
        return authorizers
    }
}