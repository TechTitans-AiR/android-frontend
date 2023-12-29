package hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.accountManagement.model_accountManagement.User

class SelectUserAdapter (
    private var merchants: List<User>,
    private val onAddClick: (User) -> Unit
    ) : RecyclerView.Adapter<SelectUserAdapter.UserViewHolder>() {

        class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val txtViewUserName: TextView = itemView.findViewById(R.id.textView_user_name)
            val txtEmail: TextView = itemView.findViewById(R.id.textView_user_email)
            val btnAdd: Button = itemView.findViewById(R.id.btn_add)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_select_user, parent, false)
            return UserViewHolder(itemView)
        }

        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
            val user = merchants[position]
            holder.txtViewUserName.text = "${user.first_name} ${user.last_name}"
            holder.txtEmail.text = user.email

            holder.btnAdd.setOnClickListener {
                onAddClick(user)
            }
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
    