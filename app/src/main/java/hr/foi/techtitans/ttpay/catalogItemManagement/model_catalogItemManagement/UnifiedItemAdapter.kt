package hr.foi.techtitans.ttpay.catalogItemManagement.model_catalogItemManagement

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.products.model_products.Article
import hr.foi.techtitans.ttpay.products.model_products.Service

class UnifiedItemAdapter<T>(private val itemList: List<T>, private val isService: Boolean) :
    RecyclerView.Adapter<UnifiedItemAdapter<T>.ItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_articles_services, parent, false)
        return ItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val itemNameTextView: TextView = itemView.findViewById(R.id.textViewItemName)
        private val itemDetails1TextView: TextView = itemView.findViewById(R.id.textViewItemDetails1)
        private val itemDetails2TextView: TextView = itemView.findViewById(R.id.textViewItemDetails2)

        fun bind(item: T) {
            when (item) {
                is Article -> bindArticle(item)
                is Service -> bindService(item)
                else -> throw IllegalArgumentException("Invalid item type")
            }
        }

        private fun bindArticle(article: Article) {
            itemNameTextView.text = article.name
            itemDetails1TextView.text = "${article.weight} kg"
            itemDetails2TextView.text = "${article.price} ${article.currency}"
        }

        private fun bindService(service: Service) {
            itemNameTextView.text = service.serviceName
            itemDetails1TextView.text = "${service.duration} ${service.durationUnit}"
            itemDetails2TextView.text = "${service.price} ${service.currency}"
        }
    }
}