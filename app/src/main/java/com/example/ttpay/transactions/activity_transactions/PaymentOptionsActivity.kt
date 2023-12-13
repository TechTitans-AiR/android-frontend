package com.example.ttpay.transactions.activity_transactions

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
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

        Log.d("PaymentOptionsActivity", "onCreate: username: $userUsername")
        Log.d("PaymentOptionsActivity", "onCreate: shoppingCartItems: $shoppingCartItems")
        Log.d("PaymentOptionsActivity", "onCreate: totalAmount: $totalAmount")

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

        // Postavite slušatelja promjene odabrane opcije plaćanja
        radioGroupPaymentOptions.setOnCheckedChangeListener { _, checkedId ->
            handlePaymentOptionChange(checkedId)
        }
    }

    private fun handlePaymentOptionChange(checkedId: Int) {
        when (checkedId) {
            R.id.radioCash -> {
                // Ako je odabrana opcija plaćanja gotovinom, prikaži polje za unos gotovine
                edtCashAmount.visibility = View.VISIBLE
            }
            R.id.radioBankCard -> {
                // Ako je odabrana opcija plaćanja karticom, sakrij polje za unos gotovine
                edtCashAmount.visibility = View.GONE
            }
        }
    }

    private fun handleCompletePayment() {
        // Ako je odabrana opcija plaćanja gotovinom, provjeri iznos gotovine
        if (radioGroupPaymentOptions.checkedRadioButtonId == R.id.radioCash) {
            val cashAmount = edtCashAmount.text.toString().toDoubleOrNull()
            if (cashAmount != null && cashAmount >= 0) {
                if (cashAmount < totalAmount) {
                    // Ako iznos nije dovoljan, onemogući gumb za dovršavanje plaćanja
                    completePayment.isEnabled = false
                    showToast("Entered amount is insufficient.")
                    return
                }
            } else {
                // Ako unos nije valjan, onemogući gumb za dovršavanje plaćanja
                completePayment.isEnabled = false
                showToast("Please enter a valid cash amount.")
                return
            }
        }

        // Ako su zadovoljeni uvjeti, omogući gumb za dovršavanje plaćanja i pokreni TransactionCompletionActivity
        completePayment.isEnabled = true

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
                        val merchantId = user.id.toString() // Pretvori u String
                        Log.d("PaymentOptionsActivity", "Fetched merchant ID: $merchantId")

                        // Pozovi callback funkciju s dobivenim merchantId
                        callback.invoke(merchantId)
                    } else {
                        showErrorDialog(username)
                    }
                } else {
                    showErrorDialog(username)
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                showErrorDialog(username)
            }
        })
    }

    private fun showErrorDialog(username: String) {
        // Implementirajte logiku prikaza dijaloga za pogrešku
        showToast("Error fetching data for $username")
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

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}