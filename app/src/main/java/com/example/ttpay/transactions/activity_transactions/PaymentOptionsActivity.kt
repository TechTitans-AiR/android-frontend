package com.example.ttpay.transactions.activity_transactions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.ttpay.R

class PaymentOptionsActivity : AppCompatActivity() {

    private lateinit var userUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_options)

        userUsername = intent.getStringExtra("username") ?: ""
    }
}