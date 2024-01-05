package hr.foi.techtitans.ttpay.login_modular.activity_login


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.login_UsernamePassword.LoginHandlerUsernamePassword
import hr.foi.techtitans.ttpay.login_UsernamePassword.data.LoginCallback
import hr.foi.techtitans.ttpay.login_UsernamePassword.data.LoginResponseData
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.AdminHomeActivity
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.MerchantHomeActivity
import org.json.JSONObject

class Login_UsernamePassword : AppCompatActivity(), LoginCallback {

    var loginHandler : LoginHandlerUsernamePassword = LoginHandlerUsernamePassword.getInstance()
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    private lateinit var progressBar: ProgressBar

    private var loginCallback: LoginCallback? = this


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_username_password)
        progressBar = findViewById(R.id.progressBar)
        usernameEditText = findViewById(R.id.editTextUsername)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.btn_login_login_activity)


        loginButton.setOnClickListener {
            val enteredUsername = usernameEditText.text.toString()
            val enteredPassword = passwordEditText.text.toString()

            showProgressBar()

            loginHandler.performLogin(enteredUsername, enteredPassword, loginCallback)


        }
    }

    override fun onLoginSuccess(loggedUser: LoginResponseData?){
        Log.d("onLoginSuccess: ", loggedUser.toString())
        loggedUser?.let { responseBody ->
            try {
                responseBody.let { loginResponse ->
                    val token = loginResponse.body.token

                    val parts = token.split(".")
                    val decodedBody = String(Base64.decode(parts[1], Base64.DEFAULT))
                    Log.d("onLoginSuccess_Decoded Body: ",decodedBody)

                    //decoding
                    val jsonObject = JSONObject(decodedBody)

                    // receiving data from JSON object
                    val role = jsonObject.getString("role")
                    val userUsername = jsonObject.getString("username")
                    val userId = jsonObject.getString("userId")

                    Log.d("onLoginSuccess_Token", token)
                    Log.d("onLoginSuccess_Role", role)
                    Log.d("onLoginSuccess_Username", userUsername)
                    Log.d("onLoginSuccess_UserId", userId)

                // storing data od looged in user
                val loggedInUser = LoggedInUser(
                    userId = userId,
                    username = userUsername,
                    token = token,
                    role = role
                )

                when(role){
                    "admin"-> {
                        val adminHomeIntent = Intent(this@Login_UsernamePassword, AdminHomeActivity::class.java)
                        adminHomeIntent.putExtra("username", loggedInUser.username)
                        adminHomeIntent.putExtra("loggedInUser", loggedInUser)
                        Toast.makeText(this@Login_UsernamePassword, "You are Admin!", Toast.LENGTH_SHORT).show()
                        startActivity(adminHomeIntent)
                        finish()
                    }
                    "merchant"-> {
                        val merchantHome=Intent(this@Login_UsernamePassword, MerchantHomeActivity::class.java)
                        merchantHome.putExtra("username", userUsername)
                        merchantHome.putExtra("loggedInUser", loggedInUser)
                        Toast.makeText(this@Login_UsernamePassword, "You are Merchant!", Toast.LENGTH_SHORT).show()
                        startActivity(merchantHome)
                        finish()
                    }
                    else->{
                        Toast.makeText(this@Login_UsernamePassword, "Something went wrong!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            } catch (e: Exception) {
                Log.d("Error parsing json!!", e.toString())
                e.printStackTrace()
            }
        }
    }

    override fun onLoginFailure(errorMessage: String?) {
        hideProgressBar() // Hide the progress bar when the call fails
        Log.d("Error message: ", errorMessage.toString())
        Toast.makeText(this@Login_UsernamePassword, "Login failed!", Toast.LENGTH_SHORT).show()
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

}