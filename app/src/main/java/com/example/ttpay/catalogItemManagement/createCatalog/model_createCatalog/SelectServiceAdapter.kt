package com.example.ttpay.catalogItemManagement.createCatalog.model_createCatalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.R
import com.example.ttpay.products.model_products.Service

class SelectServiceAdapter (
    private var services: List<Service>,
    private val onAddClick: (Service) -> Unit
) : RecyclerView.Adapter<SelectServiceAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtViewName: TextView = itemView.findViewById(R.id.textView_service_name)
        val txtViewDuration: TextView = itemView.findViewById(R.id.textView_durationAndUnit)
        val txtViewPrice: TextView = itemView.findViewById(R.id.textView_priceAndCurrency)
        val btnAdd: Button = itemView.findViewById(R.id.btn_add)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_select_services, parent, false)
        return ServiceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]
        holder.txtViewName.text = service.serviceName
        holder.txtViewDuration.text = "${service.duration} ${service.durationUnit}"
        holder.txtViewPrice.text = "${service.price} ${service.currency}"

        holder.btnAdd.setOnClickListener {
            onAddClick(service)
        }
    }

    override fun getItemCount(): Int {
        return services.size
    }

    // Update data in adapter
    fun updateData(newServices: List<Service>) {
        services = newServices
        notifyDataSetChanged()
    }
}