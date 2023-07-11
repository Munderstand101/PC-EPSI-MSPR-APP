package dev.munderstand.pc_epsi_mspr_app.activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
import dev.munderstand.pc_epsi_mspr_app.R

class PlantDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_details)

        // Retrieve plant details from intent extras
        val title = intent.getStringExtra("title")
        val description = intent.getStringExtra("description")
        val pictureUrl = intent.getStringExtra("pictureUrl")

        // Update the views in the layout with the plant details
        val textViewTitle = findViewById<TextView>(R.id.textViewTitle)
        val textViewDescription = findViewById<TextView>(R.id.textViewDescription)
        val imageViewPlant = findViewById<ImageView>(R.id.imageViewPlant)

        textViewTitle.text = title
        textViewDescription.text = description
        Picasso.get().load(pictureUrl).into(imageViewPlant)
    }
}
