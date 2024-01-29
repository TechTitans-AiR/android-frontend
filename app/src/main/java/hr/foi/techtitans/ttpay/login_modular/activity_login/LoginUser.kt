package hr.foi.techtitans.ttpay.login_modular.activity_login

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import hr.foi.air.login_pin.FragmentLoginPIN
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.core.LoginOutcomeListener
import hr.foi.techtitans.ttpay.login_UsernamePassword.FragmentLoginUsernamePass
import hr.foi.techtitans.ttpay.login_modular.model_login.LoginManager
import hr.foi.techtitans.ttpay.login_modular.model_login.Module
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.AdminHomeActivity
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.MainActivity
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.MerchantHomeActivity

class LoginUser : AppCompatActivity(), LoginOutcomeListener {

    private lateinit var btnBack:ImageView
    private  var loginManager:LoginManager= LoginManager(this)
    private var selectedButton:String=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_user)

        btnBack=findViewById(R.id.imgBackButton)
        selectedButton=intent.getStringExtra("selectedButton") ?: ""

        btnBack.setOnClickListener{
            val main= Intent(this, MainActivity::class.java)
            startActivity(main)
            finish()
        }


        val listModule=loginManager.getModules()

        val fragmentContainer = findViewById<FrameLayout>(R.id.fragmentContainer)
        val fragmentTransaction = supportFragmentManager.beginTransaction()

        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        var foundMatchingModule = false

        for(m:Module in listModule){
            if (selectedButton == m.getName()) {
                val fragment = loginManager.getFragmentForModule(m, applicationContext, this)
                fragment?.let {
                    fragmentTransaction.replace(fragmentContainer.id, it)
                    foundMatchingModule = true
                }
            }

            if (foundMatchingModule) {
                break // Break the loop after finding the matching module
            }
        }
        fragmentTransaction.commit()

    }



    override fun onSuccessfulLogin(loggedInUser: LoggedInUser) {
        Log.d("onLoginSuccess: ", loggedInUser.toString())

        try {
            val loggedUser = LoggedInUser(
                userId = loggedInUser.userId,
                username = loggedInUser.username,
                token = loggedInUser.token,
                role = loggedInUser.role
            )


            when(loggedInUser.role){
                "admin"-> {
                    val adminHomeIntent = Intent(this@LoginUser, AdminHomeActivity::class.java)
                    adminHomeIntent.putExtra("username", loggedUser.username)
                    Log.d("LoggedInUser",loggedUser.toString())
                    adminHomeIntent.putExtra("loggedInUser", loggedUser)
                    Toast.makeText(this@LoginUser, "You are Admin!", Toast.LENGTH_SHORT).show()
                    startActivity(adminHomeIntent)
                    finish()
                }
                "merchant"-> {
                    val merchantHome=Intent(this@LoginUser, MerchantHomeActivity::class.java)
                    merchantHome.putExtra("username", loggedInUser.username)
                    merchantHome.putExtra("loggedInUser", loggedUser)
                    Toast.makeText(this@LoginUser, "You are Merchant!", Toast.LENGTH_SHORT).show()
                    startActivity(merchantHome)
                    finish()
                }
                else->{
                    Toast.makeText(this@LoginUser, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }
            }

        } catch (e: Exception) {
            Log.d("Error parsing json!!", e.toString())
            e.printStackTrace()
        }

    }

    override fun onFailedLogin(reason: String?) {
        if (reason != null) {
            Log.d("Error message: ", reason)
            Toast.makeText(this@LoginUser, "${reason}", Toast.LENGTH_SHORT).show()
        } else {
            Log.d("Error message: ", "Unknown error")
            Toast.makeText(this@LoginUser, "Login failed due to an unknown error!", Toast.LENGTH_SHORT).show()
        }
    }

}