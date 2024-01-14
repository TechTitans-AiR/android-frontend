package hr.foi.air.login_pin

import android.util.Log
import com.auth0.jwt.JWT
import com.google.gson.Gson
import hr.foi.air.login_pin.service.LoginServicePIN
import hr.foi.air.login_pin.service.RetrofitPIN
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.core.LoginHandler
import hr.foi.techtitans.ttpay.core.LoginOutcomeListener
import hr.foi.techtitans.ttpay.core.LoginToken
import hr.foi.techtitans.ttpay.core.network.data.LoginRequestData
import hr.foi.techtitans.ttpay.core.network.data.LoginRequestDataPIN
import hr.foi.techtitans.ttpay.core.network.data.LoginResponseData
import hr.foi.techtitans.ttpay.core.network.models.ErrorResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PINLoginHandler : LoginHandler{
    companion object {
        @Volatile
        private var instance: PINLoginHandler? = null

        fun getInstance(): PINLoginHandler {
            return instance ?: synchronized(this) {
                instance ?: PINLoginHandler().also { instance = it }
            }
        }
    }

    private val loginServicePIN: LoginServicePIN  = RetrofitPIN.getInstance(8080)
        .create(LoginServicePIN::class.java)


    override fun handleLogin(
        loginToken: LoginToken,
        loginListener: LoginOutcomeListener
    ) {

        if (loginToken !is PINLoginToken) {
            throw IllegalArgumentException("Must receive 'PINLoginToken' instance for 'loginToken'!")
        }

        val authorizers = loginToken.getAuthorizers()
        val pin = authorizers["pin"]!!
        Log.d("Entered pin: ", pin)

        val loginRequest = LoginRequestDataPIN(pin)

        loginServicePIN.login(loginRequest).enqueue(object : Callback<LoginResponseData> {

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