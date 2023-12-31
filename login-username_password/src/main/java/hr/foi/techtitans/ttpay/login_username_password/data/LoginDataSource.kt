package hr.foi.techtitans.ttpay.login_username_password.data

import com.auth0.jwt.JWT
import hr.foi.techtitans.ttpay.login_username_password.data.service.LoginRequestData
import hr.foi.techtitans.ttpay.login_username_password.data.service.LoginResponseData
import hr.foi.techtitans.ttpay.login_username_password.data.service.LoginService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import hr.foi.techtitans.ttpay.login_username_password.network.RetrofitClientLogin


class LoginDataSource {

    private val loginService: LoginService = RetrofitClientLogin.getInstance(8080)
        .create(LoginService::class.java)

    fun callServerLogin(
        enteredUsername: String,
        enteredPassword: String,
        callback: (LoginResult<LoggedInUser>) -> Unit
    ) {
        val loginRequest = LoginRequestData(enteredUsername, enteredPassword)

        loginService.login(loginRequest).enqueue(object : Callback<LoginResponseData> {
            override fun onResponse(call: Call<LoginResponseData>, response: Response<LoginResponseData>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    responseBody?.let { loginResponse ->

                        val token = loginResponse.body.token

                        // Decode JWT token
                        val decodedJWT = JWT.decode(token)

                        // Get role from JWT
                        val role = decodedJWT.getClaim("role").asString()

                     //   Log.d("Role:", role)

                        val userUsername = decodedJWT.getClaim("username").asString()
                     //   Log.d("USERNAME: ",userUsername)

                        // LoggedInUser for the user which is logged in
                        val loggedInUser = LoggedInUser(
                            username = userUsername,
                            token = token,
                            role = role
                        )

                        callback(LoginResult.Success(loggedInUser))
                    }
                } else {
                    callback(LoginResult.Error(Exception("Invalid username or password")))
                }
            }

            override fun onFailure(call: Call<LoginResponseData>, t: Throwable) {
                callback(LoginResult.Error(Exception(t.message)))
            }
        })
    }

    fun logout() {
        // TODO: revoke authentication
    }
}