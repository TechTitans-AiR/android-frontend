package hr.foi.techtitans.ttpay.login_modular.model_login

interface LoginHandler {
    //handler which will be used for every login module

    fun performLogin(loginData: LoginData, callback: LoginCallback?)

    //TODO: add storing user data method
}

interface LoginCallback {
    fun onLoginSuccess(loggedUser: LoggedInUser?)
    fun onLoginFailure(errorMessage: String?)
}
