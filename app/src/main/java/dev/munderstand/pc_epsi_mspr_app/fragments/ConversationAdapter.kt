package dev.munderstand.pc_epsi_mspr_app.fragments

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.MainActivity

class ConversationAdapter(val conversations: MutableList<Conversation>) :
    RecyclerView.Adapter<ConversationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_chat_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val conversation = conversations[position]

        // Logging to check if the data is correctly retrieved
//        Log.d("ConversationAdapter", "First Name: ${conversation.firstName}")
//        Log.d("ConversationAdapter", "Last Name: ${conversation.lastName}")
//        Log.d("ConversationAdapter", "Username: ${conversation.username}")

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
            val conversationId = conversation.id
            val firstName = conversation.firstName
            val lastName = conversation.lastName
            val username = conversation.username
            val pictureUrl = conversation.pictureUrl
            val targetFragment = MessagesFragment.newInstance(conversationId, firstName, lastName, username,pictureUrl)
            val mainActivity = it.context as MainActivity
            mainActivity.replaceFragment(targetFragment)
        }


        /*  holder.contentLayout.setOnClickListener {

              // Start ConversationDetailsActivity on item click
              /* val intent = Intent(holder.contentLayout.context, ConversationDetailsActivity::class.java)
              // Pass any necessary data to ConversationDetailsActivity using intent extras
              intent.putExtra("title", conversation.titre)
              intent.putExtra("description", conversation.description)
              intent.putExtra("pictureUrl", conversation.picture_url)
              holder.contentLayout.context.startActivity(intent)*/
          }*/
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
