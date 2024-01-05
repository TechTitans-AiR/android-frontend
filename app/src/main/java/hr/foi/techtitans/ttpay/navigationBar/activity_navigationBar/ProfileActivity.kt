package hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView

class ProfileActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String

    private lateinit var txtViewFullName: TextView
    private lateinit var txtViewUserRole: TextView
    private lateinit var txtViewDateOfBirth: TextView
    private lateinit var txtViewDateCreated: TextView
    private lateinit var txtViewUsername: TextView
    private lateinit var txtViewPassword: TextView
    private lateinit var txtViewEmail: TextView
    private lateinit var txtViewPhone: TextView
    private lateinit var txtViewAddress: TextView
    private lateinit var txtViewStatus: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        txtViewFullName = findViewById(R.id.textView_fullName)
        txtViewUserRole = findViewById(R.id.textView_userRole)
        txtViewDateOfBirth = findViewById(R.id.textView_dateOfBirth)
        txtViewDateCreated = findViewById(R.id.textView_dateCreated)
        txtViewUsername = findViewById(R.id.textView_username)
        txtViewPassword = findViewById(R.id.textView_password)
        txtViewEmail = findViewById(R.id.textView_email)
        txtViewPhone = findViewById(R.id.textView_phone)
        txtViewAddress = findViewById(R.id.textView_address)
        txtViewStatus = findViewById(R.id.textView_status)

        userUsername = intent.getStringExtra("username") ?: ""

        // BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, userUsername)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        btnBack.setOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }
    }
}