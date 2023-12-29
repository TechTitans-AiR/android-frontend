package com.example.ttpay.catalogItemManagement.model_catalogItemManagement

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.R
import com.example.ttpay.catalogItemManagement.network_catalogItemManagement.ServiceCatalogItemManagement
import com.example.ttpay.network.RetrofitClient
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CatalogAdapter(
    private var catalogs: List<Catalog>,
    private val onItemClick: (Catalog) -> Unit,
) :
    RecyclerView.Adapter<CatalogAdapter.CatalogViewHolder>() {

    class CatalogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtViewCatalogName: TextView = itemView.findViewById(R.id.textViewCatalogName)
        val switchEnableDisable: Switch = itemView.findViewById(R.id.switchEnableDisable)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_catalog, parent, false)
        return CatalogViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        val catalog = catalogs[position]
        holder.txtViewCatalogName.text = catalog.name

        holder.switchEnableDisable.isChecked = !catalog.disabled

        holder.switchEnableDisable.setOnClickListener {
            val isEnabled = holder.switchEnableDisable.isChecked
            catalog.disabled = !isEnabled
            updateCatalogStatus(catalog.id, catalog.name, isEnabled, holder)
        }

        holder.itemView.setOnClickListener {
            onItemClick(catalog)
        }
    }

    private fun updateCatalogStatus(
        catalogId: String?,
        catalogName: String,
        isEnabled: Boolean,
        holder: CatalogViewHolder
    ) {
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceCatalogItemManagement::class.java)

        val call: Call<ResponseBody> = if (isEnabled && catalogId != null) {
            service.enableCatalog(catalogId)
        } else if (!isEnabled && catalogId != null) {
            service.disableCatalog(catalogId)
        } else {
            return
        }

        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    val statusMessage = if (isEnabled) {
                        "Catalog $catalogName is enabled."
                    } else {
                        "Catalog $catalogName is disabled."
                    }

                    Log.d("CatalogItemActivity", "Catalog status updated: $statusMessage")

                    val toastMessage = if (isEnabled) {
                        "Catalog $catalogName is enabled."
                    } else {
                        "Catalog $catalogName is disabled."
                    }

                    Toast.makeText(holder.itemView.context, toastMessage, Toast.LENGTH_SHORT).show()
                } else {
                    Log.e("CatalogItemActivity", "Failed to update catalog status: ${response.code()}")
                    showErrorToast(holder.itemView.context)
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Log.e("CatalogItemActivity", "Failed to update catalog status", t)
                showErrorToast(holder.itemView.context)
            }
        })
    }

    private fun showErrorToast(context: Context) {
        Toast.makeText(context, "Error updating catalog status", Toast.LENGTH_SHORT).show()
    }

    override fun getItemCount(): Int {
        return catalogs.size
    }

    fun updateData(newCatalogs: List<Catalog>) {
        catalogs = newCatalogs
        notifyDataSetChanged()
    }
}
