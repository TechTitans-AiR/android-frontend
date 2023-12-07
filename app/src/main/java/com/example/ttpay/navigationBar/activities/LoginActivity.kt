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

            //instance for Retrofit communication for specific port
            val retrofit = RetrofitClient.getInstance(8080)//za account_management
            val loginService = retrofit.create(LoginService::class.java)
            val call = loginService.login(enteredUsername, enteredPassword)

            calServerLogin(call, enteredUsername,enteredPassword )

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

    private fun calServerLogin(call: Call<LoginResponse>, enteredUsername: String, enteredPassword: String) {
        Log.d("Login", "Username: $enteredUsername, Password: $enteredPassword")
        call.enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val token = response.body()?.token

                    // Successful login --> token saved and redirected to activity
                    if (!token.isNullOrEmpty()) {

                        val intent = Intent(this@LoginActivity, AdminHomeActivity::class.java)
                        startActivity(intent)
                        finish() // Zatvori trenutnu aktivnost
                    }
                } else {
                    // Failed login - prikaži poruku o grešci
                    Log.d("TOKEN: ", "${response.body()?.token.toString()}")
                    Toast.makeText(this@LoginActivity, "Invalid username or password; ${response.body()?.token}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                // Handle failure
                Toast.makeText(this@LoginActivity, "Login failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })

    }
}