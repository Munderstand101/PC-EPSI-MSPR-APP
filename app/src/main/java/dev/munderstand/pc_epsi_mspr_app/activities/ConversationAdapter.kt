package dev.munderstand.pc_epsi_mspr_app.activities

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.fragments.MessagesFragment

class ConversationAdapter(val conversations: MutableList<Conversation>) :
    RecyclerView.Adapter<ConversationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conversation = conversations[position]

        holder.textViewName.text = "${conversation.firstName} ${conversation.lastName}"
        holder.textViewDesc.text = conversation.username

        // Load the conversation image using Picasso or any other image loading library
        if (!conversation.pictureUrl.isNullOrEmpty()) {
            Picasso.get().load(conversation.pictureUrl).into(holder.imageViewProduit)
        } else {
            // Set a placeholder image or handle the empty pictureUrl case differently
            holder.imageViewProduit.setImageResource(R.drawable.ic_growing_plant_white)
        }

        holder.contentLayout.setOnClickListener {
            val activity = holder.itemView.context as Activity
            val intent = Intent(activity?.applicationContext, MessagesActivity::class.java)
            intent.putExtra("user_cible_id", conversation.id)
            intent.putExtra("user_cible_firstName", conversation.firstName)
            intent.putExtra("user_cible_lastName", conversation.lastName)
            intent.putExtra("user_cible_username", conversation.username)
            intent.putExtra("user_cible_pictureUrl", conversation.pictureUrl)
            it.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return conversations.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.tv_fullname)
        val textViewDesc: TextView = view.findViewById(R.id.tv_username)
        val imageViewProduit: ImageView = view.findViewById(R.id.iv_profile_picture)
        val contentLayout: LinearLayout = view.findViewById(R.id.contentLayout)
    }
}
