package hr.foi.techtitans.ttpay.transactions.activity_transactions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.AdminHomeActivity
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.MerchantHomeActivity

class TransactionCompletionActivity : AppCompatActivity() {

    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private var totalAmount: Double = 0.0
    private var differenceAmount: Double = 0.0
    private var isCardPayment: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_completion)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""
        totalAmount = intent.getDoubleExtra("totalAmount", 0.0)
        differenceAmount = intent.getDoubleExtra("differenceAmount", 0.0)
        isCardPayment = intent.getBooleanExtra("isCardPayment", false)

        val tvAmountDifference: TextView = findViewById(R.id.tv_amount_difference)
        if (isCardPayment) {
            // If it's a card payment, hide the tvAmountDifference
            tvAmountDifference.visibility = View.GONE
        } else {
            tvAmountDifference.text = "Amount Difference: $differenceAmount"
        }

        val btnBackToHome: Button = findViewById(R.id.btn_back_to_home)
        btnBackToHome.setOnClickListener {
            val role = loggedInUser.role // Assuming role is a property in LoggedInUser class

            if (role == "admin") {
                // Open AdminHomeActivity
                val intent = Intent(this, AdminHomeActivity::class.java)
                intent.putExtra("loggedInUser", loggedInUser)
                intent.putExtra("username", userUsername)
                startActivity(intent)
            } else if (role == "merchant") {
                // Open MerchantHomeActivity
                val intent = Intent(this, MerchantHomeActivity::class.java)
                intent.putExtra("loggedInUser", loggedInUser)
                intent.putExtra("username", userUsername)
                startActivity(intent)
            }

            finish()
        }
    }
}