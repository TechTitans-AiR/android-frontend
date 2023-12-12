package com.example.ttpay.transactions.activity_transactions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.R
import com.example.ttpay.model.NavigationHandler

class TransactionItemActivity : AppCompatActivity() {

    private lateinit var userId: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionId: String
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_item)

        userId = intent.getStringExtra("userId") ?: ""
        Log.d("TransactionItemActivity", "User id: $userId")

        transactionId = intent.getStringExtra("transactionId") ?: ""

    }
}