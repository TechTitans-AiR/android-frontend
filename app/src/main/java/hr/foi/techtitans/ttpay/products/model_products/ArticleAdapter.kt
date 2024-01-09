package hr.foi.techtitans.ttpay.products.model_products

import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import hr.foi.techtitans.ttpay.products.activity_products.DetailsArticleActivity
import hr.foi.techtitans.ttpay.products.activity_products.UpdateArticleActivity

class ArticleAdapter(private var articles: List<Article>, private val loggedInUser: LoggedInUser) :
    RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder>() {

    private var selectedArticleId: String = ""

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
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("articleId", article.id)
            holder.itemView.context.startActivity(intent)
        }

        holder.imgViewPencil.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateArticleActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("articleId", article.id)
            holder.itemView.context.startActivity(intent)
        }

        holder.imgViewRemove.setOnClickListener {
            val builder = AlertDialog.Builder(holder.itemView.context)

            builder.setTitle("Confirm")
                .setMessage("Are you sure you want to delete this article?")
                .setPositiveButton("OK") { _, _ ->
                    selectedArticleId=article.id
                    val selectedArticle=articles.find { it.id==selectedArticleId }

                    if(selectedArticle != null){
                        Log.d("Deleting article: ", selectedArticle.name)
                        deleteSelectedArticle(selectedArticle.id)
                    }

                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }

            val dialog = builder.create()
            dialog.show()
        }
    }

    private fun deleteSelectedArticle(selectedArticle: String?) {
        DeleteArticle().deleteArticle(loggedInUser, selectedArticle)
        val updatedArticles = articles.filterNot { it.id == selectedArticle } //select all articles except deleted one
        updateData(updatedArticles)//update recyclerView to show rest of the articles
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