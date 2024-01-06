package hr.foi.techtitans.ttpay.accountManagement.activity_accountManagement

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
import hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar.AdminHomeActivity
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.MerchantAdapter
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AllMerchantsActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var recyclerView: RecyclerView
    private lateinit var loggedInUser: LoggedInUser
    private lateinit var progressBar: ProgressBar
    private lateinit var userUsername: String
    private lateinit var adapter:MerchantAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_merchants)
        recyclerView = findViewById(R.id.recyclerView_all_merchants)
        recyclerView.layoutManager = LinearLayoutManager(this)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        Log.d("AllMerchantsActivity - LoggedInUser",loggedInUser.toString())

        adapter = MerchantAdapter(emptyList(),loggedInUser)
        // Set adapter on recyclerView
        recyclerView.adapter = adapter


        userUsername = intent.getStringExtra("username") ?: ""

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        progressBar = findViewById(R.id.loadingProgressBar)

        fetchMerchants()

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        btnBack.setOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java)
            intent.putExtra("loggedInUser",loggedInUser)
            intent.putExtra("username", userUsername)
            startActivity(intent)
            finish()
        }

    }

    fun onPlusIconClick(view: View) {
        val intent = Intent(this, CreateNewMerchantActivity::class.java)
        intent.putExtra("loggedInUser",loggedInUser)
        intent.putExtra("username", userUsername)
        startActivity(intent)
        finish()
    }

    // Fetching all users
    private fun fetchMerchants() {
        showLoading()
        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)
        val call = service.getUsers()

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                hideLoading()
                try {
                    if (response.isSuccessful) {
                        val users = response.body() ?: emptyList()
                        Log.d("AllMerchantsActivity", "Users fetched successfully: $users")
                        adapter.updateData(users)
                    } else {
                        showErrorDialog()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showErrorDialog()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                hideLoading()
                try {
                    showErrorDialog()
                } catch (e: Exception) {
                    e.printStackTrace()
                    showErrorDialog()
                }
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
}