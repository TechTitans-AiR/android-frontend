package hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar


import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import hr.foi.air.login_usernamepassword.data.LoginDataSource
import hr.foi.air.login_usernamepassword.data.LoginRepository
import hr.foi.air.login_usernamepassword.data.LoginResult
import hr.foi.air.login_usernamepassword.data.model.LoggedInUser
import hr.foi.techtitans.ttpay.R


class LoginActivity : AppCompatActivity() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var loginRepository: LoginRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        progressBar = findViewById(R.id.progressBar)
        usernameEditText = findViewById(R.id.editTextUsername)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.btn_login_login_activity)


        // LoginRepository initializing
        loginRepository = LoginRepository(LoginDataSource())

        loginButton.setOnClickListener {
            val enteredUsername = usernameEditText.text.toString()
            val enteredPassword = passwordEditText.text.toString()

            if (enteredPassword.isNotEmpty() && enteredUsername.isNotEmpty()) {

                showProgressBar()

                // Call the server for login through the repository
                loginRepository.callServerLogin(enteredUsername, enteredPassword) { result ->
                    // Handle the result from the repository
                    when (result) {
                        is LoginResult.Success -> {
                            val user = result.data
                            // Handle successful login, navigate accordingly
                            handleLoginSuccess(user)
                        }
                        is LoginResult.Error -> {
                            // Handle login error
                            hideProgressBar()
                            val errorMessage = result.exception.localizedMessage ?: "Unknown error occurred"
                            Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show()
                        }
                        else -> {
                            // Handle any other unexpected case
                            hideProgressBar()
                            Toast.makeText(this, "Unexpected result", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun handleLoginSuccess(loggedInUser: LoggedInUser) {
        when (loggedInUser.role) {
            "admin" -> {
                val adminHomeIntent = Intent(this, AdminHomeActivity::class.java)
                adminHomeIntent.putExtra("username", loggedInUser.username)
                Toast.makeText(this, "You are Admin!", Toast.LENGTH_SHORT).show()
                startActivity(adminHomeIntent)
                finish()
            }
            "merchant" -> {
                val merchantHome = Intent(this, MerchantHomeActivity::class.java)
                merchantHome.putExtra("username", loggedInUser.username)
                Toast.makeText(this, "You are Merchant!", Toast.LENGTH_SHORT).show()
                startActivity(merchantHome)
                finish()
            }
            else -> {
                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
            }
        }
    }


}