package hr.foi.techtitans.ttpay.transactions.activity_transactions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import hr.foi.techtitans.ttpay.transactions.model_transactions.TransactionSummaryAdapter
import hr.foi.techtitans.ttpay.transactions.model_transactions.ShoppingCartItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.core.LoggedInUser

class TransactionSummaryActivity : AppCompatActivity() {

    private lateinit var loggedInUser: LoggedInUser
    private lateinit var shoppingCartItems: MutableList<ShoppingCartItem>
    private var totalAmount: Double = 0.0
    private lateinit var tv_total_amount_summary: TextView
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var btn_continue: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_summary)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!
        shoppingCartItems =
            intent.getSerializableExtra("shoppingCartItems") as MutableList<ShoppingCartItem>
        totalAmount = intent.getDoubleExtra("totalAmount", 0.0)

        Log.d("TransactionSummaryActivity", "onCreate: shoppingCartItems: $shoppingCartItems")
        Log.d("TransactionSummaryActivity", "onCreate: totalAmount: $totalAmount")

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        // Filtriraj stavke s količinom većom od 0
        val nonZeroQuantityItems = shoppingCartItems.filter { it.quantity > 0 }

        // set Recycler View for shoppingCartItems
        val recycler_transaction_details = findViewById<RecyclerView>(R.id.recycler_transaction_details)
        val layoutManager = LinearLayoutManager(this)
        recycler_transaction_details.layoutManager = layoutManager

        val adapter = TransactionSummaryAdapter(nonZeroQuantityItems)
        recycler_transaction_details.adapter = adapter

        // set text for total amount
        tv_total_amount_summary = findViewById(R.id.tv_total_amount_summary)
        tv_total_amount_summary.text = "Total Amount: €${"%.2f".format(totalAmount)}"

        btn_continue = findViewById(R.id.btn_continue_payment)
        btn_continue.setOnClickListener {
            val intent = Intent(this, PaymentOptionsActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("shoppingCartItems", ArrayList(shoppingCartItems))
            intent.putExtra("totalAmount", totalAmount)
            startActivity(intent)
        }

        val imgBack: ImageView = findViewById(R.id.back_button)
        imgBack.setOnClickListener {
            intent.putExtra("loggedInUser", loggedInUser)
            Log.d("AllTransationsActivity - LoggedInUser", loggedInUser.toString())
            onBackPressed()
        }
    }
}