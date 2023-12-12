package com.example.ttpay.catalogItemManagement.activity_catalogItemManagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.navigationBar.activities.AdminHomeActivity
import com.example.ttpay.R
import com.example.ttpay.model.User
import com.example.ttpay.model.UserAdapter
import com.example.ttpay.network.RetrofitClient
import com.example.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import com.example.ttpay.catalogItemManagement.activity_catalogItemManagement.activity_createCatalogItem.CreateCatalogItemActivity
import com.example.ttpay.catalogItemManagement.network_catalogItemManagement.ServiceCatalogItemManagement
import com.example.ttpay.model.Catalog
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.products.network_products.ServiceProducts
import com.google.android.material.bottomnavigation.BottomNavigationView
import org.json.JSONArray
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailedCatalogItemActivity : AppCompatActivity() {
    private lateinit var catalogId: String
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var textViewCatalogName: TextView
    private lateinit var textViewArticles: TextView
    private lateinit var textViewServices: TextView
    private lateinit var textViewUsers: TextView
    private lateinit var textViewDateCreated: TextView
    private lateinit var textViewDateModified: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_catalog_item)

        // Initialize views
        loadingProgressBar = findViewById(R.id.loadingProgressBar)
        textViewCatalogName = findViewById(R.id.textView_catalogName)
        textViewArticles = findViewById(R.id.textView_articles)
        textViewServices = findViewById(R.id.textView_services)
        textViewUsers = findViewById(R.id.textView_users)
        textViewDateCreated = findViewById(R.id.textView_dateCreated)
        textViewDateModified = findViewById(R.id.textView_dateModified)

        // Retrieve catalogId from the intent
        catalogId = intent.getStringExtra("catalogId") ?: ""

        // Call the function to fetch and display catalog details
        fetchCatalogDetails()
    }

    private fun fetchCatalogDetails() {
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceCatalogItemManagement::class.java)
        val call = service.getCatalogDetails(catalogId)

        call.enqueue(object : Callback<Catalog> {
            override fun onResponse(call: Call<Catalog>, response: Response<Catalog>) {
                if (response.isSuccessful) {
                    val catalog = response.body()
                    if (catalog != null) {
                        // Update the UI with catalog details
                        updateUIWithCatalogDetails(catalog)
                    }
                } else {
                    // Handle error response
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<Catalog>, t: Throwable) {
                // Handle failure
                showErrorDialog()
            }
        })
    }

    private fun updateUIWithCatalogDetails(catalog: Catalog) {
        // Set catalog name
        textViewCatalogName.text = catalog.name

        // Set articles
        val articles = parseStringList(catalog.articles)?.joinToString(", ")
        textViewArticles.text = "Articles: $articles"

        // Set services
        val services = parseStringList(catalog.services)?.joinToString(", ")
        textViewServices.text = "Services: $services"

        // Set users
        val users = parseStringList(catalog.users)?.joinToString(", ")
        textViewUsers.text = "Users: $users"

        // Set date created and date modified
        textViewDateCreated.text = "Date Created: ${catalog.date_created}"
        textViewDateModified.text = "Date Modified: ${catalog.date_modified}"

        // Hide the loading progress bar
        loadingProgressBar.visibility = View.GONE
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



    private fun showErrorDialog() {
        // Implement your error dialog logic here
    }
}