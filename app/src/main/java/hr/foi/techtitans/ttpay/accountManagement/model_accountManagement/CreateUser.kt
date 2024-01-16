package hr.foi.techtitans.ttpay.accountManagement.model_accountManagement

import android.content.Context
import android.util.Log
import android.widget.Toast
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.network.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateUser {

    fun createNewUser(loggedInUser: LoggedInUser, context: Context, newUser: newUser){
        val retrofit = RetrofitClient.getInstance(8080)//za account_management
        val service = retrofit.create(ServiceAccountManagement::class.java)


        //call for method createUser
        val call= service.createNewUser(loggedInUser.token, newUser)

        call.enqueue(object : Callback<ResponseBody> {

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                Log.d("Response create user: ", response.toString())

                if (response.isSuccessful) {
                    val responseBodyData: ResponseBody? = response.body()

                    if (responseBodyData != null) {
                        // Process the response data here
                        Log.d("Userrrr: ", "$responseBodyData")
                        Toast.makeText(context, "Created user!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "ERROR: Response body is null!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Handle error response
                    Toast.makeText(context, "ERROR: User not created!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable){
                Log.e("NetworkError", "Error: ${t.message}") //show error message in Logcat
                Toast.makeText(context, "Network error occurred", Toast.LENGTH_SHORT).show()
            }

        })
    }
}