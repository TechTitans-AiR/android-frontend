package hr.foi.techtitans.ttpay.login_modular.activity_login


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.core.LoginOutcomeListener
import hr.foi.techtitans.ttpay.login_UsernamePassword.UsernamePasswordLoginHandler
import hr.foi.techtitans.ttpay.login_UsernamePassword.UsernamePasswordLoginToken
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.AdminHomeActivity
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.MainActivity
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.MerchantHomeActivity
import org.json.JSONObject

class LoginUsernamePassword : AppCompatActivity(), LoginOutcomeListener {

    private var loginHandler : UsernamePasswordLoginHandler=UsernamePasswordLoginHandler()
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var btnBack:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_username_password)
        progressBar = findViewById(R.id.progressBar)
        usernameEditText = findViewById(R.id.editTextUsername)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.btn_login_login_activity)

        btnBack=findViewById(R.id.btnBack)

        btnBack.setOnClickListener{
            val main= Intent(this, MainActivity::class.java)
            startActivity(main)
            finish()
        }


        loginButton.setOnClickListener {
            val enteredUsername = usernameEditText.text.toString()
            val enteredPassword = passwordEditText.text.toString()

            showProgressBar()

            val loginToken = UsernamePasswordLoginToken(enteredUsername, enteredPassword)
            loginHandler.handleLogin(loginToken, this)

        }
    }

    override fun onSuccessfulLogin(loggedInUser: LoggedInUser) {
        Log.d("onLoginSuccess: ", loggedInUser.toString())
            try {
                val loggedUser = LoggedInUser(
                    userId = loggedInUser.userId,
                    username = loggedInUser.username,
                    token = loggedInUser.token,
                    role = loggedInUser.role
                )
                    Log.d("onLoginSuccess_Token", loggedUser.token)
                    Log.d("onLoginSuccess_Role", loggedUser.role)
                    Log.d("onLoginSuccess_Username", loggedUser.username)
                    Log.d("onLoginSuccess_UserId", loggedUser.userId)

                when(loggedInUser.role){
                    "admin"-> {
                        val adminHomeIntent = Intent(this@LoginUsernamePassword, AdminHomeActivity::class.java)
                        adminHomeIntent.putExtra("username", loggedUser.username)
                        Log.d("LoggedInUser",loggedUser.toString())
                        adminHomeIntent.putExtra("loggedInUser", loggedUser)
                        Toast.makeText(this@LoginUsernamePassword, "You are Admin!", Toast.LENGTH_SHORT).show()
                        startActivity(adminHomeIntent)
                        finish()
                    }
                    "merchant"-> {
                        val merchantHome=Intent(this@LoginUsernamePassword, MerchantHomeActivity::class.java)
                        merchantHome.putExtra("username", loggedInUser.username)
                        merchantHome.putExtra("loggedInUser", loggedUser)
                        Toast.makeText(this@LoginUsernamePassword, "You are Merchant!", Toast.LENGTH_SHORT).show()
                        startActivity(merchantHome)
                        finish()
                    }
                    else->{
                        Toast.makeText(this@LoginUsernamePassword, "Something went wrong!", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                Log.d("Error parsing json!!", e.toString())
                e.printStackTrace()
            }

    }

    override fun onFailedLogin(reason: String?) {
        hideProgressBar()
        if (reason != null) {
            Log.d("Error message: ", reason)
            Toast.makeText(this@LoginUsernamePassword, "Login failed!", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("Error message: ", "Unknown error")
            Toast.makeText(this@LoginUsernamePassword, "Login failed due to an unknown error!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

}