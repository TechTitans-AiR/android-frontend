package hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.products.model_products.Service

class CollectedServicesCatalogAdapter (
    private var services: List<Service>
) : RecyclerView.Adapter<CollectedServicesCatalogAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtViewName: TextView = itemView.findViewById(R.id.textView_service_name)
        val txtViewDuration: TextView = itemView.findViewById(R.id.textView_durationAndUnit)
        val txtViewPrice: TextView = itemView.findViewById(R.id.textView_priceAndCurrency)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collected_services_catalog, parent, false)
        return ServiceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]
        holder.txtViewName.text = service.serviceName
        holder.txtViewDuration.text = "${service.duration} ${service.durationUnit}"
        holder.txtViewPrice.text = "${service.price} ${service.currency}"
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