package hr.foi.techtitans.ttpay.products.model_products

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.core.LoggedInUser
import hr.foi.techtitans.ttpay.products.activity_products.DetailsServiceActivity
import hr.foi.techtitans.ttpay.products.activity_products.UpdateServiceActivity

class ServiceAdapter(private var services: List<Service>, private  val loggedInUser: LoggedInUser) :
    RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    private var selectedServiceId: String = ""

    class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtViewName: TextView = itemView.findViewById(R.id.textView_service_name)
        val txtViewDuration: TextView = itemView.findViewById(R.id.textView_durationAndUnit)
        val txtViewPrice: TextView = itemView.findViewById(R.id.textView_priceAndCurrency)
        val imgViewEye: ImageView = itemView.findViewById(R.id.imgView_eye)
        val imgViewPencil: ImageView = itemView.findViewById(R.id.imgView_pencil)
        val imgViewRemove: ImageView = itemView.findViewById(R.id.imgView_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_service, parent, false)
        return ServiceViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]
        // Displaying service name with additional details
        holder.txtViewName.text = service.serviceName
        holder.txtViewDuration.text = "${service.duration} ${service.durationUnit}"
        holder.txtViewPrice.text = "${service.price} ${service.currency}"

        holder.imgViewEye.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailsServiceActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("serviceId", service.id)
            holder.itemView.context.startActivity(intent)
        }

        holder.imgViewPencil.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateServiceActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("serviceId", service.id)
            holder.itemView.context.startActivity(intent)
        }

        holder.imgViewRemove.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)

            builder.setTitle("Confirm")
                .setMessage("Are you sure you want to delete this service?")
                .setPositiveButton("OK") { _, _ ->
                    selectedServiceId=service.id
                    val selectedService=services.find { it.id==selectedServiceId }

                    if(selectedService != null){
                        Log.d("Deleting service: ", selectedService.id)
                        deleteSelectedServices(selectedService.id)
                    }
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun deleteSelectedServices(serviceID: String) {
        DeleteService().deleteService(serviceID)
        val updatedServices = services.filterNot { it.id == serviceID } //select all articles except deleted one
        updateData(updatedServices)//update recyclerView to show rest of the articles
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