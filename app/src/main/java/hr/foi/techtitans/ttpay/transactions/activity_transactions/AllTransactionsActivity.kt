package hr.foi.techtitans.ttpay.transactions.activity_transactions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
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
import com.google.gson.Gson
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
    private lateinit var removeSearch : ImageView

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

        removeSearch = findViewById(R.id.img_delete_search_icon)

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

    fun onSearchTransactionIconClick(view: View) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_search, null)

        val etDescription = dialogView.findViewById<EditText>(R.id.etDialogDescription)
        val etDate = dialogView.findViewById<EditText>(R.id.etDialogDate)
        val spinnerMerchant = dialogView.findViewById<Spinner>(R.id.spinnerDialogMerchant)
        val progressBarMerchant = dialogView.findViewById<ProgressBar>(R.id.progressBarDialog)

        // Initialization values of elements
        etDescription.setText("")
        etDate.setText("")
        // Spinner initialization with progress bar
        fetchMerchantsForDialog(spinnerMerchant, progressBarMerchant)

        val builder = AlertDialog.Builder(this)
            .setTitle("Search")
            .setView(dialogView)
            .setPositiveButton("Search") { dialog, _ ->
                // Implement the search logic here
                val description = etDescription.text.toString()
                val date = etDate.text.toString()
                val selectedMerchant = spinnerMerchant.selectedItem.toString()
                // Pass the dialog view to the function
                performSearchAndUpdateRecyclerView(description, date, selectedMerchant, progressBarMerchant)
                removeSearch.visibility = View.VISIBLE
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = builder.create()
        alertDialog.show()
    }

    private fun fetchMerchantsForDialog(spinnerMerchant: Spinner, progressBar: ProgressBar) {
        progressBar.visibility = View.VISIBLE  // Show the progress bar

        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)
        val call = service.getUsers()

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                try {
                    progressBar.visibility = View.GONE  // Hide the progress bar

                    if (response.isSuccessful) {
                        val users = response.body() ?: emptyList()
                        Log.d("AllMerchantsActivity", "Users fetched successfully: $users")

                        val userNames = users.map { "${it.first_name} ${it.last_name}" }.toTypedArray()

                        val arrayAdapter = ArrayAdapter(
                            this@AllTransactionsActivity,
                            android.R.layout.simple_spinner_item,
                            userNames
                        )
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerMerchant.adapter = arrayAdapter

                    } else {
                        showErrorDialog()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                try {
                    progressBar.visibility = View.GONE  // Hide the progress bar
                    showErrorDialog()
                } catch (e: Exception) {
                    e.printStackTrace()
                    showErrorDialog()
                }
            }
        })
    }


    private fun performSearchAndUpdateRecyclerView(
        description: String,
        date: String,
        selectedMerchant: String,
        progressBarMerchant: ProgressBar
    ) {
        progressBarMerchant.visibility = View.VISIBLE  // Show the progress bar

        getUserIdFromName(selectedMerchant) { merchantId ->
            progressBarMerchant.visibility = View.GONE  // Hide the progress bar

            val retrofit = RetrofitClient.getInstance(8082)
            val service = retrofit.create(ServiceTransactionManagement::class.java)

            val searchParams = mutableMapOf<String, String>().apply {
                if (description.isNotEmpty()) put("description", description)
                if (date.isNotEmpty()) put("createdAt", date)
                if (merchantId.isNotEmpty()) put("merchantId", merchantId)
            }

            val call = service.searchTransactions(searchParams)

            // Log the JSON being sent for search
            Log.d("AllTransactionsActivity", "Search JSON: ${Gson().toJson(searchParams)}")

            call.enqueue(object : Callback<List<Transaction>> {
                override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                    if (response.isSuccessful) {
                        val transactions = response.body() ?: emptyList()
                        Log.d("AllTransactionsActivity", "Search results: $transactions")
                        transactionAdapter.updateData(transactions)
                    } else {
                        showErrorDialog()
                    }
                }

                override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                    showErrorDialog()
                }
            })
        }
    }


    private fun getUserIdFromName(userName: String, callback: (String) -> Unit) {
        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)

        val call = service.getUsers()

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                try {
                    if (response.isSuccessful) {
                        val users = response.body() ?: emptyList()
                        for (user in users) {
                            val fullName = "${user.first_name} ${user.last_name}"
                            if (fullName == userName) {
                                callback.invoke(user.id ?: "") // Assuming 'id' is the property that represents the user id
                                return
                            }
                        }
                    } else {
                        // Handle error
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    // Handle exception
                }

                // If user id is not found, return an empty string or handle it based on your requirements
                callback.invoke("")
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                // Handle failure
                callback.invoke("")
            }
        })
    }

    fun onDeleteSearchIconClick(view: View) {
        fetchTransactions()
        removeSearch.visibility = View.GONE
    }


}