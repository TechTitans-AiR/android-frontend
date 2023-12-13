package com.example.ttpay.catalogItemManagement.activity_catalogItemManagement.activity_createCatalogItem

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
import com.example.ttpay.R
import com.example.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import com.example.ttpay.model.AddedUserAdapter
import com.example.ttpay.model.Article
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.model.SelectServiceAdapter
import com.example.ttpay.model.SelectUserAdapter
import com.example.ttpay.model.Service
import com.example.ttpay.model.User
import com.example.ttpay.network.RetrofitClient
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
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
    private lateinit var selectUserAdapter:SelectUserAdapter

    //which user is selected
    /*private lateinit var recycleViewAddedUser: RecyclerView
    private lateinit var addedUserAdapter: AddedUserAdapter
    */

    private var listSelectedUsers= mutableListOf<User>()
    private var listArticles= mutableListOf<Article>()
    private var listServices= mutableListOf<Service>()

    private lateinit var userUsername: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_select_user)

        userUsername = intent.getStringExtra("username") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, userUsername)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        progressBar=findViewById(R.id.loadingProgressBar)

        //btn back
        imgBack = findViewById(R.id.back_back)
        imgBack.setOnClickListener {
            val intent = Intent(this, SelectServicesActivity::class.java)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
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

        //fecth mercants
        fetchMerchants()

        //initializing recycler view
        recyclerViewSelectUser = findViewById(R.id.recyclerView_select_users)

        //for adapter Select user
        recyclerViewSelectUser.layoutManager = LinearLayoutManager(this)
        selectUserAdapter = SelectUserAdapter(emptyList()) { user ->
            //Adding selected service to list
            listSelectedUsers.add(user)

            /*Updating the display of added articles
            Log.d("Add user:", user.first_name)
            addedUserAdapter.updateData(listSelectedUsers)
            */

            //Snackbar message
            showSnackbar("The user is added to the list of users.")
        }



        recyclerViewSelectUser.adapter=selectUserAdapter

        //go to next activity
        continueButton = findViewById(R.id.btn_continue_see_data)
        continueButton.setOnClickListener {
            val intent = Intent(this, CreateCatalogDataActivity::class.java)

            intent.putExtra("listArticles",ArrayList(listArticles))
            intent.putExtra("listServices", ArrayList(listServices))
            intent.putExtra("listUsers", ArrayList(listSelectedUsers))
            Log.d("userlist", "Service: $listSelectedUsers")

            intent.putExtra("username", userUsername)

            startActivity(intent)
            finish()
        }
    }
    private fun fetchMerchants() {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8080)//za account_management
        val service = retrofit.create(ServiceAccountManagement::class.java)
        val call = service.getUsers()

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                hideLoading()
                if (response.isSuccessful) {
                    val users = response.body() ?: emptyList()
                    Log.d("AllMerchantsActivity", "Users fetched successfully: $users")
                    //update data in adapter
                    selectUserAdapter.updateData(users)
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