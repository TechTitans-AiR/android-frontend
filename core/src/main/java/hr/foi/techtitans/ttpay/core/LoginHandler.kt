package hr.foi.techtitans.ttpay.core

interface LoginHandler {
    fun handleLogin(loginToken: LoginToken, loginListener: LoginOutcomeListener)
}