package hr.foi.techtitans.ttpay.transactions.activity_transactions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.transactions.model_transactions.Transaction
import hr.foi.techtitans.ttpay.transactions.model_transactions.TransactionAdapter
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.MerchantHomeActivity
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.transactions.network_transactions.ServiceTransactionManagement
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import hr.foi.techtitans.ttpay.core.LoggedInUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllTransactionsMerchantActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var progressBar: ProgressBar
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var imgBack : ImageView
    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var removeSearch: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_transactions_merchant)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""
        Log.d("MerchantHomeActivity", "User username: $userUsername")

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        progressBar = findViewById(R.id.loadingProgressBar)
        removeSearch = findViewById(R.id.img_delete_search_icon)
        recyclerView = findViewById(R.id.recyclerView_all_transactions)
        imgBack = findViewById(R.id.back_button)

        adapter = TransactionAdapter(emptyList(), loggedInUser)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = adapter

        fetchUserTransactions(loggedInUser.userId)

        imgBack.setOnClickListener {
            val intent = Intent(this, MerchantHomeActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }
    }

    fun onPlusTransactionIconClick(view: View) {
        val intent = Intent(this, CreateTransactionActivity::class.java)
        intent.putExtra("loggedInUser", loggedInUser)
        Log.d("onPlusTransactionIconClick(merchant) - LoggedInUser",loggedInUser.toString())
        intent.putExtra("username", userUsername)
        startActivity(intent)
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
                fetchUserTransactions(loggedInUser.userId)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    fun onSearchTransactionIconClick(view: View) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_search, null)

        val etDescription = dialogView.findViewById<EditText>(R.id.etDialogDescription)
        val etDate = dialogView.findViewById<EditText>(R.id.etDialogDate)
        val spinnerMerchant = dialogView.findViewById<Spinner>(R.id.spinnerDialogMerchant)
        spinnerMerchant.visibility = View.GONE

        // Initialization values of elements
        etDescription.setText("")
        etDate.setText("")

        val builder = AlertDialog.Builder(this)
            .setTitle("Search")
            .setView(dialogView)
            .setPositiveButton("Search") { dialog, _ ->
                // Implement the search logic here
                val description = etDescription.text.toString()
                val date = etDate.text.toString()
                // Pass the dialog view to the function
                performSearchAndUpdateRecyclerView(
                    description,
                    date,
                )
                removeSearch.visibility = View.VISIBLE
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    fun onDeleteSearchIconClick(view: View) {
        fetchUserTransactions(loggedInUser.userId)
        removeSearch.visibility = View.GONE
    }

    private fun performSearchAndUpdateRecyclerView(
        description: String,
        date: String,
    ) {
        progressBar.visibility = View.VISIBLE

            val retrofit = RetrofitClient.getInstance(8082)
            val service = retrofit.create(ServiceTransactionManagement::class.java)

            val searchParams = mutableMapOf<String, String>().apply {
                if (description.isNotEmpty()) put("description", description)
                if (date.isNotEmpty()) put("createdAt", date)
            }

            // Added merchantId parameter to search only the merchant's transactions
            searchParams["merchantId"] = loggedInUser.userId

            val call = service.searchTransactions(searchParams)

            // Log the JSON being sent for search
            Log.d("AllTransactionsActivity", "Search JSON: ${Gson().toJson(searchParams)}")

            call.enqueue(object : Callback<List<Transaction>> {
                override fun onResponse(
                    call: Call<List<Transaction>>,
                    response: Response<List<Transaction>>
                ) {
                    progressBar.visibility = View.GONE
                    if (response.isSuccessful) {
                        val transactions = response.body() ?: emptyList()
                        Log.d("AllTransactionsActivity", "Search results: $transactions")
                        adapter.updateData(transactions)
                    } else {
                        showErrorDialog()
                    }
                }

                override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                    progressBar.visibility = View.GONE
                    showErrorDialog()
                }
            })
        }
    }