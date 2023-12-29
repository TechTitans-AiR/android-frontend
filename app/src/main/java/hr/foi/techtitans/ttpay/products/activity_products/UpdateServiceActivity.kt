package hr.foi.techtitans.ttpay.products.activity_products

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import hr.foi.techtitans.ttpay.R

class UpdateServiceActivity : AppCompatActivity() {

    private lateinit var userUsername: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_service)

        userUsername = intent.getStringExtra("username") ?: ""

        val btnBack: ImageView = findViewById(R.id.imgView_back)

        btnBack.setOnClickListener {
            intent.putExtra("username", userUsername)
            finish()
        }
    }
}