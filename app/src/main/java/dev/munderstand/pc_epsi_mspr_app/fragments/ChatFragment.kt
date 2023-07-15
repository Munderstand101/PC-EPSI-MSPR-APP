package dev.munderstand.pc_epsi_mspr_app.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import dev.munderstand.pc_epsi_mspr_app.R
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException

class ChatFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val messageInput: EditText = view.findViewById(R.id.message_input)
        val sendButton: Button = view.findViewById(R.id.send_button)
        val messageDisplay: TextView = view.findViewById(R.id.message_display)

        sendButton.setOnClickListener {
            val message = messageInput.text.toString().trim()

            if (message.isNotEmpty()) {
                sendMessageToAPI(message)
                messageInput.text.clear()
            }
        }
    }

    private fun sendMessageToAPI(message: String) {
        val url = "https://your-api-url.com/messages" // Replace with your Symfony API URL

        val client = OkHttpClient()

        val json = """
            {
                "message": "$message"
            }
        """.trimIndent()

        val body = RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), json)
        val request = Request.Builder()
            .url(url)
            .post(body)
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    displayMessage("Message sent successfully")
                } else {
                    displayMessage("Failed to send message")
                }
            }
        })
    }

    private fun displayMessage(message: String) {
        val messageDisplay: TextView = view?.findViewById(R.id.message_display) ?: return
        val currentMessages = messageDisplay.text.toString()
        val newMessage = "$currentMessages\n$message"
        messageDisplay.text = newMessage
    }
}
