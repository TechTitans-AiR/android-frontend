package com.example.ttpay.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.R
import com.example.ttpay.transactions.network_transactions.ShoppingCartItem

class ShoppingCartAdapter(
    private val items: MutableList<ShoppingCartItem>,
    private val onQuantityChanged: (item: ShoppingCartItem, unitPrice: Double) -> Unit
) : RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.tv_product_name)
        val quantity: TextView = itemView.findViewById(R.id.tv_quantity)
        val price: TextView = itemView.findViewById(R.id.tv_price)
        val plusButton: ImageView = itemView.findViewById(R.id.img_plus)
        val minusButton: ImageView = itemView.findViewById(R.id.img_minus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_shopping_cart, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.productName.text = item.name
        holder.quantity.text = item.quantity.toString()

        // Adjust the price according to the formula
        val totalPrice = item.quantity * item.unitPrice
        holder.price.text = "€${"%.2f".format(totalPrice)}"

        holder.plusButton.setOnClickListener {
            // Increase the quantity and update the textual display
            item.quantity++
            notifyDataSetChanged()

            // Update the total amount on the screen
            onQuantityChanged.invoke(item, item.unitPrice)
        }

        holder.minusButton.setOnClickListener {
            // Decrease the quantity and update the textual display
            if (item.quantity > 0) {
                item.quantity--
                notifyDataSetChanged()

                // Update the total amount on the screen
                onQuantityChanged.invoke(item, item.unitPrice)
            }
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun updateData(newItems: List<ShoppingCartItem>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

}