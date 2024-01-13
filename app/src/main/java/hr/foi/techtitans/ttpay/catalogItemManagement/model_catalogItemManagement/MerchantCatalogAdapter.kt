package hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.catalogItemManagement.activity_catalogItemManagement.DetailedCatalogItemActivity
import hr.foi.techtitans.ttpay.core.LoggedInUser


class MerchantCatalogAdapter (
    private var catalogs: List<Catalog>,
    private val loggedInUser: LoggedInUser
) :
    RecyclerView.Adapter<MerchantCatalogAdapter.CatalogViewHolder>() {

    class CatalogViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtViewCatalogName: TextView = itemView.findViewById(R.id.textViewCatalogName)
        val imgViewEye: ImageView = itemView.findViewById(R.id.imgView_eye_itemCatalogMerchant)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CatalogViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_merchant_catalog, parent, false)
        return CatalogViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CatalogViewHolder, position: Int) {
        val catalog = catalogs[position]
        holder.txtViewCatalogName.text = catalog.name

        holder.imgViewEye.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailedCatalogItemActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("catalogId", catalog.id)
            intent.putExtra("selectedCatalog", catalog)
            val updatedCatalog:String =""
            intent.putExtra("username", loggedInUser.username)
            intent.putExtra("updatedCatalog", updatedCatalog)
            holder.itemView.context.startActivity(intent)
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