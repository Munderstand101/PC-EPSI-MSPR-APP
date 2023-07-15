package dev.munderstand.pc_epsi_mspr_app.fragments

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.PlantDetailsActivity

class PlantAdapter(val offrres: MutableList<Plant>) :
    RecyclerView.Adapter<PlantAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item, parent, false)
//            .inflate(R.layout.list_item_all, parent, false)
//            .inflate(R.layout.list_item_board, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val offre = offrres[position]
        holder.textViewName.text = offre.titre
        holder.textViewDesc.text = offre.description
        Picasso.get().load(offre.picture_url).into(holder.imageViewProduit)
        holder.contentLayout.setOnClickListener {
            // Start PlantDetailsActivity on item click
            val intent = Intent(holder.contentLayout.context, PlantDetailsActivity::class.java)
            // Pass any necessary data to PlantDetailsActivity using intent extras
            intent.putExtra("title", offre.titre)
            intent.putExtra("description", offre.description)
            intent.putExtra("pictureUrl", offre.picture_url)
            holder.contentLayout.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return offrres.size
    }


//    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val textViewName: TextView = view.findViewById(R.id.text_title)
//        val textViewDesc: TextView = view.findViewById(R.id.text_description)
//     //   val textViewDate: TextView = view.findViewById(R.id.text_date)
//        val imageViewProduit: ImageView = view.findViewById(R.id.image_content)
//        val contentLayout: CardView = view.findViewById(R.id.card_view_image_content)
//    }

//    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
//        val textViewName: TextView = view.findViewById(R.id.text_title)
//        val textViewDesc: TextView = view.findViewById(R.id.text_date_time)
//        val imageViewProduit: ImageView = view.findViewById(R.id.image_content)
//        val contentLayout: CardView = view.findViewById(R.id.card_view_image_content)
//    }


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.tv_ProductName)
        val textViewDesc: TextView = view.findViewById(R.id.tv_Desc)
        val imageViewProduit: ImageView = view.findViewById(R.id.iv_Product)
        val contentLayout: LinearLayout = view.findViewById(R.id.contentLayout)
    }
}
