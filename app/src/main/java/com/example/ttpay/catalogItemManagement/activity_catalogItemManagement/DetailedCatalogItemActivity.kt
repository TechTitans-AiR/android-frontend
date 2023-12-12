package com.example.ttpay.catalogItemManagement.activity_catalogItemManagement

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ttpay.R
import com.example.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import com.example.ttpay.network.RetrofitClient
import com.example.ttpay.catalogItemManagement.network_catalogItemManagement.ServiceCatalogItemManagement
import com.example.ttpay.model.Article
import com.example.ttpay.model.Catalog
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.model.User
import com.example.ttpay.navigationBar.activities.AdminHomeActivity
import com.example.ttpay.products.network_products.ServiceProducts
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailedCatalogItemActivity : AppCompatActivity() {
    private lateinit var catalogId: String
    private lateinit var progressBar: ProgressBar
    private lateinit var textViewCatalogName: TextView
    private lateinit var textViewArticles: TextView
    private lateinit var textViewServices: TextView
    private lateinit var textViewUsers: TextView
    private lateinit var textViewDateCreated: TextView
    private lateinit var textViewDateModified: TextView
    private lateinit var navigationHandler: NavigationHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_catalog_item)

        // Initialize views
        progressBar = findViewById(R.id.loadingProgressBar)
        textViewCatalogName = findViewById(R.id.textView_catalogName)
        textViewArticles = findViewById(R.id.textView_articles)
        textViewServices = findViewById(R.id.textView_services)
        textViewUsers = findViewById(R.id.textView_users)
        textViewDateCreated = findViewById(R.id.textView_dateCreated)
        textViewDateModified = findViewById(R.id.textView_dateModified)

        // Retrieve catalogId from the intent
        catalogId = intent.getStringExtra("catalogId") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        // Call the function to fetch and display catalog details
        fetchCatalogDetails()

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchCatalogDetails() {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceCatalogItemManagement::class.java)
        val call = service.getCatalogDetails(catalogId)

        call.enqueue(object : Callback<Catalog> {
            override fun onResponse(call: Call<Catalog>, response: Response<Catalog>) {
                hideLoading()
                if (response.isSuccessful) {
                    val catalog = response.body()
                    if (catalog != null) {
                        // Update the UI with catalog details
                        updateUIWithCatalogDetails(catalog)
                    }
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<Catalog>, t: Throwable) {
                hideLoading()
                showErrorDialog()
            }
        })
    }

    private fun updateUIWithCatalogDetails(catalog: Catalog) {
        // Set catalog name
        textViewCatalogName.text = catalog.name

        // Set articles
        // in future when the endpoint will be implemented add retrieve name not id
        val articles = parseStringList(catalog.articles)?.joinToString(", ")
        textViewArticles.text = "Articles: $articles"

        // Set services
        // in future when the endpoint will be implemented retrieve name not id
        val services = parseStringList(catalog.services)?.joinToString(", ")
        textViewServices.text = "Services: $services"


        // Fetch and set user names
        fetchAndSetUserNames(parseStringList(catalog.users))

        // Set date created and date modified
        textViewDateCreated.text = "Date Created: ${catalog.date_created}"
        textViewDateModified.text = "Date Modified: ${catalog.date_modified}"
    }

    // Function to fetch user names and update the UI
    private fun fetchAndSetUserNames(userIds: List<String>?) {
        if (userIds.isNullOrEmpty()) {
            textViewUsers.text = "Users: "
            return
        }

        val userNames = mutableListOf<String>()
        for (userId in userIds) {
            fetchUserDetails(userId) { user ->
                userNames.add("${user.first_name} ${user.last_name}")
                if (userNames.size == userIds.size) {
                    // All user names fetched, update the UI
                    textViewUsers.text = "Users: ${userNames.joinToString(", ")}"
                }
            }
        }
    }

    // Function to fetch detailed information for a user
    private fun fetchUserDetails(userId: String, callback: (User) -> Unit) {
        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)
        val call = service.getUserDetails(userId)

        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    if (user != null) {
                        callback.invoke(user)
                    }
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                // Handle failure
            }
        })
    }

    // Function to parse a JSON array represented as a string into a list of strings
    private fun parseStringList(jsonArrayString: String?): List<String>? {
        return try {
            val jsonArray = JSONArray(jsonArrayString)
            List(jsonArray.length()) { jsonArray.getString(it) }
        } catch (e: JSONException) {
            null
        }
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
            .setMessage("Error fetching data.")
            .setPositiveButton("Retry") { _, _ ->
                fetchCatalogDetails()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}