package hr.foi.techtitans.ttpay.transactions.activity_transactions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.accountManagement.activity_accountManagement.AllMerchantsActivity
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.AdminHomeActivity
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler

class TransactionsActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var loggedInUser: LoggedInUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transactions)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("AllTransationsActivity - LoggedInUser", loggedInUser.toString())
            startActivity(intent)
            finish()
        }
    }

    fun onTransactionsSectionClick(view: View) {
        val intent = Intent(this, AllTransactionsActivity::class.java)
        intent.putExtra("loggedInUser", loggedInUser)
        Log.d("onAllMerchantsClick - LoggedInUser",loggedInUser.toString())
        startActivity(intent)
        finish()
    }
    fun onOnlyMyTransactionsSectionClick(view: View) {
        val intent = Intent(this, AdminTransactionsAsMerchantActivity::class.java)
        intent.putExtra("loggedInUser", loggedInUser)
        Log.d("onAllMerchantsClick - LoggedInUser",loggedInUser.toString())
        startActivity(intent)
        finish()
    }
}