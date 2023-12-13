package com.example.ttpay.transactions.activity_transactions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import androidx.appcompat.app.AlertDialog
import com.example.ttpay.R
import com.example.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import com.example.ttpay.model.NavigationHandler
import com.example.ttpay.model.User
import com.example.ttpay.network.RetrofitClient
import com.example.ttpay.sellingItems.network_sellingItems.ServiceTransaction_SellingItems
import com.example.ttpay.transactions.network_transactions.NewTransaction
import com.example.ttpay.transactions.network_transactions.ShoppingCartItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PaymentOptionsActivity : AppCompatActivity() {

    private lateinit var userUsername: String
    private lateinit var shoppingCartItems: MutableList<ShoppingCartItem>
    private var totalAmount: Double = 0.0
    private lateinit var navigationHandler: NavigationHandler
    private lateinit var completePayment: Button
    private lateinit var radioGroupPaymentOptions: RadioGroup
    private lateinit var edtCashAmount: EditText
    private lateinit var userId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_options)

        userUsername = intent.getStringExtra("username") ?: ""
        shoppingCartItems =
            intent.getSerializableExtra("shoppingCartItems") as MutableList<ShoppingCartItem>
        totalAmount = intent.getDoubleExtra("totalAmount", 0.0)

        Log.d("TransactionSummaryActivity", "onCreate: username: $userUsername")
        Log.d("TransactionSummaryActivity", "onCreate: shoppingCartItems: $shoppingCartItems")
        Log.d("TransactionSummaryActivity", "onCreate: totalAmount: $totalAmount")

        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, userUsername)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)
        bottomNavigationView.visibility = View.VISIBLE

        radioGroupPaymentOptions = findViewById(R.id.radioGroupPaymentOptions)
        edtCashAmount = findViewById(R.id.edt_cash_amount)

        completePayment = findViewById(R.id.btn_complete_payment)
        completePayment.setOnClickListener {
            handleCompletePayment()
        }
    }

    private fun handleCompletePayment() {
        // Dohvati merchantId
        fetchUserId(userUsername) { merchantId ->
            // Koristi merchantId
            val description = "Transaction"
            val currency = "EUR"

            // Kreiraj objekt NewTransaction
            val newTransaction = NewTransaction(merchantId, description, totalAmount, currency)

            // Pošalji transakciju na backend
            sendTransactionToBackend(newTransaction)
        }
    }

    private fun fetchUserId(username: String, callback: (String) -> Unit) {
        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)

        val call = service.getUsers()

        call.enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful) {
                    val users = response.body()
                    val user = users?.find { it.username == username }
                    if (user != null) {
                        val merchantId = user.id!!
                        Log.d("AllCatalogsMerchant", "Fetched merchant ID: $merchantId")

                        // Pozovi callback funkciju s dobivenim merchantId
                        callback.invoke(merchantId)
                    } else {
                        showErrorDialog(userUsername)
                    }
                } else {
                    showErrorDialog(userUsername)
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                showErrorDialog(userUsername)
            }
        })
    }
    private fun showErrorDialog(username: String) {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Error")
            .setMessage("Error fetching data.")
            .setPositiveButton("Retry") { _, _ ->
                fetchUserId(username) { merchantId ->
                    // Ovdje možete dodati dodatnu logiku ako je potrebno
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        val dialog = builder.create()
        dialog.show()
    }


    private fun sendTransactionToBackend(newTransaction: NewTransaction) {
        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceTransaction_SellingItems::class.java)

        val call = service.createTransaction(newTransaction)

        call.enqueue(object : Callback<NewTransaction> {
            override fun onResponse(
                call: Call<NewTransaction>,
                response: Response<NewTransaction>
            ) {
                if (response.isSuccessful) {
                    // Uspješno kreirana transakcija
                    val newTransactionResponse = response.body()
                    if (newTransactionResponse != null) {
                        handleTransactionCreationSuccess(newTransactionResponse)
                    }
                } else {
                    // Pogreška prilikom kreiranja transakcije
                    // Ovdje možete obraditi pogrešku
                }
            }

            override fun onFailure(call: Call<NewTransaction>, t: Throwable) {
                // Greška prilikom komunikacije s backendom
                // Ovdje možete obraditi grešku
            }
        })
    }

    private fun handleTransactionCreationSuccess(newTransaction: NewTransaction) {
        val intent = Intent(this, TransactionCompletionActivity::class.java)
        intent.putExtra("newTransaction", newTransaction)
        intent.putExtra("shoppingCartItems", ArrayList(shoppingCartItems))
        intent.putExtra("totalAmount", totalAmount)
        intent.putExtra("username", userUsername)
        startActivity(intent)
    }
}