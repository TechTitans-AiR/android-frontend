package com.example.ttpay.model

// MerchantAdapter.kt
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.accountManagement.activity_accountManagement.DetailsMerchantActivity
import com.example.ttpay.R
import com.example.ttpay.accountManagement.activity_accountManagement.UpdateMerchantActivity
import com.example.ttpay.accountManagement.network_accountManagement.DeleteUser

class MerchantAdapter(private var users: List<User>) :
    RecyclerView.Adapter<MerchantAdapter.MerchantViewHolder>() {

    class MerchantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtViewName: TextView = itemView.findViewById(R.id.textView_firstName_lastName)
        val imgViewEye: ImageView = itemView.findViewById(R.id.imgView_eye)
        val imgViewPencil: ImageView = itemView.findViewById(R.id.imgView_pencil)
        val imgViewRemove: ImageView = itemView.findViewById(R.id.imgView_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MerchantViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_merchant, parent, false)
        return MerchantViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MerchantViewHolder, position: Int) {
        val user = users[position]
        holder.txtViewName.text = "${user.first_name} ${user.last_name}"

        holder.imgViewEye.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailsMerchantActivity::class.java)
            intent.putExtra("userId", user.id)
            holder.itemView.context.startActivity(intent)
        }

        holder.imgViewPencil.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateMerchantActivity::class.java)
            intent.putExtra("userId", user.id)
            holder.itemView.context.startActivity(intent)
        }

        holder.imgViewRemove.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)

            builder.setTitle("Confirm")
                .setMessage("Are you sure you want to delete this merchant?")
                .setPositiveButton("OK") { _, _ ->
                    // TODO: Implement deletion logic
                    val userIdToDelete = user.id // Get the user ID to delete
                    val delete= DeleteUser()

                    // Call the delete method here passing the context and user ID
                    delete.deleteUser(holder.itemView.context, userIdToDelete)
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }

            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    fun updateData(newUsers: List<User>) {
        users = newUsers
        notifyDataSetChanged()
    }

}
