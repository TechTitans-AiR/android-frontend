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
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.AdminHomeActivity
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.activity_createCatalog.CreateCatalogItemActivity
import hr.foi.techtitans.ttpay.catalogItemManagement.network_catalogItemManagement.ServiceCatalogItemManagement
import hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement.Catalog
import hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement.CatalogAdapter
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.products.model_products.Article
import hr.foi.techtitans.ttpay.products.model_products.Service
import hr.foi.techtitans.ttpay.products.network_products.ServiceProducts
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllCatalogsActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private lateinit var catalogAdapter: CatalogAdapter

    private lateinit var progressBar: ProgressBar
    private lateinit var removeSearch: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_catalogs)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""


        catalogAdapter = CatalogAdapter(emptyList(), loggedInUser)

        recyclerView = findViewById(R.id.recyclerView_all_catalogs)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = catalogAdapter

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        progressBar = findViewById(R.id.loadingProgressBar)
        removeSearch = findViewById(R.id.img_delete_search_icon)

        fetchCatalogs()

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            val intent = Intent(this, AdminSectionForCatalogsActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("AllCatalogActivity - LoggedInUser",loggedInUser.toString())
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }
    }

    fun onPlusCatalogIconClick(view: View) {
        val intent = Intent(this, CreateCatalogItemActivity::class.java)
        intent.putExtra("loggedInUser", loggedInUser)
        Log.d("onPlusCatalogIconClick - LoggedInUser",loggedInUser.toString())
        intent.putExtra("username", userUsername)
        startActivity(intent)
        finish()
    }

    private fun fetchCatalogs() {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceCatalogItemManagement::class.java)
        val call = service.getCatalogs(loggedInUser.token)

        call.enqueue(object : Callback<List<Catalog>> {
            override fun onResponse(call: Call<List<Catalog>>, response: Response<List<Catalog>>) {
                Log.d("AllCatalogsActivity", "Response code: ${response.code()}")
                hideLoading()
                if (response.isSuccessful) {
                    val catalogs = response.body() ?: emptyList()
                    Log.d("AllCatalogsActivity", "Catalogs fetched successfully: $catalogs")
                    catalogAdapter.updateData(catalogs)
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
            .setMessage("Error fetching catalogs.")
            .setPositiveButton("Retry") { _, _ ->
                fetchCatalogs()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }

    fun onSearchCatalogIconClick(view: View) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_search_catalogs, null)

        val etName = dialogView.findViewById<EditText>(R.id.etName)
        val spinnerArticles = dialogView.findViewById<Spinner>(R.id.etArticle)
        val progressBarDialog1 = dialogView.findViewById<ProgressBar>(R.id.progressBarDialog1)
        val spinnerServices = dialogView.findViewById<Spinner>(R.id.etService)
        val progressBarDialog2 = dialogView.findViewById<ProgressBar>(R.id.progressBarDialog2)
        val spinnerUsers = dialogView.findViewById<Spinner>(R.id.etUser)
        val progressBarDialog3 = dialogView.findViewById<ProgressBar>(R.id.progressBarDialog3)

        // Initialization values of elements
        etName.setText("")

        fetchArticlesForDialog(spinnerArticles, progressBarDialog1)
        fetchServicesForSpinner(spinnerServices, progressBarDialog2)
        fetchMerchantsForDialog(spinnerUsers, progressBarDialog3)

        val builder = AlertDialog.Builder(this)
            .setTitle("Search catalogs")
            .setView(dialogView)
            .setPositiveButton("Search") { dialog, _ ->

                // Implement the search logic here
                val name = etName.text.toString()
                val articleSpinner = spinnerArticles.selectedItem.toString()
                val serviceSpinner = spinnerServices.selectedItem.toString()
                val userSpinner= spinnerUsers.selectedItem.toString()

                Log.d( "Input name: ", name)
                Log.d( "Selected article - spinner: ", articleSpinner)
                Log.d( "Selected service - spinner:: ", serviceSpinner)
                Log.d( "Selected user - spinner:: ", userSpinner)

                // Pass the dialog view to the function
                performSearchAndUpdateRecyclerView(
                    name,
                    articleSpinner,
                    serviceSpinner,
                    userSpinner,
                    progressBarDialog3
                )
                removeSearch.visibility = View.VISIBLE
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        val alertDialog=builder.create()
        alertDialog.show()

       }


    private fun fetchMerchantsForDialog(spinnerMerchant: Spinner, progressBarDialog: ProgressBar) {
        progressBarDialog.visibility = View.VISIBLE

        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)
        val call = service.getUsers(loggedInUser.token)

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                try {
                    progressBarDialog.visibility = View.GONE

                    if (response.isSuccessful) {
                        val users = response.body() ?: emptyList()
                        Log.d("AllCatalogsActivity", "Users fetched successfully: $users")
                        // Add empty string at the begging of the list
                        val userNames = mutableListOf<String>("Merchants")
                        userNames.addAll(users.map { "${it.first_name} ${it.last_name}" })

                        val arrayAdapter = ArrayAdapter(
                            this@AllCatalogsActivity,
                            android.R.layout.simple_spinner_item,
                            userNames.toTypedArray()
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
                    progressBar.visibility = View.GONE
                    showErrorDialog()
                } catch (e: Exception) {
                    e.printStackTrace()
                    showErrorDialog()
                }
            }
        })
    }

    private fun fetchArticlesForDialog(spinnerArticle: Spinner, progressBarDialog: ProgressBar) {
        progressBarDialog.visibility = View.VISIBLE

        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.getArticles(loggedInUser.token)

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
                            this@AllCatalogsActivity,
                            android.R.layout.simple_spinner_item,
                            articleList.toTypedArray()
                        )
                        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                        spinnerArticle.adapter = arrayAdapter

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
                    showErrorDialog()
                } catch (e: Exception) {
                    e.printStackTrace()
                    showErrorDialog()
                }
            }
        })
    }

    private fun fetchServicesForSpinner(spinnerServices: Spinner, progressBarDialog: ProgressBar) {
        progressBarDialog.visibility = View.VISIBLE

        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)
        val call = service.getServices(loggedInUser.token)

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
                            this@AllCatalogsActivity,
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


    private fun getServiceIdFromName(serviceName: String, callback: (String) -> Unit) {
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)

        val call = service.getServices(loggedInUser.token)

        call.enqueue(object : Callback<List<Service>> {
            override fun onResponse(call: Call<List<Service>>, response: Response<List<Service>>) {
                try {
                    if (response.isSuccessful) {
                        val services = response.body() ?: emptyList()
                        for (service in services) {
                            val nameUser = service.serviceName
                            if (nameUser == serviceName) {
                                callback.invoke(
                                    service.id ?: ""
                                )
                                return
                            }
                        }
                    } else {
                        Log.e("AllCatalogsActivity", "Error response: ${response.code()}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    handleException(e)
                }
                // If  id is not found, return an empty string
                callback.invoke("")
            }

            override fun onFailure(call: Call<List<Service>>, t: Throwable) {
                handleFailure(t)
                callback.invoke("")
            }
        })
    }

    private fun getArticleIdFromName(articleName: String, callback: (String) -> Unit) {
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)

        val call = service.getArticles(loggedInUser.token)

        call.enqueue(object : Callback<List<Article>> {
            override fun onResponse(call: Call<List<Article>>, response: Response<List<Article>>) {
                try {
                    if (response.isSuccessful) {
                        val articles = response.body() ?: emptyList()
                        for (article in articles) {
                            val nameArticle = article.name
                            if (nameArticle == articleName) {
                                callback.invoke(
                                    article.id ?: ""
                                )
                                return
                            }
                        }
                    } else {
                        Log.e("AllCatalogsActivity", "Error response: ${response.code()}")
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    handleException(e)
                }
                // If id is not found, return an empty string
                callback.invoke("")
            }

            override fun onFailure(call: Call<List<Article>>, t: Throwable) {
                handleFailure(t)
                callback.invoke("")
            }
        })
    }

    private fun getUserIdFromName(userName: String, callback: (String) -> Unit) {
        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)

        val call = service.getUsers(loggedInUser.token)

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                try {
                    if (response.isSuccessful) {
                        val users = response.body() ?: emptyList()
                        for (user in users) {
                            val nameUser = "${user.first_name} ${user.last_name}"
                            if (nameUser == userName) {
                                callback.invoke(
                                    user.id ?: ""
                                )
                                return
                            }
                        }
                    } else {
                        handleErrorResponse(response)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    handleException(e)
                }
                // If user id is not found, return an empty string
                callback.invoke("")
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                handleFailure(t)
                callback.invoke("")
            }
        })
    }

    private fun handleFailure(t: Throwable) {
        Log.e("AllCatalogsActivity", "Failure: ${t.message}")
    }

    private fun handleException(e: Exception) {
        Log.e("AllCatalogsActivity", "Exception: ${e.message}")
    }

    private fun handleErrorResponse(response: Response<List<User>>) {
        Log.e("AllCatalogsActivity", "Error response: ${response.code()}")
    }
    private fun performSearchAndUpdateRecyclerView(name: String, article: String, service: String, userName: String, progressBarDialog:ProgressBar) {
        progressBarDialog.visibility = View.VISIBLE
        progressBar.visibility = View.VISIBLE

        var merchantId: String = ""
        var serviceId: String = ""
        var articleId: String = ""

        getUserIdFromName(userName) { fetchedMerchant ->
            if (userName == "Merchants") {
                merchantId = ""
            } else {
                merchantId = fetchedMerchant
            }

            getServiceIdFromName(service) { fetchedService ->
                if (service == "Services") {
                    serviceId = ""
                } else {
                    serviceId = fetchedService
                }

                getArticleIdFromName(article) { fetchedArticle ->
                    if (article == "Articles") {
                        articleId = ""
                    } else {
                        articleId = fetchedArticle
                    }

                    progressBarDialog.visibility = View.GONE


                    // Rest of your code for the API call
                    val retrofit = RetrofitClient.getInstance(8081)
                    val service = retrofit.create(ServiceCatalogItemManagement::class.java)

                    val searchParams = mutableMapOf<String, String>().apply {
                        if (name.isNotEmpty()) put("name", name)
                        if (merchantId.isNotEmpty()) put("user", merchantId)
                        if (serviceId.isNotEmpty()) put("service", serviceId)
                        if (articleId.isNotEmpty()) put("article", articleId)
                    }
                    Log.d( "Name: ", name)
                    Log.d( "merchantId: ", merchantId)
                    Log.d( "serviceId: ", serviceId)
                    Log.d( "articleId: ", articleId)

                    val call = service.searchCatalogs(searchParams)

                    // Log the JSON being sent for search
                    Log.d("AllCatalogsActivity", "Search JSON: ${Gson().toJson(searchParams)}")

                    call.enqueue(object : Callback<List<Catalog>> {
                        override fun onResponse(call: Call<List<Catalog>>, response: Response<List<Catalog>>) {
                            progressBar.visibility = View.GONE
                            if (response.isSuccessful) {
                                Log.d("response onResponse AllCatalogs:", response.body().toString())

                                val catalogs = response.body() ?: emptyList()
                                Log.d("AllCatalogsActivity", "Search results: $catalogs")
                                catalogAdapter.updateData(catalogs)

                            } else {
                                showErrorDialog()
                            }
                        }

                        override fun onFailure(call: Call<List<Catalog>>, t: Throwable) {
                            progressBarDialog.visibility = View.GONE
                            progressBar.visibility = View.GONE
                            showErrorDialog()
                        }
                    })
                }
            }
        }
    }
    fun onDeleteSearchIconClick(view: View) {
        fetchCatalogs()
        removeSearch.visibility=View.GONE
    }

    override fun onRestart() {
        super.onRestart()
        fetchCatalogs()
    }

}