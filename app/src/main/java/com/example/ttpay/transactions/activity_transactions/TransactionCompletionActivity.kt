package com.example.ttpay.transactions.activity_transactions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.ttpay.R

class TransactionCompletionActivity : AppCompatActivity() {

    private lateinit var userUsername: String
    private var totalAmount: Double = 0.0
    private var differenceAmount: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_completion)

        userUsername = intent.getStringExtra("username") ?: ""
        totalAmount = intent.getDoubleExtra("totalAmount", 0.0)
        differenceAmount = intent.getDoubleExtra("differenceAmount", 0.0)

        // Ovdje možete koristiti razliku (differenceAmount) kako želite
        val tvAmountDifference: TextView = findViewById(R.id.tv_amount_difference)
        tvAmountDifference.text = "Amount Difference: $differenceAmount"
    }
}