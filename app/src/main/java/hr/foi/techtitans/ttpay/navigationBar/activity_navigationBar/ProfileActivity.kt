package hr.foi.techtitans.ttpay.navigationBar.activity_navigationBar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.navigationBar.model_navigationBar.NavigationHandler
import com.google.android.material.bottomnavigation.BottomNavigationView
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileActivity : AppCompatActivity() {

    private lateinit var navigationHandler: NavigationHandler
    private lateinit var loggedInUser: LoggedInUser

    private lateinit var editTextFirstName: EditText
    private lateinit var editTextLastName: EditText
    private lateinit var editTextUserRole: EditText
    private lateinit var editTextDateOfBirth: EditText
    private lateinit var editTextDateCreated: EditText
    private lateinit var editTextDateModified: EditText
    private lateinit var editTextUsername: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPhone: EditText
    private lateinit var editTextAddress: EditText
    private lateinit var editTextStatus: EditText
    private lateinit var editTextPin: EditText

    private lateinit var btnEditData: Button
    private lateinit var btnCancelEdit: Button
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
        editTextUserRole = findViewById(R.id.editText_userRole)
        editTextDateCreated = findViewById(R.id.editText_dateCreated)
        editTextDateModified = findViewById(R.id.editText_dateModified)
        editTextUsername = findViewById(R.id.editText_username)
        editTextPassword = findViewById(R.id.editText_password)
        editTextEmail = findViewById(R.id.editText_email)
        editTextStatus = findViewById(R.id.editText_status)
        editTextPhone = findViewById(R.id.editText_phone)
        editTextAddress = findViewById(R.id.editText_address)
        editTextDateOfBirth = findViewById(R.id.editText_dateOfBirth)
        editTextPin = findViewById(R.id.editText_pin)
        btnCancelEdit = findViewById(R.id.btnCancelEdit)
        btnEditData = findViewById(R.id.btnEditData)

        loggedInUser = intent.getParcelableExtra("loggedInUser")!!

        getUserDetails(loggedInUser.userId)

        // BottomNavigationView
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
        navigationHandler = NavigationHandler(this, loggedInUser)
        navigationHandler.setupWithBottomNavigation(bottomNavigationView)

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        btnBack.setOnClickListener {
            when(loggedInUser.role){
                "admin"-> {
                    val intent = Intent(this, AdminHomeActivity::class.java)
                    intent.putExtra("loggedInUser", loggedInUser)
                    startActivity(intent)
                }
                "merchant"-> {
                    val intent = Intent(this, MerchantHomeActivity::class.java)
                    intent.putExtra("loggedInUser", loggedInUser)
                    startActivity(intent)
                }
            }

        }

        btnEditData.setOnClickListener {
            toggleEditMode()
        }

        btnCancelEdit.visibility = View.GONE
        btnCancelEdit.setOnClickListener {
            getUserDetails(loggedInUser.userId)
            disableEditMode()
            isEditMode = false
            btnCancelEdit.visibility = View.GONE
            Toast.makeText(this, "Profile editing is canceled.", Toast.LENGTH_SHORT).show()
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
        editTextFirstName.setBackgroundResource(R.drawable.red_border_item)

        editTextLastName.isEnabled = true
        editTextLastName.setBackgroundResource(R.drawable.red_border_item)

        editTextPhone.isEnabled = true
        editTextPhone.setBackgroundResource(R.drawable.red_border_item)

        editTextAddress.isEnabled = true
        editTextAddress.setBackgroundResource(R.drawable.red_border_item)

        editTextDateOfBirth.isEnabled = true
        editTextDateOfBirth.setBackgroundResource(R.drawable.red_border_item)

        // Change button text
        btnEditData.text = "Save Changes"

        btnCancelEdit.visibility = View.VISIBLE

        // Log statement
        Log.d("ProfileActivity", "Entered enableEditMode")
    }

    private fun disableEditMode() {
        // Disable editing for specified fields
        editTextFirstName.isEnabled = false
        editTextFirstName.setBackgroundResource(0)

        editTextLastName.isEnabled = false
        editTextLastName.setBackgroundResource(0)

        editTextPhone.isEnabled = false
        editTextPhone.setBackgroundResource(0)

        editTextAddress.isEnabled = false
        editTextAddress.setBackgroundResource(0)

        editTextDateOfBirth.isEnabled = false
        editTextDateOfBirth.setBackgroundResource(0)

        // Change button text
        btnEditData.text = "Edit Data"

        btnCancelEdit.visibility = View.GONE

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

        val call = service.getUserDetails(loggedInUser.token, userId)
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
            editTextUserRole.setText(user.userRole?.name ?: "")
            editTextDateOfBirth.setText(user.date_of_birth ?: "")
            editTextDateCreated.setText(user.date_created ?: "")
            editTextDateModified.setText(user.date_modified ?: "")
            editTextUsername.setText(user.username)
            editTextPassword.setText(user.password)
            editTextPin.setText(user.pin ?: "")
            editTextEmail.setText(user.email)
            editTextPhone.setText(user.phone ?: "")
            editTextAddress.setText(user.address ?: "")
            editTextStatus.setText(user.userStatus?.name ?: "")

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
            editTextUserRole.text.clear()
            editTextDateOfBirth.text.clear()
            editTextDateCreated.text.clear()
            editTextDateModified.text.clear()
            editTextUsername.text.clear()
            editTextPassword.text.clear()
            editTextPin.text.clear()
            editTextEmail.text.clear()
            editTextPhone.text.clear()
            editTextAddress.text.clear()
            editTextStatus.text.clear()
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