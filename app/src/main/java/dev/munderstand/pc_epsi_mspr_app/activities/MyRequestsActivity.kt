package dev.munderstand.pc_epsi_mspr_app.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.common.ApiConfig
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.Date

class MyRequestsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_requests)

        val imageViewBack = findViewById<ImageView>(R.id.iv_Left_Arrow)
        imageViewBack.setOnClickListener {
            finish()
        }

        val pageTitle = findViewById<TextView>(R.id.tv_title)
        pageTitle.text = "Mes demandes"

        val annonces = arrayListOf<Annonce>()

        val recyclerViewMyAnnonces = findViewById<RecyclerView>(R.id.recyclerViewMyAnnonces)
        recyclerViewMyAnnonces.layoutManager = LinearLayoutManager(this)
        val annonceAdapter = AnnonceAdapter(annonces)
        recyclerViewMyAnnonces.adapter = annonceAdapter

        val sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE)
        val accountInfo = sharedPreferences?.getString("accountInfo", "")
        if (accountInfo.isNullOrEmpty()) {
            // Handle the case when there is no account info
            Toast.makeText(this, "No account info available", Toast.LENGTH_SHORT).show()
        } else {
            try {
                val jsonObject = JSONObject(accountInfo)
                val accountId = jsonObject.getInt("id").toString()


                val token = sharedPreferences.getString("token", "")

                val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()
                val mRequestUrl = ApiConfig.REQUESTS_MY_ENDPOINT + accountId
                val request =
                    Request.Builder().url(mRequestUrl).header("Authorization", "Bearer $token").cacheControl(CacheControl.FORCE_NETWORK).build()

                okHttpClient.newCall(request).enqueue(object : Callback {

                    override fun onResponse(call: Call, response: Response) {
                        val data = response.body?.string()
                        val jsonArray = JSONArray(data)

                        for (i in 0 until jsonArray.length()) {
                            val jsAnnonce = jsonArray.getJSONObject(i)
                            val annonce = Annonce(
                                jsAnnonce.getString("title"),
                                jsAnnonce.getString("description"),
                                jsAnnonce.getJSONObject("plant").getString("photo"),
                                jsAnnonce.getString("address"),
                                jsAnnonce.getJSONObject("user").getString("username"),
                                jsAnnonce.getJSONObject("plant").getString("name")
                            )
                            annonces.add(annonce)
                        }
                        runOnUiThread(Runnable {
                            annonceAdapter.notifyDataSetChanged()
                        })

                        if (data != null) {
                            Log.e("WS", data)
                        }
                    }
                    override fun onFailure(call: Call, e: IOException) {
                        runOnUiThread(Runnable {
                            Toast.makeText(application, e.message, Toast.LENGTH_SHORT).show()
                        })
                    }
                })
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }


    }
}