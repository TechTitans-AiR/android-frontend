package com.example.ttpay.model

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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class CatalogAdapter(
    private var catalogs: List<Catalog>,
    private val onItemClick: (Catalog) -> Unit
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

        // Ako je catalog.disabled true, to znači da treba biti isključen Switch
        // pa ga postavljamo na false
        holder.switchEnableDisable.isChecked = !catalog.disabled

        holder.switchEnableDisable.setOnClickListener {
            // Ažuriranje stanja kataloga kada se klikne Switch
            val isEnabled = holder.switchEnableDisable.isChecked
            catalog.disabled = !isEnabled
            updateCatalogStatus(catalog.id, isEnabled, holder)
        }

        holder.itemView.setOnClickListener {
            onItemClick(catalog)
        }
    }

    private fun updateCatalogStatus(
        catalogId: String?,
        isEnabled: Boolean,
        holder: CatalogViewHolder
    ) {
        val retrofit = RetrofitClient.getInstance(8081)
        val service = retrofit.create(ServiceCatalogItemManagement::class.java)

        // Odaberi odgovarajući Retrofit poziv na osnovu stanja isEnabled
        val call: Call<Catalog> = if (isEnabled) {
            service.enableCatalog(catalogId!!)
        } else {
            service.disableCatalog(catalogId!!)
        }

        call.enqueue(object : Callback<Catalog> {
            override fun onResponse(call: Call<Catalog>, response: Response<Catalog>) {
                // Omogući Switch nakon završetka Retrofit poziva
                holder.switchEnableDisable.isEnabled = true

                if (response.isSuccessful) {
                    // Ažurirajte stanje kataloga na frontendu ili obavite dodatne akcije
                    val updatedCatalog = response.body()
                    if (updatedCatalog != null) {
                        // Ovde možete ažurirati UI ili obaviti dodatne akcije
                        Log.d("CatalogItemActivity", "Catalog status updated: $updatedCatalog")
                    }
                } else {
                    // Prikazivanje greške u slučaju neuspeha
                    Log.e(
                        "CatalogItemActivity",
                        "Failed to update catalog status: ${response.code()}"
                    )
                    showErrorToast(holder.itemView.context)
                }
            }

            override fun onFailure(call: Call<Catalog>, t: Throwable) {
                // Omogući Switch nakon završetka Retrofit poziva
                holder.switchEnableDisable.isEnabled = true

                // Prikazivanje greške u slučaju neuspeha
                Log.e("CatalogItemActivity", "Failed to update catalog status", t)
                showErrorToast(holder.itemView.context)
            }
        })
    }

    private fun showErrorToast(context: Context) {
        // Prikaži Toast poruku u slučaju greške
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
