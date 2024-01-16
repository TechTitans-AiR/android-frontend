package hr.foi.techtitans.ttpay.catalogItemManagement.activity_catalogItemManagement

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement.Catalog
import hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement.CatalogAdapter
import hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement.MerchantCatalogAdapter
import hr.foi.techtitans.ttpay.catalogItemManagement.network_catalogItemManagement.ServiceCatalogItemManagement
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.transactions.model_transactions.TransactionAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllCatalogsAdminAsMerchantActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var progressBar: ProgressBar
    private lateinit var recyclerView: RecyclerView
    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private lateinit var catalogAdapter: MerchantCatalogAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_catalogs_admin_as_merchant)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""
        catalogAdapter = MerchantCatalogAdapter(emptyList(), loggedInUser)

        recyclerView = findViewById(R.id.recyclerView_all_catalogs_admin)
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.adapter = catalogAdapter

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        progressBar = findViewById(R.id.loadingProgressBar)

        fetchUserCatalogs(loggedInUser.userId)

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("AdminTransactionsAsMerchantActivity - LoggedInUser", loggedInUser.toString())
            intent.putExtra("username", userUsername)
            onBackPressed()
        }
    }

    private fun fetchUserCatalogs(userId: String) {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceCatalogItemManagement::class.java)
        val call = service.getUserCatalogs(loggedInUser.token, userId)

        call.enqueue(object : Callback<List<Catalog>> {
            override fun onResponse(call: Call<List<Catalog>>, response: Response<List<Catalog>>) {
                hideLoading()
                if (response.isSuccessful) {
                    val catalogs = response.body()
                    if (catalogs != null) {
                        // Filter catalogs by the disabled property (disable = false)
                        val activeCatalogs = catalogs.filter { !it.disabled }
                        catalogAdapter.updateData(activeCatalogs)
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
                fetchUserCatalogs(loggedInUser.userId)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
}