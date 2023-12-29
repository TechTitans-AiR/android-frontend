package hr.foi.techtitans.ttpay.accountManagement.model_accountManagement

import android.content.Context
import android.util.Log
import android.widget.Toast
import hr.foi.techtitans.ttpay.accountManagement.network_accountManagement.ServiceAccountManagement
import hr.foi.techtitans.ttpay.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateUser {

    fun createNewUser(context: Context, newUser: newUser){
        val retrofit = RetrofitClient.getInstance(8080)//za account_management
        val service = retrofit.create(ServiceAccountManagement::class.java)


        //call for method createUser
        val call= service.createNewUser(newUser)

        call.enqueue(object : Callback<newUser> {

            override fun onResponse(call: Call<newUser>, response: Response<newUser>){
                if(response.isSuccessful){ //successful response
                    val createdUser: newUser?= response.body()
                    if (createdUser != null) {
                        Log.d("Userrrr: ", "$createdUser")
                        Toast.makeText(context, "Created user!", Toast.LENGTH_SHORT).show()
                    } else {

                        Toast.makeText(context, "ERROR: User object is null!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "ERROR: User not created!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<newUser>, t: Throwable){
                Log.e("NetworkError", "Error: ${t.message}") //show error message in Logcat
                Toast.makeText(context, "Network error occurred", Toast.LENGTH_SHORT).show()
            }

        })
    }
}