package hr.foi.techtitans.ttpay.catalogItemManagement.activity_catalogItemManagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import hr.foi.techtitans.ttpay.catalogItemManagement.network_catalogItemManagement.ServiceCatalogItemManagement
import hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement.Catalog
import hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement.MerchantCatalogAdapter
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.MerchantHomeActivity
import hr.foi.techtitans.ttpay.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.core.LoggedInUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllCatalogsMerchantActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var progressBar: ProgressBar

    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private lateinit var userId: String

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MerchantCatalogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_catalogs_merchant)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""
        Log.d("MerchantHomeActivity", "User username: $userUsername")

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        recyclerView = findViewById(R.id.recyclerView_all_catalogs_merchant)
        progressBar = findViewById(R.id.loadingProgressBar)

        adapter = MerchantCatalogAdapter(emptyList()) { catalog ->
            val intent = Intent(this, DetailedCatalogItemActivity::class.java)
            intent.putExtra("catalogId", catalog.id)
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
                        fetchUserCatalogs(userId)
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

    private fun fetchUserCatalogs(userId: String) {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceCatalogItemManagement::class.java)
        val call = service.getUserCatalogs(userId)

        call.enqueue(object : Callback<List<Catalog>> {
            override fun onResponse(call: Call<List<Catalog>>, response: Response<List<Catalog>>) {
                hideLoading()
                if (response.isSuccessful) {
                    val catalogs = response.body()
                    if (catalogs != null) {
                        // Filter catalogs by the disabled property (disable = false)
                        val activeCatalogs = catalogs.filter { !it.disabled }
                        adapter.updateData(activeCatalogs)
                    }
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<Catalog>>, t: Throwable) {
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
            .setMessage("Error fetching catalogs. Make sure that you have a catalog.")
            .setPositiveButton("Retry") { _, _ ->
                fetchUserId(userUsername)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}