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
import com.example.ttpay.DetailsMerchantActivity
import com.example.ttpay.R
import com.example.ttpay.UpdateMerchantActivity

class MerchantAdapter(private val users: List<User>) :
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
        holder.txtViewName.text = "${user.firstName} ${user.lastName}"

        holder.imgViewEye.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailsMerchantActivity::class.java)
            // Pass the user ID or other necessary data to the details activity if needed
            intent.putExtra("userId", user.id)
            holder.itemView.context.startActivity(intent)
        }

        holder.imgViewPencil.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateMerchantActivity::class.java)
            // Pass the user ID or other necessary data to the update activity if needed
            intent.putExtra("userId", user.id)
            holder.itemView.context.startActivity(intent)
        }

        holder.imgViewRemove.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)

            // Dialog settings
            builder.setTitle("Confirm")
                .setMessage("Are you sure you want to delete this merchant?")
                .setPositiveButton("OK") { _, _ ->
                    // Implement deletion logic
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss() // Close dialog
                }

            // Create and display dialog
            val dialog = builder.create()
            dialog.show()
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }
}
