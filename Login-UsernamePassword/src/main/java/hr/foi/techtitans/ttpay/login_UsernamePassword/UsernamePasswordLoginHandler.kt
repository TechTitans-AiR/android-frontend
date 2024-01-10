package hr.foi.techtitans.ttpay.login_UsernamePassword

import android.util.Log
import com.auth0.jwt.JWT
import com.google.gson.Gson
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.core.LoginHandler
import hr.foi.techtitans.ttpay.core.LoginOutcomeListener
import hr.foi.techtitans.ttpay.core.LoginToken
import hr.foi.techtitans.ttpay.core.network.models.ErrorResponseBody
import hr.foi.techtitans.ttpay.core.network.models.ResponseBody
import hr.foi.techtitans.ttpay.core.network.models.SuccessfulResponseBody
import hr.foi.techtitans.ttpay.core.network.data.LoginRequestData
import hr.foi.techtitans.ttpay.core.network.data.LoginResponseData
import hr.foi.techtitans.ttpay.login_UsernamePassword.service.`LoginService-UserPass`
import hr.foi.techtitans.ttpay.login_UsernamePassword.service.Retrofit
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UsernamePasswordLoginHandler : LoginHandler {

    companion object {
        @Volatile
        private var instance: UsernamePasswordLoginHandler? = null

        fun getInstance(): UsernamePasswordLoginHandler {
            return instance ?: synchronized(this) {
                instance ?: UsernamePasswordLoginHandler().also { instance = it }
            }
        }
    }

    private val loginService: `LoginService-UserPass` = Retrofit.getInstance(8080)
        .create(`LoginService-UserPass`::class.java)


        override fun handleLogin(
            loginToken: LoginToken,
            loginListener: LoginOutcomeListener
        ) {

            if (loginToken !is UsernamePasswordLoginToken) {
            throw IllegalArgumentException("Must receive UsernamePasswordLoginToken instance for 'loginToken'!")
        }

        val authorizers = loginToken.getAuthorizers()
        val username = authorizers["username"]!!
        val password = authorizers["password"]!!
            Log.d("Username & password: ", username+ password)

        val loginRequest = LoginRequestData(username, password)
        loginService.login(loginRequest).enqueue(object : Callback<LoginResponseData> {

            override fun onResponse(
                call: Call<LoginResponseData>,
                response: Response<LoginResponseData>
            ) {
                Log.d("Response: ", response.toString())
                if (response.isSuccessful) {
                    val responseBody = response.body()

                    if (responseBody != null && responseBody.body.token.isNotEmpty()) {
                        val token = responseBody.body.token

                        val decodedJWT = JWT.decode(token)

                        val userID = decodedJWT.getClaim("userId").asString()
                        val role = decodedJWT.getClaim("role").asString()
                        val userUsername = decodedJWT.getClaim("username").asString()

                         val loggedInUser = LoggedInUser(
                            userId = userID,
                            username = userUsername,
                            token = token,
                            role = role
                        )

                        loginListener.onSuccessfulLogin(loggedInUser)
                    } else {
                        loginListener.onFailedLogin("No data for login or the data is wrong!")
                        Log.d("No data, wrong data: ", response.message())
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        val errorResponse = Gson().fromJson(errorBody, ErrorResponseBody::class.java)
                        loginListener.onFailedLogin(errorResponse.error_message)
                    } else {
                        loginListener.onFailedLogin("Login failed because of server error.")
                        Log.d("Login failed: ", response.message())
                    }
                }

            }

            override fun onFailure(call: Call<LoginResponseData>, t: Throwable) {
                loginListener.onFailedLogin("Login failed. Please try again.")
            }

        })
    }
}