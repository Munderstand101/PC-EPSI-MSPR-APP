package dev.munderstand.pc_epsi_mspr_app.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import dev.munderstand.pc_epsi_mspr_app.R
class BotanistDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_botanist_details)

        // Retrieve botanist details from intent extras
        val name = intent.getStringExtra("name")
        val specialization = intent.getStringExtra("specialization")
        val address = intent.getStringExtra("address")
        val pictureUrl = intent.getStringExtra("pictureUrl")

        // Update the views in the layout with the botanist details
        val textViewName = findViewById<TextView>(R.id.textViewName)
        val textViewSpecialization = findViewById<TextView>(R.id.textViewSpecialization)
        val textViewAddress = findViewById<TextView>(R.id.textViewAddress)
        val imageViewBotanist = findViewById<ImageView>(R.id.imageViewBotanist)

        textViewName.text = name
        textViewSpecialization.text = specialization
        textViewAddress.text = address

        if (!pictureUrl.isNullOrEmpty()) {
            Picasso.get().load(pictureUrl).into(imageViewBotanist)
        } else {
            // Load a fallback image if pictureUrl is empty
            Picasso.get().load(R.drawable.ic_growing_plant_black).into(imageViewBotanist)
        }
    }
}
