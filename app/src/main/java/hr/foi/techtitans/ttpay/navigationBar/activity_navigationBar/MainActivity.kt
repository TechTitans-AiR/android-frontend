package hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.login_username_password.LoginActivityUsernamePassword
import hr.foi.techtitans.ttpay.login_username_password.data.LoggedInUser

class MainActivity : AppCompatActivity() {

    private val LOGIN_REQUEST_CODE = 123 // Bilo koji broj koji odgovara tvojim potrebama

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButtonUsernamePassword: Button = findViewById(R.id.btn_login)
        val loginButtonFingertip:Button=findViewById(R.id.btn_login2)


        loginButtonUsernamePassword.setOnClickListener {
            val loginIntent = Intent(this, LoginActivityUsernamePassword::class.java)
            startActivityForResult(loginIntent, LOGIN_REQUEST_CODE)
            val receivedUser: LoggedInUser? = intent.getParcelableExtra("loggedInUser")
        }
        loginButtonFingertip.setOnClickListener{
            //TODO: intent for activity for fingertip
        }
    }
}