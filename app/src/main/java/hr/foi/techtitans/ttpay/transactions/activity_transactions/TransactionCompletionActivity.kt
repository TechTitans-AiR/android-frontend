package hr.foi.techtitans.ttpay.transactions.activity_transactions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser

class TransactionCompletionActivity : AppCompatActivity() {

    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private var totalAmount: Double = 0.0
    private var differenceAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_completion)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""
        totalAmount = intent.getDoubleExtra("totalAmount", 0.0)
        differenceAmount = intent.getDoubleExtra("differenceAmount", 0.0)

        val tvAmountDifference: TextView = findViewById(R.id.tv_amount_difference)
        tvAmountDifference.text = "Amount Difference: $differenceAmount"

        val btnBackToHome: Button = findViewById(R.id.btn_back_to_home)
        btnBackToHome.setOnClickListener {
        finish()
        }
    }
}