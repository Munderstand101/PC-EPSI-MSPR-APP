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
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.*

class MessageAdapter(val messages: MutableList<Message>, val context: Context) :
    RecyclerView.Adapter<MessageAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.layout_item_slot, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages[position]

        holder.textViewName.text = "${message.firstName} ${message.lastName}"
        holder.textViewDesc.text = message.content

        // Load the message image using Picasso or any other image loading library
        if (!message.pictureUrl.isNullOrEmpty()) {
            Picasso.get().load(message.pictureUrl).into(holder.imageViewProduit)
        } else {
            // Set a placeholder image or handle the empty pictureUrl case differently
            holder.imageViewProduit.setImageResource(R.drawable.ic_growing_plant_white)
        }

        // Get the SharedPreferences
        val sharedPreferences = context.getSharedPreferences("account", Context.MODE_PRIVATE)
        val accountInfo = sharedPreferences?.getString("accountInfo", "")
        val jsonObject = JSONObject(accountInfo.toString())
        val accountId = jsonObject.getInt("id").toString()

        // Determine the layout based on target_id
        val currentUserId = accountId.toInt() // Your current user's ID (parsed from SharedPreferences)
        val isSentByCurrentUser = message.target_id == currentUserId

        // Set data for sender or recipient layout based on your layout structure
        if (isSentByCurrentUser) {
            // Use layout_sender_item for messages sent by the current user
            holder.contentLayout.removeAllViews()
            LayoutInflater.from(holder.itemView.context).inflate(
                R.layout.layout_sender_item,
                holder.contentLayout
            )

            val senderMessageTextView: TextView =
                holder.contentLayout.findViewById(R.id.tvSenderMessage)
            val senderTimeTextView: TextView =
                holder.contentLayout.findViewById(R.id.tvSenderMessageTime)
            val senderImageViewProduit: ImageView =
                holder.contentLayout.findViewById(R.id.iv_profile_picture)
            senderMessageTextView.text = "${message.content}"
            val createdAtString = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(message.createdAt)
            senderTimeTextView.text = createdAtString


            if (!message.pictureUrl.isNullOrEmpty()) {
                Picasso.get().load(message.pictureUrl).into(senderImageViewProduit)
            } else {
                // Set a placeholder image or handle the empty pictureUrl case differently
                senderImageViewProduit.setImageResource(R.drawable.ic_growing_plant_white)
            }

        } else {
            // Use layout_recipient_item for messages received from others
            holder.contentLayout.removeAllViews()
            LayoutInflater.from(holder.itemView.context).inflate(
                R.layout.layout_recipient_item,
                holder.contentLayout
            )

            val recipientMessageTextView: TextView =
                holder.contentLayout.findViewById(R.id.tvRecipientMessage)
            val recipientTimeTextView: TextView =
                holder.contentLayout.findViewById(R.id.tvRecipientMessageTime)
            val recipientImageViewProduit: ImageView =
                holder.contentLayout.findViewById(R.id.iv_profile_picture)
            recipientMessageTextView.text = "${message.content}"

            val createdAtString = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(message.createdAt)
            recipientTimeTextView.text = createdAtString


            if (!message.pictureUrl.isNullOrEmpty()) {
                Picasso.get().load(message.pictureUrl).into(recipientImageViewProduit)
            } else {
                // Set a placeholder image or handle the empty pictureUrl case differently
                recipientImageViewProduit.setImageResource(R.drawable.ic_growing_plant_white)
            }
        }

        holder.contentLayout.setOnClickListener {
            // Start messageDetailsActivity on item click
            /* val intent = Intent(holder.contentLayout.context, messageDetailsActivity::class.java)
            // Pass any necessary data to messageDetailsActivity using intent extras
            intent.putExtra("title", message.titre)
            intent.putExtra("description", message.description)
            intent.putExtra("pictureUrl", message.picture_url)
            holder.contentLayout.context.startActivity(intent)*/
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewName: TextView = view.findViewById(R.id.tv_fullname)
        val textViewDesc: TextView = view.findViewById(R.id.tv_username)
        val imageViewProduit: ImageView = view.findViewById(R.id.iv_profile_picture)
        val contentLayout: LinearLayout = view.findViewById(R.id.contentLayout)
    }
}
