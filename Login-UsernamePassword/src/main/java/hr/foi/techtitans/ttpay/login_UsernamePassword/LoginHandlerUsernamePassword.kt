package hr.foi.techtitans.ttpay.login_UsernamePassword


import android.util.Log
import hr.foi.techtitans.ttpay.login_UsernamePassword.data.service.LoginCallback
import hr.foi.techtitans.ttpay.login_UsernamePassword.data.service.LoginRequestData
import hr.foi.techtitans.ttpay.login_UsernamePassword.data.service.LoginResponseData
import hr.foi.techtitans.ttpay.login_UsernamePassword.data.service.`LoginService-UserPass`
import hr.foi.techtitans.ttpay.login_UsernamePassword.data.service.Retrofit
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


 class LoginHandlerUsernamePassword private constructor() {

    companion object {
        @Volatile
        private var instance: LoginHandlerUsernamePassword? = null

        fun getInstance(): LoginHandlerUsernamePassword {
            return instance ?: synchronized(this) {
                instance ?: LoginHandlerUsernamePassword().also { instance = it }
            }
        }
    }

    private val loginService: `LoginService-UserPass` = Retrofit.getInstance(8080)
        .create(`LoginService-UserPass`::class.java)


     fun performLogin(enteredUsername: String,enteredPassword:String ,callback: LoginCallback?) {

        val loginRequest = LoginRequestData(enteredUsername, enteredPassword)

        // login call
        loginService.login(loginRequest).enqueue(object : Callback<LoginResponseData> {
            override fun onResponse(call: Call<LoginResponseData>, response: Response<LoginResponseData>) {
                if (response.isSuccessful) {
                    Log.d("onResponse:", response.body().toString())

                    val responseBody = response.body()
                    responseBody?.let { loginResponse ->
                        try {
                            val token = responseBody.body.token

                            Log.d("Token: ", token)


                            // Prikaz dekodiranog tijela tokena
                            Log.d("Response body: ",responseBody.toString())


                            callback?.onLoginSuccess(responseBody)
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }
                }else {
                        callback?.onLoginFailure("Invalid username or password")
                    }
                }
            override fun onFailure(call: Call<LoginResponseData>, t: Throwable) {
                Log.d("onFailure:",call.toString())
                //callback function onLoginFailure from LoginHandler.kt
                callback?.onLoginFailure(t.message)
            }
        })
    }

}
