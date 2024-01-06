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
import hr.foi.techtitans.ttpay.products.model_products.ArticleAdapter
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.products.model_products.Service
import hr.foi.techtitans.ttpay.products.model_products.ServiceAdapter
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.UserAdapter
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.AdminHomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser

@Suppress("DEPRECATION")
class CreateCatalogDataActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var createButton: Button
    private lateinit var imgBack: ImageView
    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser


    //adapters
    private lateinit var articlesAdapter: ArticleAdapter
    private lateinit var servicesAdapter: ServiceAdapter
    private lateinit var usersAdapter: UserAdapter

    //lists of data for catalog
    private var listSelectedArticles= mutableListOf<Article>()
    private var listSelectedServices= mutableListOf<Service>()
    private var listSelectedUsers= mutableListOf<User>()

    // defining empty lambda activity onItemClick for UserAdapter
    private val onItemClick: (User) -> Unit = {  }

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

        //get all lists from previous screens
        listSelectedArticles = intent.getSerializableExtra("listArticles") as? ArrayList<Article> ?: arrayListOf()
        listSelectedServices = intent.getSerializableExtra("listServices") as? ArrayList<Service> ?: arrayListOf()
        listSelectedUsers = intent.getSerializableExtra("listUsers") as? ArrayList<User> ?: arrayListOf()

        Log.d("CreateCatalogDataActivity, articles: ",listSelectedArticles.toString())
        Log.d("CreateCatalogDataActivity, services: ",listSelectedServices.toString())
        Log.d("CreateCatalogDataActivity, users: ",listSelectedUsers.toString())

        articlesAdapter= ArticleAdapter(listSelectedArticles, loggedInUser)
        servicesAdapter= ServiceAdapter(listSelectedServices, loggedInUser)


        //get catalog name
        val catalogName=findViewById<EditText>(R.id.editText_nameOfCatalog)

        usersAdapter= UserAdapter(listSelectedUsers,onItemClick)

        // Connect adapters with ConcatAdapter--> for more than one adapter shown in one RecycleView
        val concatAdapter = ConcatAdapter(articlesAdapter, servicesAdapter, usersAdapter)

        // Make ConcatAdapter adapter for RecycleView
        val recyclerView: RecyclerView = findViewById(R.id.recycleView_all_collected_data_catalog)
        recyclerView.adapter = concatAdapter

        // Layout manager for RecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)

        createButton = findViewById(R.id.btn_create_catalog)
        createButton.setOnClickListener {
            //get just ids from lists
            val selectedArticleIds = listSelectedArticles.map { it.id }
            val selectedServicesIds=listSelectedServices.map{it.id}
            val selectedUsersIds= listSelectedUsers.mapNotNull { it.id }

            //take catalog name
            val catalogNameText = catalogName.text.toString()

            //prepare data for endpoint
            val newCatalog = NewCatalog(
                catalogNameText,
                selectedArticleIds,
                selectedServicesIds,
                selectedUsersIds,
                disabled = false //disabled property set to false
            )



            //Create new catalog
            val createNew= CreateCatalog()
            Log.d("NewCatalog Object: ", newCatalog.toString())
            createNew.createNewCatalog(this,newCatalog)
            val intent = Intent(this, AdminHomeActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }
    }
}