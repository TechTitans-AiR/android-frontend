package com.example.ttpay.model

// MerchantAdapter.kt
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.AllProductsActivity
import com.example.ttpay.DetailsMerchantActivity
import com.example.ttpay.R
import com.example.ttpay.UpdateMerchantActivity

class MerchantAdapter(private val merchants: List<String>) :
    RecyclerView.Adapter<MerchantAdapter.MerchantViewHolder>() {

    class MerchantViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txt_view_name: TextView = itemView.findViewById(R.id.textView_firstName_lastName)
        val txt_view_eye: ImageView = itemView.findViewById(R.id.imgView_eye)
        val txt_view_pencil: ImageView = itemView.findViewById(R.id.imgView_pencil)
        val txt_view_remove: ImageView = itemView.findViewById(R.id.imgView_remove)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MerchantViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_merchant, parent, false)
        return MerchantViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MerchantViewHolder, position: Int) {
        val merchant = merchants[position]
        holder.txt_view_name.text = merchant

        holder.txt_view_eye.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailsMerchantActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }

        holder.txt_view_pencil.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateMerchantActivity::class.java)
            holder.itemView.context.startActivity(intent)
        }

        holder.txt_view_remove.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)

            // Dialog settings
            builder.setTitle("Confirm")
                .setMessage("Are you sure you want to delete this merchant?")
                .setPositiveButton("OK") { _, _ ->
                    // Implement
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
        return merchants.size
    }
}
