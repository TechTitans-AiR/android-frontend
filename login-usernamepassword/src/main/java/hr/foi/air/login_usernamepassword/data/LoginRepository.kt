package hr.foi.air.login_usernamepassword.data


import hr.foi.air.login_usernamepassword.data.model.LoggedInUser


// LoginRepository.kt

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
        dataSource.callServerLogin(enteredUsername, enteredPassword, callback)
    }
}


