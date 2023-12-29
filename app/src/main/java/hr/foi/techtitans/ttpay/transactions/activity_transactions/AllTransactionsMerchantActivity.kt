package hr.foi.techtitans.ttpay.transactions.activity_transactions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.transactions.model_transactions.Transaction
import hr.foi.techtitans.ttpay.transactions.model_transactions.TransactionAdapter
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.MerchantHomeActivity
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.transactions.network_transactions.ServiceTransactionManagement
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllTransactionsMerchantActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var progressBar: ProgressBar

    private lateinit var userUsername: String

    private lateinit var userId: String

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_transactions_merchant)

        userUsername = intent.getStringExtra("username") ?: ""
        Log.d("MerchantHomeActivity", "User username: $userUsername")

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, userUsername)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        progressBar = findViewById(R.id.loadingProgressBar)

        recyclerView = findViewById(R.id.recyclerView_all_transactions)

        adapter = TransactionAdapter(emptyList()) { transaction ->
            val intent = Intent(this, DetailedTransactionActivity::class.java)
            intent.putExtra("transactionId", transaction.id)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchUserId(userUsername)

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            val intent = Intent(this, MerchantHomeActivity::class.java)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }
    }

    fun onPlusTransactionIconClick(view: View) {
        val intent = Intent(this, CreateTransactionActivity::class.java)
        intent.putExtra("username", userUsername)
        startActivity(intent)
    }

    private fun fetchUserId(username: String) {
        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)

        val call = service.getUsers()

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val users = response.body()
                    val user = users?.find { it.username == username }
                    if (user != null) {
                        userId = user.id!!
                        Log.d("AllTransactionMerchantActivity", "Fetched user ID: $userId")
                        fetchUserTransactions(userId)
                    } else {
                        showErrorDialog()
                    }
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                showErrorDialog()
            }
        })
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
}