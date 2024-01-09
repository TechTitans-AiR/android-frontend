package hr.foi.techtitans.ttpay.accountManagement.model_accountManagement

import android.content.Context
import android.util.Log
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import hr.foi.techtitans.ttpay.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteUser {
    fun deleteUser(loggedInUser: LoggedInUser, context: Context,userIdToDelete:String?){
        val retrofit = RetrofitClient.getInstance(8080)//za account_management
        val service = retrofit.create(ServiceAccountManagement::class.java)

        val call = service.deleteUser(loggedInUser.token, userIdToDelete)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Handle successful deletion
                    Log.d("User Deletion", "User with ID $userIdToDelete deleted successfully")
                } else {
                    // Handle unsuccessful deletion
                    Log.e("User Deletion", "Failed to delete user with ID $userIdToDelete")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle failure
                Log.e("User Deletion", "Failed to delete user with ID $userIdToDelete: ${t.message}")
            }
        })

    }
}