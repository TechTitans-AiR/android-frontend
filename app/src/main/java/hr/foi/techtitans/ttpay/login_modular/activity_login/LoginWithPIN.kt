package hr.foi.techtitans.ttpay.login_modular.activity_login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import hr.foi.air.login_pin.PINLoginHandler
import hr.foi.air.login_pin.PINLoginToken
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.core.LoginOutcomeListener
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.AdminHomeActivity
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.MainActivity
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.MerchantHomeActivity

class LoginWithPIN : AppCompatActivity(), LoginOutcomeListener {

    private var loginHandler : PINLoginHandler = PINLoginHandler()


    private lateinit var editTextPIN:EditText
    private lateinit var btnLogin:Button
    private lateinit var btnBack:ImageView
    private lateinit var progressBarPin:ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_with_pin)

        editTextPIN = findViewById(R.id.digitCodePIN)
        btnLogin=findViewById(R.id.btn_login_PIN_activity)
        btnBack=findViewById(R.id.imgBackButton)
        progressBarPin=findViewById(R.id.progressBarPIN)


        btnBack.setOnClickListener{
            val main= Intent(this, MainActivity::class.java)
            startActivity(main)
            finish()
        }

        btnLogin.setOnClickListener{
            val enteredPIN = editTextPIN.text.toString()

            showProgressBar()

            val loginTokenPIN = PINLoginToken(enteredPIN)
            loginHandler.handleLogin(loginTokenPIN, this)
        }


    }

    private fun showProgressBar() {
        progressBarPin.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBarPin.visibility = View.GONE
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


            when(loggedInUser.role){
                "admin"-> {
                    val adminHomeIntent = Intent(this@LoginWithPIN, AdminHomeActivity::class.java)
                    adminHomeIntent.putExtra("username", loggedUser.username)
                    Log.d("LoggedInUser",loggedUser.toString())
                    adminHomeIntent.putExtra("loggedInUser", loggedUser)
                    Toast.makeText(this@LoginWithPIN, "You are Admin!", Toast.LENGTH_SHORT).show()
                    startActivity(adminHomeIntent)
                    finish()
                }
                "merchant"-> {
                    val merchantHome=Intent(this@LoginWithPIN, MerchantHomeActivity::class.java)
                    merchantHome.putExtra("username", loggedInUser.username)
                    merchantHome.putExtra("loggedInUser", loggedUser)
                    Toast.makeText(this@LoginWithPIN, "You are Merchant!", Toast.LENGTH_SHORT).show()
                    startActivity(merchantHome)
                    finish()
                }
                else->{
                    Toast.makeText(this@LoginWithPIN, "Something went wrong!", Toast.LENGTH_SHORT).show()
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
            Toast.makeText(this@LoginWithPIN, "Login failed!", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("Error message: ", "Unknown error")
            Toast.makeText(this@LoginWithPIN, "Login failed due to an unknown error!", Toast.LENGTH_SHORT).show()
        }
    }
}