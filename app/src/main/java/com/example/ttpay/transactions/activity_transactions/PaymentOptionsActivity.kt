package com.example.ttpay.transactions.activity_transactions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import com.example.ttpay.R
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.transactions.network_transactions.ShoppingCartItem
import com.google.android.material.bottomnavigation.BottomNavigationView

class PaymentOptionsActivity : AppCompatActivity() {

    private lateinit var userUsername: String
    private lateinit var shoppingCartItems: MutableList<ShoppingCartItem>
    private var totalAmount: Double = 0.0
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var complete_payment: Button
    private lateinit var radioGroupPaymentOptions: RadioGroup
    private lateinit var edtCashAmount: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_options)

        userUsername = intent.getStringExtra("username") ?: ""
        shoppingCartItems =
            intent.getSerializableExtra("shoppingCartItems") as MutableList<ShoppingCartItem>
        totalAmount = intent.getDoubleExtra("totalAmount", 0.0)

        Log.d("TransactionSummaryActivity", "onCreate: username: $userUsername")
        Log.d("TransactionSummaryActivity", "onCreate: shoppingCartItems: $shoppingCartItems")
        Log.d("TransactionSummaryActivity", "onCreate: totalAmount: $totalAmount")

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, userUsername)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        radioGroupPaymentOptions = findViewById(com.example.ttpay.R.id.radioGroupPaymentOptions)
        edtCashAmount = findViewById(R.id.edt_cash_amount)

        complete_payment = findViewById(R.id.btn_complete_payment)
        complete_payment.setOnClickListener {
            val intent = Intent(this, TransactionCompletionActivity::class.java)
            intent.putExtra("shoppingCartItems", ArrayList(shoppingCartItems))
            intent.putExtra("totalAmount", totalAmount)
            intent.putExtra("username", userUsername)
            startActivity(intent)
        }
    }
}