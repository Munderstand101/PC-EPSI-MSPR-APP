package dev.munderstand.pc_epsi_mspr_app.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.common.ApiConfig
import dev.munderstand.pc_epsi_mspr_app.activities.common.BaseActivity
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

class ConversationsActivity : BaseActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ConversationAdapter
    private val items = mutableListOf<Conversation>()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_conversations)

        setHeaderTxt("Vos conversations")
        showBack()

        recyclerView = findViewById<RecyclerView>(R.id.rcv_conversations)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ConversationAdapter(items)
        recyclerView.adapter = adapter

        val sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE)
        val accountInfo = sharedPreferences?.getString("accountInfo", "")
        val token = sharedPreferences?.getString("token", "")

        val jsonObject = JSONObject(accountInfo.toString())
        val accountId = jsonObject.getInt("id").toString()
        fetchConversations(accountId, token.toString())

        swipeRefreshLayout = findViewById<SwipeRefreshLayout>(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            // Refresh data when the user swipes down
            fetchConversations(accountId, token.toString())
        }
    }

    private fun fetchConversations(id: String, token: String) {
//        //Log.e("WS id : ", id)
        val conversations = arrayListOf<Conversation>()

        val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()
        val mRequestUrl = ApiConfig.CONVERSATIONS_ENDPOINT + id
        val request =
            Request.Builder().url(mRequestUrl).header("Authorization", "Bearer $token")
                .cacheControl(CacheControl.FORCE_NETWORK).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()

                val jsonArray = JSONArray(data)

                for (i in 0 until jsonArray.length()) {
                    val jsAnnonce = jsonArray.getJSONObject(i)
                    val conv = Conversation(
                        jsAnnonce.getInt("id"),
                        jsAnnonce.getJSONObject("targetUser").getInt("id"),
                        jsAnnonce.getJSONObject("targetUser").getString("username"),
                        jsAnnonce.getJSONObject("targetUser").getString("firstName"),
                        jsAnnonce.getJSONObject("targetUser").getString("lastName"),
                        jsAnnonce.getJSONObject("targetUser").getString("picture_url")
                    )
                    //Log.e("WS conv : ", conv.toString())
                    conversations.add(conv)
                }

                // Update the UI on the main thread
                runOnUiThread {
                    items.clear()
                    items.addAll(conversations)
                    adapter.notifyDataSetChanged()
                    swipeRefreshLayout.isRefreshing = false
                }

//                data?.let {
////                    //Log.e("WS data : ", it)
//                }
            }

            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread {
                    Toast.makeText(application, e.message, Toast.LENGTH_SHORT).show()
                    swipeRefreshLayout.isRefreshing = false
                }
            }
        })
    }
}