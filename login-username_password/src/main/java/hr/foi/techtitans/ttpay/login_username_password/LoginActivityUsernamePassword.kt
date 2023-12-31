package hr.foi.techtitans.ttpay.login_username_password

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import hr.foi.techtitans.ttpay.login_username_password.data.LoginDataSource
import hr.foi.techtitans.ttpay.login_username_password.data.LoginRepository
import hr.foi.techtitans.ttpay.login_username_password.data.LoginResult
import android.app.Activity

class LoginActivityUsernamePassword : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button

    private lateinit var progressBar: ProgressBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_activity_username_password)

        progressBar = findViewById(R.id.progressBar)
        usernameEditText = findViewById(R.id.editTextUsername)
        passwordEditText = findViewById(R.id.editTextPassword)
        loginButton = findViewById(R.id.btn_login_login_activity)


        loginButton.setOnClickListener {
            val enteredUsername = usernameEditText.text.toString()
            val enteredPassword = passwordEditText.text.toString()


            val loginDataSource = LoginDataSource()
            val loginRepository = LoginRepository(loginDataSource)

            if (enteredPassword.isNotEmpty() && enteredUsername.isNotEmpty()) {
                // Show progress bar when login button is clicked
                showProgressBar()
                // Call the server for login
                loginRepository.callServerLogin(enteredUsername, enteredPassword) { result ->
                    when (result) {
                        is LoginResult.Success -> {
                            val loggedInUser = result.data

                            val resultIntent = Intent()
                            resultIntent.putExtra("loggedInUser", loggedInUser) // loggedInUser je objekt tipa LoggedInUser

                            setResult(Activity.RESULT_OK, resultIntent)
                            finish()
                        }

                        is LoginResult.Error -> {
                            val exception = result.exception
                            // Obrada greške pri prijavi
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
}
