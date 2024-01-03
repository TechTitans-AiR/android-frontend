package hr.foi.techtitans.ttpay.login_UsernamePassword

import com.auth0.jwt.JWT
import hr.foi.techtitans.ttpay.login_UsernamePassword.data.service.LoginRequestData
import hr.foi.techtitans.ttpay.login_UsernamePassword.data.service.LoginResponseData
import hr.foi.techtitans.ttpay.login_UsernamePassword.data.service.`LoginService-UserPass`
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import hr.foi.techtitans.ttpay.login_modular.model_login.LoginCallback
import hr.foi.techtitans.ttpay.login_modular.model_login.LoginData
import hr.foi.techtitans.ttpay.login_modular.model_login.LoginHandler
import hr.foi.techtitans.ttpay.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginHandlerUsernamePassword : LoginHandler {

    private val loginService: `LoginService-UserPass` = RetrofitClient.getInstance(8080)
        .create(`LoginService-UserPass`::class.java)

    override fun performLogin(loginDataOfUser: LoginData, callback: LoginCallback?) {
        val enteredUsername = loginDataOfUser.username
        val enteredPassword = loginDataOfUser.password

        val loginRequest = LoginRequestData(enteredUsername, enteredPassword)

        // login call
        loginService.login(loginRequest).enqueue(object : Callback<LoginResponseData> {
            override fun onResponse(call: Call<LoginResponseData>, response: Response<LoginResponseData>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let { loginResponse ->
                        val token = loginResponse.body.token

                        val decodedJWT = JWT.decode(token)

                        // getting role from JWT
                        val role = decodedJWT.getClaim("role").asString()
                        val userUsername = decodedJWT.getClaim("username").asString()

                        // storing data from API
                        val loggedInUser = LoggedInUser(
                            username = userUsername,
                            token = token,
                            role = role
                        )

                        //callback function onLoginSuccess from LoginHandler.kt
                        callback?.onLoginSuccess(loggedInUser)
                    }
                } else {
                    //callback function onLoginFailure
                    callback?.onLoginFailure("Invalid username or password")
                }
            }

            override fun onFailure(call: Call<LoginResponseData>, t: Throwable) {
                //callback function onLoginFailure from LoginHandler.kt
                callback?.onLoginFailure(t.message)
            }
        })
    }

}
