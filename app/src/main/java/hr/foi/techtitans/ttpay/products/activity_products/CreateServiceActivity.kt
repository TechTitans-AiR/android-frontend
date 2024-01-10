package hr.foi.techtitans.ttpay.products.activity_products

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler

class CreateServiceActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var loadingDataProgressBar: ProgressBar
    private lateinit var actionBack: ImageView
    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_service)

        loadingDataProgressBar = findViewById(R.id.loadingData)
        actionBack = findViewById(R.id.action_back)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        actionBack.setOnClickListener {
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("CreateServiceActivity - LoggedInUser",loggedInUser.toString())
            intent.putExtra("username", userUsername)
            setResult(RESULT_OK, intent)
            finish()
        }


    }
}