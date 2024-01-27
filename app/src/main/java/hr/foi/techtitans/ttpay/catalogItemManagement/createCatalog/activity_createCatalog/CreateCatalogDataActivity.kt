package hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.activity_createCatalog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog.CreateCatalog
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog.NewCatalog
import hr.foi.techtitans.ttpay.products.model_products.Article
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.products.model_products.Service
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.catalogItemManagement.activity_catalogItemManagement.AdminSectionForCatalogsActivity
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog.CollectedArticlesCatalogAdapter
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog.CollectedServicesCatalogAdapter
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog.CollectedUserCatalogAdapter
import hr.foi.techtitans.ttpay.core.LoggedInUser

@Suppress("DEPRECATION")
class CreateCatalogDataActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var createButton: Button
    private lateinit var imgBack: ImageView
    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser

    //Adapters
    private lateinit var articlesAdapter: CollectedArticlesCatalogAdapter
    private lateinit var servicesAdapter: CollectedServicesCatalogAdapter
    private lateinit var usersAdapter: CollectedUserCatalogAdapter

    //Lists of data for catalog
    private var listSelectedArticles= mutableListOf<Article>()
    private var listSelectedServices= mutableListOf<Service>()
    private var listSelectedUsers= mutableListOf<User>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_catalog_data)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        imgBack = findViewById(R.id.back_back)
        imgBack.setOnClickListener {
            val intent = Intent(this, SelectUserActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("CreateCatalogDataActivity - LoggedInUser",loggedInUser.toString())
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }

        //Get all lists from previous screens (articles, services, users)
        listSelectedArticles = intent.getSerializableExtra("listArticles") as? ArrayList<Article> ?: arrayListOf()
        listSelectedServices = intent.getSerializableExtra("listServices") as? ArrayList<Service> ?: arrayListOf()
        listSelectedUsers = intent.getSerializableExtra("listUsers") as? ArrayList<User> ?: arrayListOf()

        Log.d("CreateCatalogDataActivity, articles: ",listSelectedArticles.toString())
        Log.d("CreateCatalogDataActivity, services: ",listSelectedServices.toString())
        Log.d("CreateCatalogDataActivity, users: ",listSelectedUsers.toString())

        articlesAdapter= CollectedArticlesCatalogAdapter(listSelectedArticles)
        servicesAdapter= CollectedServicesCatalogAdapter(listSelectedServices)


        //Get catalog name
        val catalogName=findViewById<EditText>(R.id.editText_nameOfCatalog)
        usersAdapter= CollectedUserCatalogAdapter(listSelectedUsers)

        // Connect adapters with ConcatAdapter--> for more than one adapter shown in one RecycleView
        val concatAdapter = ConcatAdapter(articlesAdapter, servicesAdapter, usersAdapter)

        // Make ConcatAdapter adapter for RecycleView
        val recyclerView: RecyclerView = findViewById(R.id.recycleView_all_collected_data_catalog)
        recyclerView.adapter = concatAdapter

        // Layout manager for RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        createButton = findViewById(R.id.btn_create_catalog)
        createButton.setOnClickListener {

            //Get IDs from lists
            val selectedArticleIds = listSelectedArticles.map { it.id }
            val selectedServicesIds=listSelectedServices.map{it.id}
            val selectedUsersIds= listSelectedUsers.mapNotNull { it.id }

            //Take catalog name
            val catalogNameText = catalogName.text.toString()

            //Prepare data for endpoint
            var newCatalog = NewCatalog(
                catalogNameText,
                selectedArticleIds,
                selectedServicesIds,
                selectedUsersIds,
                disabled = false //disabled property set to false
            )

            // Handle the case when no items are selected
            if (selectedArticleIds.isEmpty()) {
                newCatalog.articles = listOf()
            }

            if (selectedServicesIds.isEmpty()) {
                newCatalog.services = listOf()
            }

            if (selectedUsersIds.isEmpty()) {
                newCatalog.users = listOf()
            }

            //Create new catalog
            val createNew= CreateCatalog()
            Log.d("NewCatalog Object: ", newCatalog.toString())
            createNew.createNewCatalog(loggedInUser,this,newCatalog)

            val intent = Intent(this, AdminSectionForCatalogsActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }
    }
}