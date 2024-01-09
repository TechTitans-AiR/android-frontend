package hr.foi.techtitans.ttpay.products.model_products

import android.util.Log
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import hr.foi.techtitans.ttpay.network.RetrofitClient
import hr.foi.techtitans.ttpay.products.network_products.ServiceProducts
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DeleteArticle {

    fun deleteArticle(loggedInUser: LoggedInUser, itemIdToDelete: String?) {
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceProducts::class.java)

        val call = service.deleteArticle(loggedInUser.token, itemIdToDelete)

        call.enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // Handle successful deletion
                    Log.d("Article Deletion", "Article with ID $itemIdToDelete deleted successfully")
                } else {
                    // Handle unsuccessful deletion
                    Log.e("Article Deletion", "Failed to delete article with ID $itemIdToDelete")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // Handle failure
                Log.e("Article Deletion", "Failed to delete article with ID $itemIdToDelete: ${t.message}")
            }
        })
    }
}