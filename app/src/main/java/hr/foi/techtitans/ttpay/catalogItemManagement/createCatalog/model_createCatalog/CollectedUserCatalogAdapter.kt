package hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User

class CollectedUserCatalogAdapter (
    private var merchants: List<User>
) : RecyclerView.Adapter<CollectedUserCatalogAdapter.UserViewHolder>() {

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtViewUserName: TextView = itemView.findViewById(R.id.textView_user_name)
        val txtEmail: TextView = itemView.findViewById(R.id.textView_user_email)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collected_user_catalog, parent, false)
        return UserViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = merchants[position]
        holder.txtViewUserName.text = "${user.first_name} ${user.last_name}"
        holder.txtEmail.text = user.email
    }

    override fun getItemCount(): Int {
        return merchants.size
    }

    // Update data in adapter
    fun updateData(newUsers: List<User>) {
        merchants = newUsers
        notifyDataSetChanged()
    }
}