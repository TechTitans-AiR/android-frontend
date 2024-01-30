package hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.core.LoginOutcomeListener
import hr.foi.techtitans.ttpay.login_modular.model_login.LoginManager
import hr.foi.techtitans.ttpay.login_modular.model_login.ModuleAdapter


class MainActivity : AppCompatActivity() {

    private lateinit var loginManager: LoginManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var outcomeListener: LoginOutcomeListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

            loginManager = LoginManager(this)
            recyclerView = findViewById(R.id.recyclerViewButtonModules)
            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = ModuleAdapter(loginManager.getModules(), this)

    }
}