package com.example.ttpay.navigationBar.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.ttpay.R
import com.example.ttpay.accountManagement.activity_accountManagement.AllMerchantsActivity
import com.example.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import com.example.ttpay.catalogItemManagement.activity_catalogItemManagement.AllCatalogsActivity
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.model.User
import com.example.ttpay.network.RetrofitClient
import com.example.ttpay.products.activity_products.AllProductsActivity
import com.example.ttpay.transactions.activity_transactions.AllTransactionsActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminHomeActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String
    private lateinit var textViewUserName: TextView
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_home)

        userUsername = intent.getStringExtra("username") ?: ""

        Log.d("AdminHomeActivity", "User username: $userUsername")

        textViewUserName = findViewById(R.id.textViewUserName)
        fetchUserId(userUsername)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, userUsername)
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

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
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
                showErrorDialog()
            }
        })
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