package hr.foi.techtitans.ttpay.login_UsernamePassword.data.service



interface LoginCallback {
    fun onLoginSuccess(loggedUser: LoginResponseData?)
    fun onLoginFailure(errorMessage: String?)
}
