package hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import hr.foi.techtitans.ttpay.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton: Button = findViewById(R.id.btn_login)

        loginButton.setOnClickListener {
            /*val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent) */
            val intent = Intent(
                this,
                hr.foi.air.login_usernamepassword.ui.login.LoginUsernamePassword::class.java
            )
            startActivity(intent)
        }
    }
}