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
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.UserAdapter
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.AdminHomeActivity
import hr.foi.techtitans.ttpay.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import hr.foi.techtitans.ttpay.products.model_products.ServiceAdapter
import hr.foi.techtitans.ttpay.transactions.model_transactions.Transaction
import hr.foi.techtitans.ttpay.transactions.model_transactions.TransactionAdapter
import hr.foi.techtitans.ttpay.transactions.network_transactions.ServiceTransactionManagement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllTransactionsActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private lateinit var transactionAdapter : TransactionAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_transactions)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""
        transactionAdapter = TransactionAdapter(emptyList(), loggedInUser)

        recyclerView = findViewById(R.id.recyclerView_all_transactions)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = transactionAdapter

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        progressBar = findViewById(R.id.loadingProgressBar)

        fetchTransactions()

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("AllTransationsActivity - LoggedInUser",loggedInUser.toString())
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }
    }

    fun onPlusTransactionIconClick(view: View) {
        val intent = Intent(this, CreateTransactionActivity::class.java)
        intent.putExtra("loggedInUser", loggedInUser)
        Log.d("onPlusTransactionIconClick - LoggedInUser",loggedInUser.toString())
        intent.putExtra("username", userUsername)
        startActivity(intent)
        finish()
    }

    private fun fetchTransactions() {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8082)
        val service = retrofit.create(ServiceTransactionManagement::class.java)
        val call = service.getTransactions()

        call.enqueue(object : Callback<List<Transaction>> {
            override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                Log.d("AllTransactionsActivity", "Response code: ${response.code()}")
                hideLoading()
                if (response.isSuccessful) {
                    val transactions = response.body() ?: emptyList()
                    Log.d("AllTransactionsActivity", "Transactions fetched successfully: $transactions")
                    transactionAdapter.updateData(transactions)
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
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
                fetchTransactions()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    fun onSearchTransactionIconClick(view: View) {}
}