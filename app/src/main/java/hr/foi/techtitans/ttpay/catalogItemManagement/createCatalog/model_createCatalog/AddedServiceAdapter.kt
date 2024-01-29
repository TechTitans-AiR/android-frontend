package hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.products.model_products.Service

class AddedServiceAdapter  (
    private var services: List<Service>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<AddedServiceAdapter.ServiceViewHolder>() {

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtViewName: TextView = itemView.findViewById(R.id.textView_service_name)
        val btnDelete: Button = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_added_service, parent, false)
        return ServiceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]
        holder.txtViewName.text = service.serviceName

        holder.btnDelete.setOnClickListener {
            onDeleteClick(position)
        }
    }

    override fun getItemCount(): Int {
        return services.size
    }

    // Update data in adapter
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newArticles: List<Service>) {
        services = newArticles
        notifyDataSetChanged()
    }
}