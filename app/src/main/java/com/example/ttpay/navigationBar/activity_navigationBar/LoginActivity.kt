package com.example.ttpay.navigationBar.activity_navigationBar


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import com.auth0.jwt.JWT
import com.example.ttpay.R
import com.example.ttpay.navigationBar.activity_navigationBar.login.LoginRequest
import com.example.ttpay.navigationBar.activity_navigationBar.login.LoginResponse
import com.example.ttpay.navigationBar.activity_navigationBar.login.LoginService
import com.example.ttpay.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressBar = findViewById(R.id.progressBar)
        usernameEditText = findViewById(R.id.editTextUsername)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.btn_login_login_activity)


        loginButton.setOnClickListener {
            val enteredUsername = usernameEditText.text.toString()
            val enteredPassword = passwordEditText.text.toString()

            if(enteredPassword.isNotEmpty() && enteredUsername.isNotEmpty()){
                // Show progress bar when login button is clicked
                showProgressBar()
                // Call the server for login
                callServerLogin(enteredUsername,enteredPassword)
            }

        }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun callServerLogin(enteredUsername: String, enteredPassword: String) {
        val retrofit = RetrofitClient.getInstance(8080)
        val loginService = retrofit.create(LoginService::class.java)
        val loginRequest = LoginRequest(enteredUsername, enteredPassword)

        loginService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                hideProgressBar() // Hide the progress bar when the response is received
                Log.d("Response 1: ", response.body().toString())//check if response have something

                val statusCode = response.code() // get status code
                Log.d("Status Code response:", statusCode.toString())

                if (response.isSuccessful) {
                    val responseBody = response.body()

                    Log.d("Status Code success:", statusCode.toString())

                    responseBody?.let { loginResponse ->
                        val token = loginResponse.body.token

                        Log.d("TOKEN: ", token)

                        //decode JWT token
                        val decodedJWT = JWT.decode(token)

                        // get role from JWT
                        var role=""
                        role=decodedJWT.getClaim("role").asString()

                        // which role is user
                        Log.d("Role:", role)

                        val userUsername = decodedJWT.getClaim("username").asString()
                        Log.d("USERNAME: ",userUsername)


                        if (token.isNotEmpty()) {

                            when(role){
                                "admin"-> {
                                    val adminHomeIntent = Intent(this@LoginActivity, AdminHomeActivity::class.java)
                                    adminHomeIntent.putExtra("username", userUsername)
                                    Toast.makeText(this@LoginActivity, "You are Admin!", Toast.LENGTH_SHORT).show()
                                    startActivity(adminHomeIntent)
                                    finish()
                                }
                                "merchant"-> {
                                    val merchantHome=Intent(this@LoginActivity, MerchantHomeActivity::class.java)
                                    merchantHome.putExtra("username", userUsername)
                                    Toast.makeText(this@LoginActivity, "You are Merchant!", Toast.LENGTH_SHORT).show()
                                    startActivity(merchantHome)
                                    finish()
                                }
                                else->{
                                    Toast.makeText(this@LoginActivity, "Something went wrong!", Toast.LENGTH_SHORT).show()
                                }
                            }

                        } else {
                            Log.d("Status Code token null:", statusCode.toString())
                            Toast.makeText(this@LoginActivity, "Token is null!", Toast.LENGTH_SHORT).show()
                        }
                    }
                } else {
                    // print the status code
                    Log.d("Status Code Failure Call:", statusCode.toString())
                    Toast.makeText(this@LoginActivity, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                hideProgressBar() // Hide the progress bar when the call fails
                Toast.makeText(this@LoginActivity, "Login failed: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }



}