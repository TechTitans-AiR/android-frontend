package hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.login_modular.activity_login.Login_UsernamePassword


class MainActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButtonUsernamePassword: Button = findViewById(R.id.btn_login)
        val loginButtonFingertip:Button=findViewById(R.id.btn_login2)


        loginButtonUsernamePassword.setOnClickListener {
            val loginIntent = Intent(this, Login_UsernamePassword::class.java)
            startActivity(loginIntent)
            finish()
        }
        loginButtonFingertip.setOnClickListener{
            //TODO: intent for activity for fingertip
        }
    }
}