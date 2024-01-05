package hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.accountManagement.activity_accountManagement.AllMerchantsActivity
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import hr.foi.techtitans.ttpay.catalogItemManagement.activity_catalogItemManagement.AllCatalogsActivity
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.products.activity_products.AllProductsActivity
import hr.foi.techtitans.ttpay.transactions.activity_transactions.AllTransactionsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String
    private lateinit var textViewUserName: TextView
    private lateinit var userId: String
    private lateinit var progressBarUserName: ProgressBar
    private lateinit var loggedInUser: LoggedInUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        Log.d("AdminHomeActivity", "User username: $userUsername")

        textViewUserName = findViewById(R.id.textViewUserName)
        progressBarUserName = findViewById(R.id.progressBarUserName)

        fetchUserId(userUsername)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
    }

    fun onAllMerchantsClick(view: View) {
        val intent = Intent(this, AllMerchantsActivity::class.java)
        intent.putExtra("username", userUsername)
        startActivity(intent)
        finish()
    }

    fun onAllTransactionsClick(view: View) {
        val intent = Intent(this, AllTransactionsActivity::class.java)
        intent.putExtra("username", userUsername)
        startActivity(intent)
        finish()
    }

    fun onAllProductsClick(view: View) {
        val intent = Intent(this, AllProductsActivity::class.java)
        intent.putExtra("username", userUsername)
        startActivity(intent)
        finish()
    }

    fun onCatalogItemClick(view: View) {
        val intent = Intent(this, AllCatalogsActivity::class.java)
        intent.putExtra("username", userUsername)
        startActivity(intent)
        finish()
    }

    private fun fetchUserId(username: String) {
        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)

        val call = service.getUsers()

        // Show the progress bar
        progressBarUserName.visibility = View.VISIBLE

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                // Hide the progress bar
                progressBarUserName.visibility = View.GONE

                if (response.isSuccessful) {
                    val users = response.body()
                    val user = users?.find { it.username == username }
                    if (user != null) {
                        userId = user.id!!
                        Log.d("AllCatalogsMerchant", "Fetched user ID: $userId")
                        fetchUserDetails(userId)
                    } else {
                        showErrorDialog()
                    }
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                // Hide the progress bar
                progressBarUserName.visibility = View.GONE

                showErrorDialog()
            }
        })
    }

    private fun fetchUserDetails(userId: String) {
        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)
        val call = service.getUserDetails(userId)

        // Show the progress bar
        progressBarUserName.visibility = View.VISIBLE

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                // Hide the progress bar
                progressBarUserName.visibility = View.GONE

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
                // Hide the progress bar
                progressBarUserName.visibility = View.GONE

                Log.e("CatalogItemActivity", "onFailure() called", t)
                showErrorDialog()
            }
        })
    }

    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Error")
            .setMessage("Error fetching data.")
            .setPositiveButton("Retry") { _, _ ->
                fetchUserDetails(userId)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}