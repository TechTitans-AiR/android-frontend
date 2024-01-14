package hr.foi.techtitans.ttpay.login_modular.activity_login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.MainActivity

class LoginWithPIN : AppCompatActivity() {

    private lateinit var editTextPIN:EditText
    private lateinit var btnLogin:Button
    private lateinit var btnBack:ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_with_pin)

        editTextPIN = findViewById(R.id.digitCodePIN)
        btnLogin=findViewById(R.id.btn_login_PIN_activity)
        btnBack=findViewById(R.id.imgBackButton)


        btnBack.setOnClickListener{
            val main= Intent(this, MainActivity::class.java)
            startActivity(main)
            finish()
        }

        btnLogin.setOnClickListener{

        }


    }
}