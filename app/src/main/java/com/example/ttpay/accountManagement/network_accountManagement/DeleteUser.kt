package com.example.ttpay.accountManagement.network_accountManagement

import android.content.Context
import android.util.Log
import com.example.ttpay.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteUser {
    fun deleteUser(context: Context,userIdToDelete:String?){
        val retrofit = RetrofitClient.getInstance(8080)//za account_management
        val service = retrofit.create(ServiceAccountManagement::class.java)

        val call = service.deleteUser(userIdToDelete)

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