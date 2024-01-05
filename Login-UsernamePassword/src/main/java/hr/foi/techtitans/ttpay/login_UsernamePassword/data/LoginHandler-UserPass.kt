package hr.foi.techtitans.ttpay.login_UsernamePassword.data



interface LoginCallback {
    fun onLoginSuccess(loggedUser: LoginResponseData?)
    fun onLoginFailure(errorMessage: String?)
}
