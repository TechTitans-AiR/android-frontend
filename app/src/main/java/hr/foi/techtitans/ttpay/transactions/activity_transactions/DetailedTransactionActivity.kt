package hr.foi.techtitans.ttpay.transactions.activity_transactions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser

class DetailedTransactionActivity : AppCompatActivity() {

    private lateinit var transactionId: String
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser

    // UI elements
    private lateinit var imgBack : ImageView
    private lateinit var merchant : TextView
    private lateinit var amount : TextView
    private lateinit var currency : TextView
    private lateinit var dateCreated : TextView
    private lateinit var dateUpdated : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_transaction)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""
        transactionId = intent.getStringExtra("transactionId") ?: ""
        Log.d("DetailedTransactionActivity", "Transaction id: $transactionId")

        // initialize UI elements
        merchant = findViewById(R.id.textView_merchant)
        amount = findViewById(R.id.textView_amount)
        currency = findViewById(R.id.textView_currency)
        dateCreated = findViewById(R.id.textView_createdAt)
        dateUpdated = findViewById(R.id.textView_updatedAt)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        imgBack = findViewById(R.id.back_button)

        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        imgBack.setOnClickListener {
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("username", userUsername)
            onBackPressed()
        }
    }
}