package hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import hr.foi.techtitans.ttpay.catalogItemManagement.activity_catalogItemManagement.AllCatalogsMerchantActivity
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.transactions.activity_transactions.AllTransactionsMerchantActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.core.LoggedInUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MerchantHomeActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var textViewUserName: TextView
    private lateinit var progressBarUserName: ProgressBar
    private lateinit var loggedInUser: LoggedInUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_merchant_home)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!

        Log.d("MerchantHomeActivity", "User username: ${loggedInUser.username}")

        textViewUserName = findViewById(R.id.textViewUserName)
        progressBarUserName = findViewById(R.id.progressBarUserName)

        fetchUserDetails(loggedInUser.userId)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
    }

    fun onAllTransactionsClick(view: View) {
        val intent = Intent(this, AllTransactionsMerchantActivity::class.java)
        intent.putExtra("loggedInUser", loggedInUser)
        Log.d("onAllTransactionsClick(merchant) - LoggedInUser",loggedInUser.toString())
        startActivity(intent)
        finish()
    }

    fun onCatalogItemClick(view: View) {
        val intent = Intent(this, AllCatalogsMerchantActivity::class.java)
        intent.putExtra("loggedInUser", loggedInUser)
        Log.d("onCatalogItemClick(merchant) - LoggedInUser",loggedInUser.toString())
        startActivity(intent)
        finish()
    }

    private fun fetchUserDetails(userId: String) {
        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)
        val call = service.getUserDetails(loggedInUser.token, userId)

        // Show the progress bar
        progressBarUserName.visibility = View.VISIBLE

        call.enqueue(object : Callback<User> {
            @SuppressLint("SetTextI18n")
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
                fetchUserDetails(loggedInUser.userId)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}