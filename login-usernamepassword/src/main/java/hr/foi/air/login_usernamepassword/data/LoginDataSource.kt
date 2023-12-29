package hr.foi.air.login_usernamepassword.data

import android.util.Log
import com.auth0.jwt.JWT
import hr.foi.air.login_usernamepassword.data.model.LoggedInUser
import hr.foi.air.login_usernamepassword.data.model.LoginRequestData
import hr.foi.air.login_usernamepassword.data.model.LoginResponseData
import hr.foi.air.login_usernamepassword.data.service.LoginServiceUsernamePassword
import hr.foi.techtitans.ttpay.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource {

    private val loginService: LoginServiceUsernamePassword = RetrofitClient.getInstance(8080)
        .create(LoginServiceUsernamePassword::class.java)

    fun callServerLogin(
        enteredUsername: String,
        enteredPassword: String,
        callback: (Result<LoggedInUser>) -> Unit
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

                        Log.d("Role:", role)

                        val userUsername = decodedJWT.getClaim("username").asString()
                        Log.d("USERNAME: ",userUsername)

                        // LoggedInUser for the user which is logged in
                        val loggedInUser = LoggedInUser(
                            username = userUsername,
                            token = token,
                            role = role
                        )

                        callback(Result.Success(loggedInUser))
                    }
                } else {
                    callback(Result.Error(Exception("Invalid username or password")))
                }
            }

            override fun onFailure(call: Call<LoginResponseData>, t: Throwable) {
                callback(Result.Error(Exception(t.message)))
            }
        })
    }

    fun logout() {
        // TODO: revoke authentication
    }
}