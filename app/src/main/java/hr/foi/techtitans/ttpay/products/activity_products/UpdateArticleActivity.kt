package hr.foi.techtitans.ttpay.products.activity_products

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler

class UpdateArticleActivity : AppCompatActivity() {

    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser
    private lateinit var navigationHandler: NavigationHandler

    // UI elements
    private lateinit var progressBar: ProgressBar
    private lateinit var itemNameEditText: EditText
    private lateinit var itemCategoryEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var currencyEditText: EditText
    private lateinit var quantityInStockEditText: EditText
    private lateinit var weightEditText: EditText
    private lateinit var materialEditText: EditText
    private lateinit var brandEditText: EditText
    private lateinit var btnCancel: Button
    private lateinit var btnEditData: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_article)

        // Initialize UI elements
        progressBar = findViewById(R.id.progressBar)
        itemNameEditText = findViewById(R.id.itemNameEditText)
        itemCategoryEditText = findViewById(R.id.itemCategoryEditText)
        descriptionEditText = findViewById(R.id.descriptionEditText)
        priceEditText = findViewById(R.id.priceEditText)
        currencyEditText = findViewById(R.id.currencyEditText)
        quantityInStockEditText = findViewById(R.id.quantityInStockEditText)
        weightEditText = findViewById(R.id.weightEditText)
        materialEditText = findViewById(R.id.materialEditText)
        brandEditText = findViewById(R.id.brandEditText)
        btnCancel = findViewById(R.id.btnCancel)
        btnEditData = findViewById(R.id.btnEditData)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        userUsername = intent.getStringExtra("username") ?: ""

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        btnBack.setOnClickListener {
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("UpdateArticleActivity - LoggedInUser",loggedInUser.toString())
            intent.putExtra("username", userUsername)
            finish()
        }
    }
}