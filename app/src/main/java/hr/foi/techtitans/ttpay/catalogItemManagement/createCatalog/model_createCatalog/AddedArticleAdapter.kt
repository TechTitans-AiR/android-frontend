package hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.products.model_products.Article

class AddedArticleAdapter(
    private var articles: List<Article>,
    private val onDeleteClick: (Int) -> Unit
) : RecyclerView.Adapter<AddedArticleAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtViewName: TextView = itemView.findViewById(R.id.textView_article_name)
        val btnDelete: Button = itemView.findViewById(R.id.btn_delete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_added_article, parent, false)
        return ArticleViewHolder(itemView)
    }


    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        if (position in 0 until articles.size) {
            val article = articles[position]
            holder.txtViewName.text = article.name

            holder.btnDelete.setOnClickListener {
                onDeleteClick(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    // Update data in adapter
    fun updateData(newArticles: List<Article>) {
        Log.d("AddedArticleAdapter", "Updating data. New size: ${newArticles.size}")

        articles = newArticles
        notifyDataSetChanged()

        Log.d("AddedArticleAdapter", "Data updated. New size: ${articles.size}")
    }
    fun removeItem(position: Int) {
        // Provjerite je li position unutar granica
        if (position in 0 until articles.size) {
            // Stvorite novu listu bez elementa na poziciji
            val updatedList = articles.toMutableList().apply { removeAt(position) }

            // Ažurirajte podatke i obavijestite adapter
            updateData(updatedList)

            // Obavijestite RecyclerView da je element uklonjen
            notifyItemRemoved(position)

            // Ako ima promjena u redoslijedu elemenata nakon brisanja, obavijestite RecyclerView
            notifyItemRangeChanged(position, itemCount)

            // Pozovite onDeleteClick callback
            onDeleteClick(position)
        }
    }


}
