package hr.foi.techtitans.ttpay.transactions.activity_transactions

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.products.model_products.Article
import hr.foi.techtitans.ttpay.products.network_products.ServiceProducts
import hr.foi.techtitans.ttpay.transactions.model_transactions.Transaction
import hr.foi.techtitans.ttpay.transactions.network_transactions.ServiceTransactionManagement
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Locale

class DetailedTransactionActivity : AppCompatActivity() {

    private lateinit var transactionId: String
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var bottomNavigationView : BottomNavigationView
    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser

    // UI elements
    private lateinit var imgBack : ImageView
    private lateinit var description : TextView
    private lateinit var merchant : TextView
    private lateinit var amount : TextView
    private lateinit var currency : TextView
    private lateinit var dateCreated : TextView
    private lateinit var dateUpdated : TextView
    private lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_transaction)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""
        transactionId = intent.getStringExtra("transactionId") ?: ""
        Log.d("DetailedTransactionActivity", "Transaction id: $transactionId")

        // initialize UI elements
        description = findViewById(R.id.textView_transaction_description)
        merchant = findViewById(R.id.textView_merchant)
        amount = findViewById(R.id.textView_amount)
        currency = findViewById(R.id.textView_currency)
        dateCreated = findViewById(R.id.textView_createdAt)
        dateUpdated = findViewById(R.id.textView_updatedAt)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        imgBack = findViewById(R.id.back_button)
        progressBar = findViewById(R.id.loadingProgressBar)

        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        fetchTransactionDetails(transactionId)

        imgBack.setOnClickListener {
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("username", userUsername)
            onBackPressed()
        }
    }

    private fun fetchTransactionDetails(transactionId: String?) {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8082)
        val service = retrofit.create(ServiceTransactionManagement::class.java)
        val call = service.getTransactionDetails(loggedInUser.token, transactionId.orEmpty())
        call.enqueue(object : Callback<Transaction> {
            override fun onResponse(call: Call<Transaction>, response: Response<Transaction>) {
                hideLoading()

                if (response.isSuccessful) {
                    val transaction = response.body()
                    if (transaction != null) {
                        fetchUserDetails(transaction.merchantId)
                        description.text = transaction.description
                        amount.text = "${transaction.amount}"
                        currency.text = transaction.currency
                        dateCreated.text = formatDate(transaction.createdAt)
                        dateUpdated.text = formatDate(transaction.updatedAt)
                    }
                } else {
                    showErrorDialog()
                }
            }
            override fun onFailure(call: Call<Transaction>, t: Throwable) {
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
            .setMessage("Error fetching transaction details.")
            .setPositiveButton("Retry") { _, _ ->
                val transactionId = intent.getStringExtra("transactionId")
                fetchTransactionDetails(transactionId)
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
        val call = service.getUserDetails(loggedInUser.token, userId)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        val userFullName = "${user.first_name} ${user.last_name}"
                        merchant.text = userFullName
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                val errorMessage = "Error fetching user details: ${t.message}"
                Toast.makeText(applicationContext, errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
    private fun formatDate(dateString: String?): String {
        return try {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.getDefault())

            val date = inputFormat.parse(dateString ?: "")
            outputFormat.format(date ?: "")
        } catch (e: Exception) {
            dateString ?: ""
        }
    }
}