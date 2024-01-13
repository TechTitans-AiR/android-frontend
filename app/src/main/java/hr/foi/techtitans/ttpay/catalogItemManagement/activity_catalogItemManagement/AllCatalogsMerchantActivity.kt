package hr.foi.techtitans.ttpay.catalogItemManagement.activity_catalogItemManagement

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Spinner
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
import hr.foi.techtitans.ttpay.products.model_products.Article
import hr.foi.techtitans.ttpay.products.model_products.Service
import hr.foi.techtitans.ttpay.products.network_products.ServiceProducts
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

    private lateinit var removeSearch: ImageView

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
        removeSearch = findViewById(R.id.img_delete_search_icon)


        adapter = MerchantCatalogAdapter(emptyList()) { catalog ->
            val intent = Intent(this, DetailedCatalogItemActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("catalogId", catalog.id)
            startActivity(intent)
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        fetchUserCatalogs(loggedInUser.userId)

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            val intent = Intent(this, MerchantHomeActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
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
                fetchUserCatalogs(loggedInUser.userId)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    fun onDeleteSearchIconMerchantClick(view: View) {}
    fun onSearchCatalogIconMerchantClick(view: View) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_search_catalogs_merchant, null)

        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val spinnerArticles = dialogView.findViewById<Spinner>(R.id.etArticle)
        val progressBarDialog1 = dialogView.findViewById<ProgressBar>(R.id.progressBarDialog1)
        val spinnerServices = dialogView.findViewById<Spinner>(R.id.etService)
        val progressBarDialog2 = dialogView.findViewById<ProgressBar>(R.id.progressBarDialog2)


        // Initialization values of elements
        etName.setText("")

        fetchArticlesForDialog(spinnerArticles, progressBarDialog1)
        fetchServicesForSpinner(spinnerServices, progressBarDialog2)

        val builder = AlertDialog.Builder(this)
            .setTitle("Search catalogs")
            .setView(dialogView)
            .setPositiveButton("Search") { dialog, _ ->

                // Implement the search logic here
                val name = etName.text.toString()
                val articleSpinner = spinnerArticles.selectedItem.toString()
                val serviceSpinner = spinnerServices.selectedItem.toString()

                Log.d("Input name: ", name)
                Log.d("Selected article - spinner: ", articleSpinner)
                Log.d("Selected service - spinner:: ", serviceSpinner)

                // Pass the dialog view to the function
                performSearchAndUpdateRecyclerView(
                    name,
                    articleSpinner,
                    serviceSpinner,
                    progressBarDialog2
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

    private fun performSearchAndUpdateRecyclerView(
        name: String,
        articleSpinner: String,
        serviceSpinner: String,
        progressBarDialog: Any
    ) {

    }

    private fun fetchServicesForSpinner(spinnerServices: Spinner, progressBarDialog: ProgressBar) {
        progressBarDialog.visibility = View.VISIBLE

        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.getServicesForMerchant(loggedInUser.token, loggedInUser.userId)

        call.enqueue(object : Callback<List<Service>> {
            override fun onResponse(call: Call<List<Service>>, response: Response<List<Service>>) {
                try {
                    progressBarDialog.visibility = View.GONE

                    if (response.isSuccessful) {
                        val services = response.body() ?: emptyList()
                        Log.d("AllCatalogsActivity", "Services fetched successfully: $services")
                        // Add empty string at the begging of the list

                        val serviceList = mutableListOf<String>("Services")
                        serviceList.addAll(services.map { "${it.serviceName}" })

                        val arrayAdapter = ArrayAdapter(
                            this@AllCatalogsMerchantActivity,
                            android.R.layout.simple_spinner_item,
                            serviceList.toTypedArray()
                        )
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerServices.adapter = arrayAdapter

                    } else {
                        showErrorDialog()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<Service>>, t: Throwable) {
                try {
                    progressBarDialog.visibility = View.GONE
                    showErrorDialog()
                } catch (e: Exception) {
                    e.printStackTrace()
                    showErrorDialog()
                }
            }
        })
    }

    private fun fetchArticlesForDialog(spinnerArticles: Spinner, progressBarDialog: ProgressBar) {
        progressBarDialog.visibility = View.VISIBLE

        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.getArticlesForMerchant(loggedInUser.token, loggedInUser.userId)

        call.enqueue(object : Callback<List<Article>> {
            override fun onResponse(call: Call<List<Article>>, response: Response<List<Article>>) {
                try {
                    progressBarDialog.visibility = View.GONE

                    if (response.isSuccessful) {
                        val articles = response.body() ?: emptyList()
                        Log.d("AllCatalogsActivity", "Articles fetched successfully: $articles")
                        // Add empty string at the begging of the list

                        val articleList = mutableListOf<String>("Articles")
                        articleList.addAll(articles.map { "${it.name}" })

                        val arrayAdapter = ArrayAdapter(
                            this@AllCatalogsMerchantActivity,
                            android.R.layout.simple_spinner_item,
                            articleList.toTypedArray()
                        )
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerArticles.adapter = arrayAdapter

                    } else {
                        showErrorDialog()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<Article>>, t: Throwable) {
                try {
                    progressBarDialog.visibility = View.GONE
                    Log.d("Failed - articles: ", call.toString() )
                    showErrorDialog()
                } catch (e: Exception) {
                    e.printStackTrace()
                    showErrorDialog()
                }
            }
        })
    }


}