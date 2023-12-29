package com.example.ttpay.transactions.activity_transactions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.R
import com.example.ttpay.navigationBar.model_navigationBar.NavigationHandler
import com.example.ttpay.transactions.model_transactions.TransactionAdapter
import com.example.ttpay.network.RetrofitClient
import com.example.ttpay.transactions.network_transactions.ServiceTransactionManagement
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AlertDialog
import com.example.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import com.example.ttpay.transactions.model_transactions.Transaction
import com.example.ttpay.accountManagement.model_accountManagement.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TransactionItemActivity : AppCompatActivity() {

    private lateinit var userId: String
    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionId: String
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var progressBar: ProgressBar
    private lateinit var adapter: TransactionAdapter
    private lateinit var textViewUserName: TextView
    private lateinit var userUsername: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_item)

        userId = intent.getStringExtra("userId") ?: ""
        Log.d("TransactionItemActivity", "User id: $userId")

        transactionId = intent.getStringExtra("transactionId") ?: ""

        userUsername = intent.getStringExtra("username") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, userUsername)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        recyclerView = findViewById(R.id.recyclerView_all_transactions)
        progressBar = findViewById(R.id.loadingProgressBar)

        adapter = TransactionAdapter(emptyList()) { transaction ->
            val intent = Intent(this, DetailedTransactionActivity::class.java)
            intent.putExtra("transactionId", transaction.id)
            intent.putExtra("username", userUsername)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchUserTransactions(userId)

        textViewUserName = findViewById(R.id.textViewUserName)
        fetchUserDetails(userId)

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            val intent = Intent(this, AllTransactionsActivity::class.java)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchUserTransactions(userId: String) {
        Log.d("TransactionActivity", "fetchUserTransactions() started")
        showLoading()

        val retrofit = RetrofitClient.getInstance(8082)
        val service = retrofit.create(ServiceTransactionManagement::class.java)
        val call = service.getUserTransactions(userId)

        call.enqueue(object : Callback<List<Transaction>> {
            override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                Log.d("TransactionActivity", "onResponse() called")
                hideLoading()

                if (response.isSuccessful) {
                    val transactions = response.body()
                    if (transactions != null) {
                        adapter.updateData(transactions)
                        Log.d("TransactionActivity", "Response: $response")
                    }
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                Log.e("TransactionActivity", "onFailure() called", t)
                hideLoading()
                showErrorDialog()
            }
        })
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }

    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Error")
            .setMessage("Error fetching transactions.")
            .setPositiveButton("Retry") { _, _ ->
                fetchUserTransactions(userId)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    private fun fetchUserDetails(userId: String) {
        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)
        val call = service.getUserDetails(userId)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        textViewUserName.text = "${user.first_name} ${user.last_name}"
                    }
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Log.e("CatalogItemActivity", "onFailure() called", t)
                hideLoading()
                showErrorDialog()
            }
        })
    }
}