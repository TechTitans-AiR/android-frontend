package hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog

import android.content.Context
import android.util.Log
import android.widget.Toast
import hr.foi.techtitans.ttpay.catalogItemManagement.network_catalogItemManagement.ServiceCatalogItemManagement
import hr.foi.techtitans.ttpay.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CreateCatalog {
    fun createNewCatalog(context: Context, newCatalog: NewCatalog){
        val retrofit = RetrofitClient.getInstance(8081)//za catalog_item_management
        val service = retrofit.create(ServiceCatalogItemManagement::class.java)


        //call for method createUser
        val call= service.createNewCatalogForUser(newCatalog)

        call.enqueue(object : Callback<NewCatalog> {

            override fun onResponse(call: Call<NewCatalog>, response: Response<NewCatalog>){
                if(response.isSuccessful){ //successful response
                    val created: NewCatalog?= response.body()
                    Log.d("Response: ", response.raw().toString())
                    if (created != null) {
                        Log.d("CATALOG: ", "$created")
                        Toast.makeText(context, "Created catalog ${newCatalog.name}!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "ERROR: Catalog object is null!", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "ERROR: Catalog not created!", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NewCatalog>, t: Throwable){
                Log.e("NetworkError", "Error: ${t.message}") //show error message in Logcat
                Toast.makeText(context, "Network error occurred", Toast.LENGTH_SHORT).show()
            }

        })
    }
}