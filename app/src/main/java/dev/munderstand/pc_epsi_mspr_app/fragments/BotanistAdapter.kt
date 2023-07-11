package dev.munderstand.pc_epsi_mspr_app.fragments

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.BotanistDetailsActivity

class BotanistAdapter(private val botanists: List<Botanist>) :
    RecyclerView.Adapter<BotanistAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val botanist = botanists[position]

        holder.itemView.apply {
            findViewById<TextView>(R.id.tv_ProductName).text = botanist.name
            findViewById<TextView>(R.id.tv_Desc).text = botanist.specialization

            val imageViewBotanist = findViewById<ImageView>(R.id.iv_Product)

            // Load the botanist image using Picasso or any other image loading library
            if (!botanist.pictureUrl.isNullOrEmpty()) {
                Picasso.get().load(botanist.pictureUrl).into(imageViewBotanist)
            } else {
                // Set a placeholder image if the pictureUrl is empty or null
                imageViewBotanist.setImageResource(R.drawable.ic_growing_plant_black)
            }

            setOnClickListener {
                // Handle botanist item click event
                val context = holder.itemView.context
                Toast.makeText(context, botanist.name, Toast.LENGTH_SHORT).show()

                // Open BotanistDetailsActivity and pass the botanist details as intent extras
                // Replace BotanistDetailsActivity::class.java with your actual activity class
                val intent = Intent(context, BotanistDetailsActivity::class.java)
                intent.putExtra("name", botanist.name)
                intent.putExtra("specialization", botanist.specialization)
                intent.putExtra("address", botanist.address)
                intent.putExtra("pictureUrl", botanist.pictureUrl)
                context.startActivity(intent)
            }
        }
    }

    override fun getItemCount(): Int {
        return botanists.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}
