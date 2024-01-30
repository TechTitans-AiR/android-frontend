package hr.foi.techtitans.ttpay.login_modular.model_login

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView
import hr.foi.techtitans.ttpay.R
import hr.foi.techtitans.ttpay.core.LoginHandler
import hr.foi.techtitans.ttpay.login_modular.activity_login.LoginUser

class ModuleAdapter(private val modules: List<LoginHandler>, private val context: Context) :
    RecyclerView.Adapter<ModuleAdapter.ModuleViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ModuleViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.module_item, parent, false)
            return ModuleViewHolder(view)
        }

        override fun onBindViewHolder(holder: ModuleViewHolder, position: Int) {
            val handler = modules[position]
            holder.bind(handler)
        }

        override fun getItemCount(): Int {
            return modules.size
        }


        inner class ModuleViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val button: Button = itemView.findViewById(R.id.btnModule)

            fun bind(loginHandler : LoginHandler) {
                button.text = loginHandler.getName()
                button.setOnClickListener {
                    val intent = Intent(context,LoginUser::class.java)
                    intent.putExtra("selectedButton", button.text)
                    context.startActivity(intent)
                }
            }
        }
    }