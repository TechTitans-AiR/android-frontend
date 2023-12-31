package hr.foi.techtitans.ttpay.login_username_password.data



class LoginRepository(val dataSource: LoginDataSource) {

    var user: LoggedInUser? = null

    val isLoggedIn: Boolean
        get() = user != null

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun callServerLogin(
        enteredUsername: String,
        enteredPassword: String,
        callback: (LoginResult<LoggedInUser>) -> Unit
    ) {
        dataSource.callServerLogin(enteredUsername, enteredPassword) { result ->
            callback(result)
        }
    }
}
