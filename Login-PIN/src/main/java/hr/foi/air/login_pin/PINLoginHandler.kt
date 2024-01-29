package hr.foi.air.login_pin

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.auth0.jwt.JWT
import com.google.gson.Gson
import hr.foi.air.login_pin.service.LoginServicePIN
import hr.foi.air.login_pin.service.RetrofitPIN
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.core.LoginHandler
import hr.foi.techtitans.ttpay.core.LoginOutcomeListener
import hr.foi.techtitans.ttpay.core.LoginToken
import hr.foi.techtitans.ttpay.core.network.data.LoginRequestDataPIN
import hr.foi.techtitans.ttpay.core.network.data.ResponseBodyData
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
        loginListener: LoginOutcomeListener,
    ) {

        if (loginToken !is PINLoginToken) {
            throw IllegalArgumentException("Must receive 'PINLoginToken' instance for 'loginToken'!")
        }

        val authorizers = loginToken.getAuthorizers()
        val pin = authorizers["pin"]!!
        Log.d("Entered pin: ", pin)

        val loginRequest = LoginRequestDataPIN(pin)

        Log.d("Request data: ", loginRequest.toString())
        Log.d("Request data: ", Gson().toJson(loginRequest))


        loginServicePIN.login(loginRequest).enqueue(object : Callback<ResponseBodyData> {

            override fun onResponse(
                call: Call<ResponseBodyData>,
                response: Response<ResponseBodyData>
            ) {
                Log.d("Response: ", response.toString())
                Log.d("Response body before successful: ", response.body().toString())

                if (response.isSuccessful) {
                    Log.d("Response body after successful: ", response.body().toString())

                    val responseBody = response.body()

                    if ( responseBody?.token != null) {
                        Log.d("if:", responseBody.token.toString())
                        val token = responseBody.token

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
                        loginListener.onFailedLogin("User is not in the database, please check input data!")
                        Log.d("User is not in the database, please check input data: ", response.message())
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    if (errorBody != null) {
                        loginListener.onFailedLogin(errorBody.toString())
                    } else {
                        loginListener.onFailedLogin("Login failed because of server error.")
                        Log.d("Login failed: ", response.message())
                    }
                }

            }

            override fun onFailure(call: Call<ResponseBodyData>, t: Throwable) {
                loginListener.onFailedLogin("Login failed. Please try again.")
            }

        })
    }

}