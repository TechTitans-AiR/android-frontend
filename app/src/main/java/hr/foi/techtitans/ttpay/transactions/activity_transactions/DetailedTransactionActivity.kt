package hr.foi.techtitans.ttpay.transactions.activity_transactions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView

class DetailedTransactionActivity : AppCompatActivity() {

    private lateinit var transactionId: String
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_transaction)

        transactionId = intent.getStringExtra("transactionId") ?: ""
        Log.d("DetailedTransactionActivity", "Transaction id: $transactionId")

        userUsername = intent.getStringExtra("username") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, userUsername)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            intent.putExtra("username", userUsername)
            onBackPressed()
        }
    }
}