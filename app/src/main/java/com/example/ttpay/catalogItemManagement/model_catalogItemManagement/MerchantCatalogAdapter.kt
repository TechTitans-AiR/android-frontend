package com.example.ttpay.catalogItemManagement.model_catalogItemManagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.R


class MerchantCatalogAdapter (
    private var catalogs: List<Catalog>,
    private val onItemClick: (Catalog) -> Unit
) :
    RecyclerView.Adapter<MerchantCatalogAdapter.CatalogViewHolder>() {

    class CatalogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtViewCatalogName: TextView = itemView.findViewById(R.id.textViewCatalogName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_merchant_catalog, parent, false)
        return CatalogViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        val catalog = catalogs[position]
        holder.txtViewCatalogName.text = catalog.name

        holder.itemView.setOnClickListener {
            onItemClick(catalog)
        }
    }

    override fun getItemCount(): Int {
        return catalogs.size
    }

    fun updateData(newCatalogs: List<Catalog>) {
        catalogs = newCatalogs
        notifyDataSetChanged()
    }
}