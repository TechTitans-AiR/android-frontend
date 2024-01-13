package hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.login_modular.activity_login.LoginUsernamePassword
import hr.foi.techtitans.ttpay.login_modular.activity_login.LoginWithPIN


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButtonUsernamePassword: Button = findViewById(R.id.btn_login)
        val loginButtonFingertip:Button=findViewById(R.id.btn_login2)


        loginButtonUsernamePassword.setOnClickListener {
            val loginIntent = Intent(this, LoginUsernamePassword::class.java)
            startActivity(loginIntent)
            finish()
        }
        loginButtonFingertip.setOnClickListener{
            val loginIntent = Intent(this, LoginWithPIN::class.java)
            startActivity(loginIntent)
            finish()
        }
    }
}