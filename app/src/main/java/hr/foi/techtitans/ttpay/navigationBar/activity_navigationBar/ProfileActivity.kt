package hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import hr.foi.techtitans.ttpay.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var userUsername: String
    private lateinit var loggedInUser: LoggedInUser

    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextAddress: EditText
    private lateinit var editTextDateOfBirth: EditText
    private lateinit var txtViewUserRole: TextView
    private lateinit var txtViewDateCreated: TextView
    private lateinit var txtViewUsername: TextView
    private lateinit var txtViewPassword: TextView
    private lateinit var txtViewEmail: TextView
    private lateinit var txtViewStatus: TextView

    private lateinit var btnEditData: Button
    private var isEditMode = false

    // Original values
    private lateinit var originalFirstName: String
    private lateinit var originalLastName: String
    private lateinit var originalAddress: String
    private lateinit var originalPhone: String
    private lateinit var originalDateOfBirth: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        editTextFirstName = findViewById(R.id.editText_FirstName)
        editTextLastName = findViewById(R.id.editText_LastName)
        editTextPhone = findViewById(R.id.editText_phone)
        editTextAddress = findViewById(R.id.editText_address)
        editTextDateOfBirth = findViewById(R.id.editText_dateOfBirth)
        txtViewUserRole = findViewById(R.id.textView_userRole)
        txtViewDateCreated = findViewById(R.id.textView_dateCreated)
        txtViewUsername = findViewById(R.id.textView_username)
        txtViewPassword = findViewById(R.id.textView_password)
        txtViewEmail = findViewById(R.id.textView_email)
        txtViewStatus = findViewById(R.id.textView_status)

        userUsername = intent.getStringExtra("username") ?: ""
        loggedInUser = intent.getParcelableExtra("loggedInUser")!!

        getUserDetails(loggedInUser.userId)

        // BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        btnBack.setOnClickListener {
            val intent = Intent(this, AdminHomeActivity::class.java)
            intent.putExtra("username", userUsername)
            intent.putExtra("loggedInUser", loggedInUser)
            startActivity(intent)
            finish()
        }

        btnEditData = findViewById(R.id.btnEditData)
        btnEditData.setOnClickListener {
            toggleEditMode()
        }
    }

    private fun toggleEditMode() {
        isEditMode = !isEditMode

        if (isEditMode) {
            enableEditMode()
        } else {
            saveChanges()
            disableEditMode()
        }
    }

    private fun enableEditMode() {
        // Enable editing for specified fields
        editTextFirstName.isEnabled = true
        editTextLastName.isEnabled = true
        editTextPhone.isEnabled = true
        editTextAddress.isEnabled = true
        editTextDateOfBirth.isEnabled = true
        txtViewUserRole.visibility = View.GONE
        txtViewDateCreated.visibility = View.GONE
        txtViewUsername.visibility = View.GONE
        txtViewPassword.visibility = View.GONE
        txtViewEmail.visibility = View.GONE
        txtViewStatus.visibility = View.GONE

        // Change button text
        btnEditData.text = "Save Changes"

        // Log statement
        Log.d("ProfileActivity", "Entered enableEditMode")
    }

    private fun disableEditMode() {
        // Disable editing for specified fields
        editTextFirstName.isEnabled = false
        editTextLastName.isEnabled = false
        editTextPhone.isEnabled = false
        editTextAddress.isEnabled = false
        editTextDateOfBirth.isEnabled = false
        txtViewUserRole.visibility = View.VISIBLE
        txtViewDateCreated.visibility = View.VISIBLE
        txtViewUsername.visibility = View.VISIBLE
        txtViewPassword.visibility = View.VISIBLE
        txtViewEmail.visibility = View.VISIBLE
        txtViewStatus.visibility = View.VISIBLE

        // Change button text
        btnEditData.text = "Edit Data"

        // Log statement
        Log.d("ProfileActivity", "Entered disableEditMode")
    }

    private fun saveChanges() {
        // Log statement
        Log.d("ProfileActivity", "Entered saveChanges")

        Toast.makeText(this, "Changes Saved", Toast.LENGTH_SHORT).show()

        // Collect and send only updated fields to the server
        val updatedFields = collectUpdatedFields()

        // Check if there are any changes before making the API call
        if (updatedFields.isNotEmpty()) {
            updateUserProfile(loggedInUser.userId, updatedFields, loggedInUser.token)
        }
    }

    private fun getUserDetails(userId: String) {
        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)

        val call = service.getUserDetails(userId)
        call.enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                if (response.isSuccessful) {
                    val user = response.body()
                    // Set user data into text views
                    updateUserDetails(user)
                } else {
                    Toast.makeText(this@ProfileActivity, "Failed to retrieve user details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                Toast.makeText(this@ProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateUserDetails(user: User?) {
        if (user != null) {
            // Update text views with user data
            editTextFirstName.setText(user.first_name)
            editTextLastName.setText(user.last_name)
            txtViewUserRole.text = user.userRole?.name ?: ""
            editTextDateOfBirth.setText(user.date_of_birth ?: "")
            txtViewDateCreated.text = "Date Created: ${user.date_created ?: ""}"
            txtViewUsername.text = "Username: ${user.username}"
            txtViewPassword.text = "Password: ${user.password}"
            txtViewEmail.text = "Email: ${user.email}"
            editTextPhone.setText(user.phone ?: "")
            editTextAddress.setText(user.address ?: "")
            txtViewStatus.text = "Status: ${user.userStatus?.name ?: ""}"

            // Save original values for later comparison
            originalFirstName = user.first_name
            originalLastName = user.last_name
            originalAddress = user.address ?: ""
            originalPhone = user.phone ?: ""
            originalDateOfBirth = user.date_of_birth ?: ""
        } else {
            // If user is null, set values in text views to empty string and display the toast message
            editTextFirstName.text.clear()
            editTextLastName.text.clear()
            txtViewUserRole.text = ""
            editTextDateOfBirth.text.clear()
            txtViewDateCreated.text = ""
            txtViewUsername.text = ""
            txtViewPassword.text = ""
            txtViewEmail.text = ""
            editTextPhone.text.clear()
            editTextAddress.text.clear()
            txtViewStatus.text = ""
            Toast.makeText(this@ProfileActivity, "User details not available", Toast.LENGTH_SHORT).show()
        }
    }

    private fun collectUpdatedFields(): Map<String, String> {
        val fieldsMap = mutableMapOf<String, String>()

        if (isFieldChanged(editTextFirstName, originalFirstName)) {
            fieldsMap["first_name"] = editTextFirstName.text.toString()
        }
        if (isFieldChanged(editTextLastName, originalLastName)) {
            fieldsMap["last_name"] = editTextLastName.text.toString()
        }
        if (isFieldChanged(editTextPhone, originalPhone)) {
            fieldsMap["phone"] = editTextPhone.text.toString()
        }
        if (isFieldChanged(editTextAddress, originalAddress)) {
            fieldsMap["address"] = editTextAddress.text.toString()
        }
        if (isFieldChanged(editTextDateOfBirth, originalDateOfBirth)) {
            fieldsMap["date_of_birth"] = editTextDateOfBirth.text.toString()
        }

        return fieldsMap
    }

    private fun isFieldChanged(editText: EditText, originalValue: String?): Boolean {
        val currentValue = editText.text.toString()
        return currentValue != originalValue
    }

    private fun updateUserProfile(userId: String, updatedFields: Map<String, String>, token: String) {
        val retrofit = RetrofitClient.getInstance(8080)
        val service = retrofit.create(ServiceAccountManagement::class.java)

        val call = service.updateUserProfile(userId, "Bearer $token", updatedFields)
        Log.d("ProfileActivity", "User ID: $userId, Token: $token, Updated Fields: $updatedFields")

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                Log.d("ProfileActivity", "Response code: ${response.code()}")
                if (response.isSuccessful) {
                    Log.d("ProfileActivity", "Profile updated successfully")
                    Toast.makeText(this@ProfileActivity, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("ProfileActivity", "Failed to update profile. Response code: ${response.code()}")
                    Toast.makeText(this@ProfileActivity, "Failed to update profile", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("ProfileActivity", "Update profile failed: ${t.message}", t)
                Toast.makeText(this@ProfileActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}