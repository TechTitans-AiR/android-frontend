package com.example.ttpay.model

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.ttpay.R

class ArticleDetailedCatalogAdapter(private var articles: List<String>) :
    RecyclerView.Adapter<ArticleDetailedCatalogAdapter.ArticleViewHolder>() {

    inner class ArticleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewArticleName: TextView = itemView.findViewById(R.id.textView_article_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_article, parent, false)
        return ArticleViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        holder.textViewArticleName.text = articles[position]
    }

    override fun getItemCount(): Int {
        return articles.size
    }

    fun updateData(newArticles: List<String>) {
        articles = newArticles
        notifyDataSetChanged()
    }
}
