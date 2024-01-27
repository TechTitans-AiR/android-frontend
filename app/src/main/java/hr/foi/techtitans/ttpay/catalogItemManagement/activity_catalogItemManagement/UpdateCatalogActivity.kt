package hr.foi.techtitans.ttpay.catalogItemManagement.activity_catalogItemManagement

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.activity_createCatalog.SelectUserActivity
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog.SelectUserAdapter
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog.UpdateCatalog
import hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement.Catalog
import hr.foi.techtitans.ttpay.catalogItemManagement.network_catalogItemManagement.ServiceCatalogItemManagement
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.products.model_products.Article
import hr.foi.techtitans.ttpay.products.model_products.ArticleAdapter
import hr.foi.techtitans.ttpay.products.model_products.Service
import hr.foi.techtitans.ttpay.products.model_products.ServiceAdapter
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class UpdateCatalogActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var btnSave: Button
    private lateinit var imgBack: ImageView
    private lateinit var loggedInUser: LoggedInUser

    //Adapters
    private lateinit var articlesAdapter: ArticleAdapter
    private lateinit var servicesAdapter: ServiceAdapter
    private lateinit var usersAdapter: SelectUserAdapter

    //Lists of data for catalog
    private var listSelectedArticles= mutableListOf<Article>()
    private var listSelectedServices= mutableListOf<Service>()
    private var listSelectedUsers= mutableListOf<User>()

    // Defining empty lambda activity onItemClick for UserAdapter
    private val onItemClick: (User) -> Unit = {  }

    private var catalog: Catalog?= Catalog(null, "", "", "", "", null, null, false)
    private  var currentCatalog: Catalog?= Catalog(null, "", "", "", "", null, null, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_catalog)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!

        catalog=intent.getParcelableExtra("selectedCatalog")
        Log.d("SelectArticles - Selected catalog: ", catalog.toString())

        currentCatalog=catalog
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        imgBack = findViewById(R.id.back_back)
        imgBack.setOnClickListener {
            val intent = Intent(this, SelectUserActivity::class.java)
            if(catalog!=null){

            }
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("CreateCatalogDataActivity - LoggedInUser",loggedInUser.toString())
            startActivity(intent)
            finish()
        }
        //get all lists from previous screens
        listSelectedArticles = intent.getSerializableExtra("listArticles") as? ArrayList<Article> ?: arrayListOf()
        listSelectedServices = intent.getSerializableExtra("listServices") as? ArrayList<Service> ?: arrayListOf()
        listSelectedUsers = intent.getSerializableExtra("listUsers") as? ArrayList<User> ?: arrayListOf()



        Log.d("CreateCatalogDataActivity, articles: ",listSelectedArticles.toString())
        Log.d("CreateCatalogDataActivity, services: ",listSelectedServices.toString())
        Log.d("CreateCatalogDataActivity, users: ",listSelectedUsers.toString())

        articlesAdapter= ArticleAdapter(listSelectedArticles, loggedInUser, false)
        servicesAdapter= ServiceAdapter(listSelectedServices, loggedInUser, false)

        //get catalog name
        val catalogName=findViewById<EditText>(R.id.editText_nameOfCatalog)
        catalogName.hint=currentCatalog?.name.toString()

        usersAdapter= SelectUserAdapter(listSelectedUsers, false){}

        // Connect adapters with ConcatAdapter--> for more than one adapter shown in one RecycleView
        val concatAdapter = ConcatAdapter(articlesAdapter, servicesAdapter, usersAdapter)

        // Make ConcatAdapter adapter for RecycleView
        val recyclerView: RecyclerView = findViewById(R.id.recycleView_all_collected_data_catalog)
        recyclerView.adapter = concatAdapter

        // Layout manager for RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        btnSave = findViewById(R.id.btnSaveChanges)
        btnSave.setOnClickListener {

            //Get IDs from lists
            val selectedArticleIds = listSelectedArticles.map { it.id }
            val selectedServicesIds=listSelectedServices.map{it.id}
            val selectedUsersIds= listSelectedUsers.mapNotNull { it.id }

            //Take catalog name
            var catalogNameText = catalogName.text.toString()
            if(catalogNameText.isEmpty()){
                catalogNameText=currentCatalog?.name.toString()
            }

            //Prepare data for endpoint
            val catalog = UpdateCatalog(
                id = currentCatalog?.id,
                catalogNameText,
                selectedArticleIds,
                selectedServicesIds,
                selectedUsersIds,
                disabled = false //Disabled property set to false
            )

            updateCatalog(catalog)
            Log.d("updateCatalog Object: ", catalog.toString())

            val intent=Intent(this, DetailedCatalogItemActivity::class.java)
            intent.putExtra("catalogId", currentCatalog?.id)
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("updatedCatalog", currentCatalog?.id.toString())
            startActivity(intent)
        }
    }

    private fun updateCatalog(updatingCatalog: UpdateCatalog) {
        val retrofit = RetrofitClient.getInstance(8081)//za catalog_item_management
        val service = retrofit.create(ServiceCatalogItemManagement::class.java)

        val call = service.updateExistingCatalog(
            loggedInUser.token,
            updatingCatalog.id.toString(),
            updatingCatalog
        )

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(
                        this@UpdateCatalogActivity,
                        "Update complete!",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d("Catalog updated succesfully!", updatingCatalog.toString())
                } else {
                    Log.e("UpdateCatalogError", "Catalog update error. Code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@UpdateCatalogActivity, "Failed update!", Toast.LENGTH_LONG)
                    .show()
            }
        })
    }
}
