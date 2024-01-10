package hr.foi.techtitans.ttpay.transactions.activity_transactions

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
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.transactions.model_transactions.TransactionAdapter
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.transactions.network_transactions.ServiceTransactionManagement
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AlertDialog
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import hr.foi.techtitans.ttpay.transactions.model_transactions.Transaction
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.core.LoggedInUser
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
    private lateinit var loggedInUser: LoggedInUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_item)

        userId = intent.getStringExtra("userId") ?: ""
        Log.d("TransactionItemActivity", "User id: $userId")

        transactionId = intent.getStringExtra("transactionId") ?: ""

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        recyclerView = findViewById(R.id.recyclerView_all_transactions)
        progressBar = findViewById(R.id.loadingProgressBar)

        adapter = TransactionAdapter(emptyList(), loggedInUser)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchUserTransactions(userId)

        textViewUserName = findViewById(R.id.textViewUserName)
        fetchUserDetails(userId)

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            val intent = Intent(this, AllTransactionsActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
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