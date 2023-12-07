package com.example.ttpay.navigationBar.activities

import android.content.ComponentCallbacks
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.ttpay.R
import com.example.ttpay.model.User
import com.example.ttpay.navigationBar.activities.login.LoginRequest
import com.example.ttpay.navigationBar.activities.login.LoginResponse
import com.example.ttpay.navigationBar.activities.login.LoginService
import com.example.ttpay.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.editTextUsername)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.btn_login_login_activity)

        val adminUser = User.createAdmin()

        loginButton.setOnClickListener {
            val enteredUsername = usernameEditText.text.toString()
            val enteredPassword = passwordEditText.text.toString()

            //for login
            callServerLogin(enteredUsername,enteredPassword)

           /* if (enteredUsername == adminUser.username && enteredPassword == adminUser.password) {
                // Correct login credentials, redirect to a new page
                val intent = Intent(this, AdminHomeActivity::class.java)
                startActivity(intent)
                finish() // Close the current activity
            } else {
                // Incorrect credentials, show an error message
                Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
            }*/
        }
    }

    private fun callServerLogin(enteredUsername: String, enteredPassword: String) {
        val retrofit = RetrofitClient.getInstance(8080)//za account_management
        val loginService = retrofit.create(LoginService::class.java)
        val loginRequest = LoginRequest(enteredUsername, enteredPassword)

        loginService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token

                    Log.d("TOKEN: ", response.body().toString())

                    if (!token.isNullOrEmpty()) {
                        val intent = Intent(this@LoginActivity, AdminHomeActivity::class.java)
                        startActivity(intent)
                        finish() // Zatvori trenutnu aktivnost
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Login failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}