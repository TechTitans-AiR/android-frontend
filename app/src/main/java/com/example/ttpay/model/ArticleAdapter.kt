package com.example.ttpay.model

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.R
import com.example.ttpay.products.activity_products.DetailsArticleActivity
import com.example.ttpay.products.activity_products.UpdateArticleActivity

class ArticleAdapter(private var articles: List<Article>) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private var selectedArticleId: Int = -1
    private var onItemClickListener: ((Int) -> Unit)? = null // Listener za klik na artikl

    fun setOnItemClickListener(listener: (Int) -> Unit) {
        onItemClickListener = listener
    }
    class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtViewName: TextView = itemView.findViewById(R.id.textView_article_name)
        val imgViewEye: ImageView = itemView.findViewById(R.id.imgView_eye)
        val imgViewPencil: ImageView = itemView.findViewById(R.id.imgView_pencil)
        val imgViewRemove: ImageView = itemView.findViewById(R.id.imgView_remove)
        val txtViewWeight: TextView = itemView.findViewById(R.id.textView_weight)
        val txtViewPrice: TextView = itemView.findViewById(R.id.textView_price_currency)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = articles[position]
        holder.txtViewName.text = article.name
        holder.txtViewWeight.text = "${article.weight} weight"
        holder.txtViewPrice.text = "${article.price} ${article.currency}"

        holder.imgViewEye.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailsArticleActivity::class.java)
            intent.putExtra("articleId", article.id)
            holder.itemView.context.startActivity(intent)
        }

        holder.imgViewPencil.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateArticleActivity::class.java)
            intent.putExtra("articleId", article.id)
            holder.itemView.context.startActivity(intent)
        }

        holder.imgViewRemove.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)

            builder.setTitle("Confirm")
                .setMessage("Are you sure you want to delete this article?")
                .setPositiveButton("OK") { _, _ ->
                    val selectedArticle=articles.find { it.id==selectedArticleId.toString() }

                    if(selectedArticle != null){
                        deleteSelectedArticle(selectedArticle)
                    }

                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }

            val dialog = builder.create()
            dialog.show()
        }
    }

    fun setSelectedArticleId(articleId: Int) {
        selectedArticleId = articleId
    }

    private fun deleteSelectedArticle(selectedArticle: Article?) {

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