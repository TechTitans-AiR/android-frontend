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
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog.AddedUserAdapter
import hr.foi.techtitans.ttpay.products.model_products.Article
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog.SelectUserAdapter
import hr.foi.techtitans.ttpay.products.model_products.Service
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import hr.foi.techtitans.ttpay.catalogItemManagement.activity_catalogItemManagement.UpdateCatalogActivity
import hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog.AddedServiceAdapter
import hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement.Catalog
import hr.foi.techtitans.ttpay.core.LoggedInUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SelectUserActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var continueButton: Button
    private lateinit var imgBack: ImageView

    private lateinit var progressBar:ProgressBar

    //for selecting user
    private lateinit var recyclerViewSelectUser:RecyclerView
    private lateinit var selectUserAdapter: SelectUserAdapter

    private lateinit var recycleViewAddedUser: RecyclerView
    private lateinit var addedUserAdapter: AddedUserAdapter

    private var listSelectedUsers= mutableListOf<User>()
    private var listArticles= mutableListOf<Article>()
    private var listServices= mutableListOf<Service>()

    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser

    private var catalog: Catalog?= Catalog(null, "", "", "", "", null, null, false)

    private  var currentCatalog: Catalog?= Catalog(null, "", "", "", "", null, null, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user)

        //initializing recycler view
        recyclerViewSelectUser = findViewById(R.id.recyclerView_select_users)
        recycleViewAddedUser = findViewById(R.id.recyclerView_added_users)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        catalog=intent.getParcelableExtra("selectedCatalog")
        Log.d("SelectArticles - Selected catalog: ", catalog.toString())

        currentCatalog=catalog

        //for adapter Select user
        recyclerViewSelectUser.layoutManager = LinearLayoutManager(this)
        selectUserAdapter = SelectUserAdapter(emptyList()) { user ->
            //Adding selected service to list
            listSelectedUsers.add(user)
            addedUserAdapter.updateData(listSelectedUsers)

            //Snackbar message
            showSnackbar("The user is added to the list of users.")
        }
        recyclerViewSelectUser.adapter = selectUserAdapter

        recycleViewAddedUser.layoutManager = LinearLayoutManager(this)
        addedUserAdapter = AddedUserAdapter(emptyList()) { position ->
            // Remove selected user from the list
            listSelectedUsers.removeAt(position)
            addedUserAdapter.updateData(listSelectedUsers)

            //Snackbar message
            showSnackbar("The user is deleted from the list of users.")
        }
        recycleViewAddedUser.adapter = addedUserAdapter

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        progressBar=findViewById(R.id.loadingProgressBar)

        //btn back
        imgBack = findViewById(R.id.back_back)
        imgBack.setOnClickListener {

            if(catalog!=null){
                intent.putExtra("selectedCatalog", catalog)

            }else{
                val intent = Intent(this, SelectServicesActivity::class.java)
                intent.putExtra("loggedInUser", loggedInUser)
                intent.putExtra("username", userUsername)
                startActivity(intent)
                finish()
            }

        }

        //get the list of the added articles
        val selectedArticles: ArrayList<Article>? = intent.getSerializableExtra("selectedArticles") as? ArrayList<Article>
        //Print the list of articles in logcat
        selectedArticles?.let {
            for (article in it) {
                listArticles.add(article)
                Log.d("SelectUserActivity", "Article: $article")
            }
        }

        //get the list of the added services
        val selectedServices: ArrayList<Service>? = intent.getSerializableExtra("selectedServices") as? ArrayList<Service>
        //Print the list of articles in logcat
        selectedServices?.let {
            for (services in it) {
                listServices.add(services)
                Log.d("SelectUserActivity", "Service: $services")
            }
        }

        //fetch merchants
        fetchMerchants()

        //go to next activity
        continueButton = findViewById(R.id.btn_continue_see_data)
        continueButton.setOnClickListener {
            if(catalog!=null) {
                val intent = Intent(this, UpdateCatalogActivity::class.java)
                intent.putExtra("selectedCatalog", catalog)
                intent.putExtra("loggedInUser", loggedInUser)
                intent.putExtra("username", userUsername)

                intent.putExtra("listArticles",ArrayList(listArticles))
                intent.putExtra("listServices", ArrayList(listServices))
                intent.putExtra("listUsers", ArrayList(listSelectedUsers))
                Log.d("userlist", "Service: $listSelectedUsers")
                startActivity(intent)
                finish()
            }
            else{
                val intent = Intent(this, CreateCatalogDataActivity::class.java)

                if(catalog!=null){
                    intent.putExtra("selectedCatalog", catalog)
                }
                intent.putExtra("loggedInUser", loggedInUser)
                intent.putExtra("listArticles",ArrayList(listArticles))
                intent.putExtra("listServices", ArrayList(listServices))
                intent.putExtra("listUsers", ArrayList(listSelectedUsers))
                Log.d("userlist", "Service: $listSelectedUsers")

                intent.putExtra("username", userUsername)

                startActivity(intent)
                finish()
            }

        }
    }

    private fun initAddedUsersAdapter() {
        addedUserAdapter = AddedUserAdapter(listSelectedUsers) { position ->
            // Deleting the selected users from the list of users
            listSelectedUsers.removeAt(position)

            //Updating the display of added users
            addedUserAdapter.updateData(listSelectedUsers)

            //Snackbar message
            showSnackbar("The services is deleted from the list of services.")
        }
        recycleViewAddedUser.adapter = addedUserAdapter
    }


    private fun fetchMerchants() {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8080)//za account_management
        val service = retrofit.create(ServiceAccountManagement::class.java)
        val call = service.getUsers(loggedInUser.token)

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                hideLoading()
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    Log.d("AllMerchantsActivity", "Users fetched successfully: $users")

                    if(catalog!=null){
                        handleAllUsers(users)
                    }else{
                        //update data in adapter
                        selectUserAdapter.updateData(users)
                    }
                    Log.d("RecyclerView Size", recyclerViewSelectUser.adapter?.itemCount.toString())

                } else {
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                hideLoading()
                showErrorDialog()
            }
        })
    }

    private fun handleAllUsers(allUsers: List<User>) {
        val selectedUsers = allUsers.filter { user ->
            currentCatalog?.users?.contains(user.id.toString()) == true
        }
        Log.d("selectedUsers.filter",selectedUsers.toString())

        listSelectedUsers=selectedUsers.toMutableList()
        addedUserAdapter.updateData(selectedUsers)
        selectUserAdapter?.updateData(allUsers.filterNot{selectedUsers.contains(it)})

        if (recyclerViewSelectUser.adapter == null) {
            initAddedUsersAdapter()
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
                fetchMerchants()
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