package hr.foi.techtitans.ttpay.catalogItemManagement.createCatalog.model_createCatalog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.products.model_products.Article

class SelectArticleAdapter(
    private var articles: List<Article>,
    private val onAddClick: (Article) -> Unit
) : RecyclerView.Adapter<SelectArticleAdapter.ArticleViewHolder>() {

    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtViewName: TextView = itemView.findViewById(R.id.textView_article_name)
        val txtViewWeight: TextView = itemView.findViewById(R.id.textView_weight)
        val txtViewPrice: TextView = itemView.findViewById(R.id.textView_price_currency)
        val btnAdd: Button = itemView.findViewById(R.id.btn_add)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_select_articles, parent, false)
        return ArticleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.txtViewName.text = article.name
        holder.txtViewWeight.text = "${article.weight} weight"
        holder.txtViewPrice.text = "${article.price} ${article.currency}"

        holder.btnAdd.setOnClickListener {
            onAddClick(article)
        }
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
