package com.example.ttpay.products.network_products

import android.util.Log
import com.example.ttpay.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteService {

    fun deleteService(itemIdToDelete: String?) {
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)

        val call = service.deleteService(itemIdToDelete)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Handle successful deletion
                    Log.d("Service Deletion", "Service with ID $itemIdToDelete deleted successfully")
                } else {
                    // Handle unsuccessful deletion
                    Log.e("Service Deletion", "Failed to delete service with ID $itemIdToDelete")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle failure
                Log.e("Service Deletion", "Failed to delete service with ID $itemIdToDelete: ${t.message}")
            }
        })
    }
}