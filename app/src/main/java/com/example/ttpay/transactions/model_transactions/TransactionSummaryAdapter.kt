package com.example.ttpay.transactions.model_transactions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.R

class TransactionSummaryAdapter (private val items: List<ShoppingCartItem>) :
    RecyclerView.Adapter<TransactionSummaryAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.tv_product_name_summary)
        val quantity: TextView = itemView.findViewById(R.id.tv_quantity_summary)
        val price: TextView = itemView.findViewById(R.id.tv_price_summary)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_transaction_summary, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.productName.text = item.name
        holder.quantity.text = "Quantity: ${item.quantity}"
        holder.price.text = "€${"%.2f".format(item.quantity * item.unitPrice)}"
    }

    override fun getItemCount(): Int {
        return items.size
    }
}