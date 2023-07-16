package dev.munderstand.pc_epsi_mspr_app.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.common.ApiConfig
import java.time.format.DateTimeFormatter

class AnnonceAdapter(val annonces: ArrayList<Annonce>): RecyclerView.Adapter<AnnonceAdapter.ViewHolder>() {

        override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.cell_annonces, viewGroup, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val annonce = annonces[position]
            holder.textViewTitle.text = annonce.title
            holder.textViewDescription.text = annonce.description
            Picasso.get().load(ApiConfig.BASE_URL_PHOTOS + annonce.imgUrl).into(holder.imageViewPlantAnnonce)
        }

        override fun getItemCount(): Int {
            return annonces.size
        }

        class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val textViewTitle = view.findViewById<TextView>(R.id.textViewTitleAnnonce)
            val textViewDescription = view.findViewById<TextView>(R.id.textViewDescriptionAnnonce)
            val imageViewPlantAnnonce = view.findViewById<ImageView>(R.id.imageViewPlantAnnonce)
        }
}