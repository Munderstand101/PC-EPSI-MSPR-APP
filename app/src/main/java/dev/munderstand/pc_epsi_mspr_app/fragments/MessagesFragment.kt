package dev.munderstand.pc_epsi_mspr_app.fragments

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.squareup.picasso.Picasso
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.common.ApiConfig
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MessagesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class MessagesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MessageAdapter
    private val items = mutableListOf<Message>()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    // Interval for refreshing messages in milliseconds (5 seconds)
    private val REFRESH_INTERVAL = 1000L

    // Handler and Runnable for refreshing messages
    private val handler = Handler()
    private val refreshRunnable = object : Runnable {
        override fun run() {
            // Fetch messages periodically
            val sharedPreferences = activity?.getSharedPreferences("account", Context.MODE_PRIVATE)
            val token = sharedPreferences?.getString("token", "")
            arguments?.getInt("conversationId")?.let {
                fetchMessages(it.toString(), token.toString())
            }

            // Post the Runnable again after the refresh interval
            handler.postDelayed(this, REFRESH_INTERVAL)
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rcv_messages)
        recyclerView.layoutManager = LinearLayoutManager(activity)

       // adapter = MessageAdapter(items)
        adapter = MessageAdapter(items, requireContext())
        recyclerView.adapter = adapter

        val sharedPreferences = activity?.getSharedPreferences("account", Context.MODE_PRIVATE)
        val accountInfo = sharedPreferences?.getString("accountInfo", "")
        val token = sharedPreferences?.getString("token", "")

        val jsonObject = JSONObject(accountInfo.toString())
        val accountId = jsonObject.getInt("id").toString()

        // Retrieve the conversationId from the fragment's arguments
        val conversationId = arguments?.getInt("conversationId") ?: 0

        val firstName = arguments?.getString("firstName") ?: ""
        val lastName = arguments?.getString("lastName") ?: ""
        val username = arguments?.getString("username") ?: ""
        val pictureUrl = arguments?.getString("pictureUrl") ?: ""

        val textViewFullName = view.findViewById<TextView>(R.id.tvFullName)
        val textViewUserName = view.findViewById<TextView>(R.id.tvUsername)
        val imageViewUser = view.findViewById<ImageView>(R.id.ivProfileImage)
        textViewFullName.text  = "$firstName $lastName"
        textViewUserName.text  = "$username"
        textViewUserName.text  = "$username"

        if (!pictureUrl.isNullOrEmpty()) {
            Picasso.get().load(pictureUrl).into(imageViewUser)
        } else {
            // Load a fallback image if pictureUrl is empty
            Picasso.get().load(R.drawable.ic_growing_plant_black).into(imageViewUser)
        }


        fetchMessages(conversationId.toString(), token.toString())

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            // Refresh data when the user swipes down
            fetchMessages(conversationId.toString(), token.toString())
        }


        val btnSend = view.findViewById<ImageView>(R.id.btnSend)
        val etMessage = view.findViewById<EditText>(R.id.etMessage)

        btnSend.setOnClickListener {
            val messageContent = etMessage.text.toString().trim()
            if (messageContent.isNotEmpty()) {
                val sharedPreferences = activity?.getSharedPreferences("account", Context.MODE_PRIVATE)
                val accountInfo = sharedPreferences?.getString("accountInfo", "")
                val token = sharedPreferences?.getString("token", "")

                val jsonObject = JSONObject(accountInfo.toString())
                val accountId = jsonObject.getInt("id").toString()

                // Pass the callback to sendMessage
                sendMessage(messageContent, conversationId.toString(), token.toString(), object : OnMessageSentListener {
                    override fun onMessageSent() {
                        // Fetch messages after sending the new message successfully
                        fetchMessages(conversationId.toString(), token.toString())
                    }
                })

                etMessage.text.clear()
            }
        }


    }

    override fun onResume() {
        super.onResume()
        // Start refreshing messages when the fragment is visible
        handler.postDelayed(refreshRunnable, REFRESH_INTERVAL)
    }

    override fun onPause() {
        super.onPause()
        // Stop refreshing messages when the fragment is not visible
        handler.removeCallbacks(refreshRunnable)
    }

    private fun fetchMessages(id: String, token: String) {
        //Log.e("MS id : ", id)
        val messages = arrayListOf<Message>()

        val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()
        val mRequestUrl = ApiConfig.MESSAGES_ENDPOINT + id
        val request =
            Request.Builder().url(mRequestUrl).header("Authorization", "Bearer $token")
                .cacheControl(CacheControl.FORCE_NETWORK).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()

                val jsonArray = JSONArray(data)

                for (i in 0 until jsonArray.length()) {
                    val jsAnnonce = jsonArray.getJSONObject(i)
                    val createdAtString = jsAnnonce.getString("createdAt")
                    val createdAt = parseDate(createdAtString) // Parse the createdAt string to Date object
                    val message = Message(
                        jsAnnonce.getInt("id"),
                        jsAnnonce.getString("content"),
                        jsAnnonce.getJSONObject("sender").getInt("id"),
                        jsAnnonce.getJSONObject("sender").getString("username"),
                        jsAnnonce.getJSONObject("sender").getString("firstName"),
                        jsAnnonce.getJSONObject("sender").getString("lastName"),
                        jsAnnonce.getJSONObject("sender").getString("picture_url"),
                        createdAt // Pass the createdAt date to the Message object
                    )
                    messages.add(message)
                }

                // Update the UI on the main thread
                activity?.runOnUiThread {
                    items.clear()
                    items.addAll(messages)
                    adapter.notifyDataSetChanged()
                    swipeRefreshLayout.isRefreshing = false

                    // Scroll to the bottom of the RecyclerView after updating the data
                    scrollToBottom()
                }

                data?.let {
                    //Log.e("WS data : ", it)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    Toast.makeText(activity, e.message, Toast.LENGTH_SHORT).show()
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        })
    }

    private fun parseDate(dateString: String?): Date {
        val pattern = "yyyy-MM-dd'T'HH:mm:ssXXX" // Format of the date string from the API
        val sdf = SimpleDateFormat(pattern, Locale.getDefault())
        return try {
            sdf.parse(dateString.toString())
        } catch (e: ParseException) {
           // Log.e("MessagesFragment", "Error parsing date: $dateString")
            // Return the current date and time if parsing fails
            Date()
        } ?: Date()
    }


    private fun sendMessage(
        messageContent: String,
        accountId: String,
        token: String,
        onMessageSentListener: OnMessageSentListener?
    ) {
        val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()

        val requestBody = JSONObject()
        requestBody.put("content", messageContent)

        val request = Request.Builder()
            .url(ApiConfig.SEND_MESSAGES_ENDPOINT + accountId)
            .header("Authorization", "Bearer $token")
            .post(RequestBody.create("application/json".toMediaTypeOrNull(), requestBody.toString()))
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                activity?.runOnUiThread {
                    if (response.isSuccessful) {
                        // Message sent successfully, handle the response if needed
                        // For example, you can show a toast message
                        Toast.makeText(activity, "Message sent successfully!", Toast.LENGTH_SHORT).show()

                        // Call the callback to trigger message refresh
                        onMessageSentListener?.onMessageSent()
                        scrollToBottom()
                    } else {
                        // Message sending failed, handle the error response if needed
                        // For example, you can show a toast message
                        Toast.makeText(activity, "Failed to send message.", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                activity?.runOnUiThread {
                    // Message sending failed, handle the error if needed
                    // For example, you can show a toast message
                    Toast.makeText(activity, "Failed to send message.", Toast.LENGTH_SHORT).show()
                }
            }
        })
    }


    interface OnMessageSentListener {
        fun onMessageSent()
    }

    private fun scrollToBottom() {
        // Scroll to the last item in the RecyclerView
        recyclerView.scrollToPosition(adapter.itemCount - 2)
    }

    companion object {
        fun newInstance(conversationId: Int, firstName: String, lastName: String, username: String, pictureUrl: String): MessagesFragment {
            return MessagesFragment().apply {
                arguments = Bundle().apply {
                    putInt("conversationId", conversationId)
                    putString("firstName", firstName)
                    putString("lastName", lastName)
                    putString("username", username)
                    putString("pictureUrl", pictureUrl)
                }
            }
        }
    }


}