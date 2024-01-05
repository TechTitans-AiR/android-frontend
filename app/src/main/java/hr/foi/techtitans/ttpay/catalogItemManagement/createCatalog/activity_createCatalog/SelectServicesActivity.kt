package hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.activity_createCatalog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog.AddedServiceAdapter
import hr.foi.techtitans.ttpay.products.model_products.Article
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog.SelectServiceAdapter
import hr.foi.techtitans.ttpay.products.model_products.Service
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.products.network_products.ServiceProducts
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectServicesActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var continueButton: Button
    private lateinit var imgBack: ImageView

    private lateinit var progressBar: ProgressBar

    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser


    //for selecting service
    private lateinit var recyclerViewSelectServices: RecyclerView
    private lateinit var selectServiceAdapter: SelectServiceAdapter

    //which service is selected
    private lateinit var addedServiceAdapter: AddedServiceAdapter
    private lateinit var recyclerViewAddedServices: RecyclerView

    private var listSelectedServices = mutableListOf<Service>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_services)

        recyclerViewSelectServices = findViewById(R.id.recyclerView_select_services)
        recyclerViewAddedServices = findViewById(R.id.recyclerView_added_services)

        //nav
        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        progressBar = findViewById(R.id.loadingProgressBar)

        //btn back
        imgBack = findViewById(R.id.back_back)
        imgBack.setOnClickListener {
            val intent = Intent(this, SelectArticlesActivity::class.java)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }

        //get the list of the added articles
        val selectedArticles: ArrayList<Article>? = intent.getSerializableExtra("selected_articles") as? ArrayList<Article>
        //Print the list of articles in logcat
        selectedArticles?.let {
            for (article in it) {
                Log.d("SelectServicesActivity", "Article: $article")
            }
        }

        //get all services
        fetchServices();

        //for adapter Select service
        recyclerViewSelectServices.layoutManager = LinearLayoutManager(this)
        selectServiceAdapter = SelectServiceAdapter(emptyList()) { service ->
            //Adding selected service to list
            listSelectedServices.add(service)

            //Updating the display of added articles
            addedServiceAdapter.updateData(listSelectedServices)

            //Snackbar message
            showSnackbar("The service is added to the list of services.")
        }

        //for adapter Added service
        recyclerViewAddedServices.layoutManager = LinearLayoutManager(this)
        addedServiceAdapter = AddedServiceAdapter(listSelectedServices) { position ->
            // Deleting the selected article from the list of articles
            listSelectedServices.removeAt(position)

            //Updating the display of added articles
            addedServiceAdapter.updateData(listSelectedServices)

            //Snackbar message
            showSnackbar("The service is deleted from the list of services.")
        }

        recyclerViewSelectServices.adapter=selectServiceAdapter
        recyclerViewAddedServices.adapter=addedServiceAdapter

        //continue to select user
        continueButton = findViewById(R.id.btn_continue_select_user)
        continueButton.setOnClickListener {
            val intent = Intent(this, SelectUserActivity::class.java)

            intent.putExtra("selectedServices", ArrayList(listSelectedServices))
            intent.putExtra("selectedArticles", ArrayList(selectedArticles))
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }
    }

    private fun showLoading() {
        progressBar.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        progressBar.visibility = View.GONE
    }
    private fun fetchServices() {
        showLoading()

        val retrofit= RetrofitClient.getInstance(8081)
        val service=retrofit.create(ServiceProducts::class.java)

        //call for method new catalog
        val call=service.getServices()

        call.enqueue(object : Callback<List<Service>> {
            override fun onResponse(call: Call<List<Service>>, response: Response<List<Service>>) {
                hideLoading()
                if (response.isSuccessful) {
                    val listService = response.body() ?: emptyList()
                    Log.d("Lista servisa: ", ArrayList(listService).toString())
                    selectServiceAdapter.updateData(listService)
                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<Service>>, t: Throwable) {
                hideLoading()
                showErrorDialog()
            }
        })
    }
    private fun showErrorDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Error")
            .setMessage("Error fetching data.")
            .setPositiveButton("Retry") { _, _ ->
                fetchServices()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }
    private fun showSnackbar(message: String) {
        val snackbar = Snackbar.make(
            findViewById(android.R.id.content),
            message,
            Snackbar.LENGTH_SHORT
        )
        snackbar.show()
    }
}