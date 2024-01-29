package hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.products.model_products.Article

class CollectedArticlesCatalogAdapter (
    private var articles: List<Article>,
) : RecyclerView.Adapter<CollectedArticlesCatalogAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtViewName: TextView = itemView.findViewById(R.id.textView_article_name)
        val txtViewWeight: TextView = itemView.findViewById(R.id.textView_weight)
        val txtViewPrice: TextView = itemView.findViewById(R.id.textView_price_currency)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_collected_articles_catalog, parent, false)
        return ArticleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.txtViewName.text = article.name
        holder.txtViewWeight.text = "${article.weight} weight"
        holder.txtViewPrice.text = "${article.price} ${article.currency}"
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    // Update data in adapter
    fun updateData(newArticles: List<Article>) {
        articles = newArticles
        notifyDataSetChanged()
    }
}