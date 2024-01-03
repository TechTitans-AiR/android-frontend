package hr.foi.techtitans.ttpay.navigationBar.model_navigationBar

import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User




interface LoginHandler {
    //handler which will be used for every login module

    fun performLogin(callback: LoginCallback?)

    //TODO: add storing user data method
}

interface LoginCallback {
    fun onLoginSuccess(user: User?)
    fun onLoginFailure(errorMessage: String?)
}
