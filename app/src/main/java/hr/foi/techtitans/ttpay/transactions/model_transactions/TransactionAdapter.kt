package hr.foi.techtitans.ttpay.transactions.model_transactions

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.login_modular.model_login.LoggedInUser
import hr.foi.techtitans.ttpay.products.activity_products.DetailsArticleActivity
import hr.foi.techtitans.ttpay.transactions.activity_transactions.DetailedTransactionActivity

class TransactionAdapter(
    private var transactions: List<Transaction>, private val loggedInUser: LoggedInUser) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtViewTransactionDescription: TextView = itemView.findViewById(R.id.textViewTransactionDescription)
        val imgViewEye: ImageView = itemView.findViewById(R.id.imgView_eye)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.txtViewTransactionDescription.text = transaction.description

        holder.imgViewEye.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailedTransactionActivity::class.java)
            intent.putExtra("loggedInUser", loggedInUser)
            intent.putExtra("transactionId", transaction._id)
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return transactions.size
    }

    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}