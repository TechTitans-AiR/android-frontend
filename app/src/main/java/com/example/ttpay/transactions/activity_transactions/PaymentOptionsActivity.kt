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
import com.example.ttpay.R
import com.example.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import com.example.ttpay.navigationBar.model_navigationBar.NavigationHandler
import com.example.ttpay.accountManagement.model_accountManagement.User
import com.example.ttpay.network.RetrofitClient
import com.example.ttpay.transactions.model_transactions.NewTransaction
import com.example.ttpay.transactions.model_transactions.ShoppingCartItem
import com.example.ttpay.transactions.network_transactions.ServiceTransactionManagement
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
    private lateinit var edtDescription: EditText

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
        edtCashAmount.visibility = View.GONE
        edtDescription = findViewById(R.id.edt_description)
        edtDescription.visibility = View.GONE

        completePayment = findViewById(R.id.btn_complete_payment)
        completePayment.setOnClickListener {
            handleCompletePayment()
        }

        radioGroupPaymentOptions.setOnCheckedChangeListener { _, checkedId ->
            handlePaymentOptionChange(checkedId)
        }
    }

    private fun handlePaymentOptionChange(checkedId: Int) {
        when (checkedId) {
            R.id.radioCash -> {
                edtCashAmount.visibility = View.VISIBLE
                edtDescription.visibility = View.VISIBLE
            }
            R.id.radioBankCard -> {
                edtCashAmount.visibility = View.GONE
                edtDescription.visibility = View.VISIBLE
            }
        }
    }

    private fun handleCompletePayment() {
        if (radioGroupPaymentOptions.checkedRadioButtonId == R.id.radioCash) {
            val cashAmount = edtCashAmount.text.toString().toDoubleOrNull()
            if (cashAmount != null && cashAmount >= 0) {
                if (cashAmount < totalAmount) {
                    completePayment.isEnabled = false
                    showToast("Entered amount is insufficient.")
                    Log.d("PaymentOptionsActivity", "Entered amount is insufficient.")
                    return
                }
            } else {
                completePayment.isEnabled = false
                showToast("Please enter a valid cash amount.")
                Log.d("PaymentOptionsActivity", "Invalid cash amount entered.")
                return
            }
        }

        completePayment.isEnabled = true

        fetchUserId(userUsername) { merchantId ->
            val description = edtDescription.text.toString()
            val currency = "EUR"
            val newTransaction = NewTransaction(merchantId, description, totalAmount, currency)
            sendTransactionToBackend(newTransaction, getYourCashAmount())
        }
    }

    private fun getYourCashAmount(): Double {
        return edtCashAmount.text.toString().toDoubleOrNull() ?: 0.0
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
                        val merchantId = user.id.toString()
                        Log.d("PaymentOptionsActivity", "Fetched merchant ID: $merchantId")
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
        showToast("Error fetching data for $username")
        Log.d("PaymentOptionsActivity", "Error fetching data for $username")
    }

    private fun sendTransactionToBackend(newTransaction: NewTransaction, cashAmount: Double) {
        val retrofit = RetrofitClient.getInstance(8082)
        val service = retrofit.create(ServiceTransactionManagement::class.java)

        val call = service.createTransaction(newTransaction)

        call.enqueue(object : Callback<NewTransaction> {
            override fun onResponse(
                call: Call<NewTransaction>,
                response: Response<NewTransaction>
            ) {
                if (response.isSuccessful) {
                    val newTransactionResponse = response.body()
                    if (newTransactionResponse != null) {
                        handleTransactionCreationSuccess(newTransactionResponse, cashAmount)
                        Log.d("PaymentOptionsActivity", "Transaction created successfully.")
                    }
                } else {
                    Log.e("PaymentOptionsActivity", "Error creating transaction. Code: ${response.code()}")

                    val errorBody = response.errorBody()?.string()
                    Log.e("PaymentOptionsActivity", "Error Body: $errorBody")
                }
            }

            override fun onFailure(call: Call<NewTransaction>, t: Throwable) {
                Log.d("PaymentOptionsActivity", "Communication error with backend.")
            }
        })
    }

    private fun handleTransactionCreationSuccess(newTransaction: NewTransaction, cashAmount: Double) {
        Log.d("PaymentOptionsActivity", "Cash Amount: $cashAmount")
        Log.d("PaymentOptionsActivity", "Total Amount: $totalAmount")
        val differenceAmount = cashAmount - totalAmount

        val intent = Intent(this, TransactionCompletionActivity::class.java)
        intent.putExtra("newTransaction", newTransaction)
        intent.putExtra("shoppingCartItems", ArrayList(shoppingCartItems))
        intent.putExtra("totalAmount", totalAmount)
        intent.putExtra("username", userUsername)
        intent.putExtra("differenceAmount", differenceAmount)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}