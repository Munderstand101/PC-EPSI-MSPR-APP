package dev.munderstand.pc_epsi_mspr_app.activities

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import java.io.IOException

class AnnoncesActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_annonces)

        setHeaderTxt("Annonces")
        showBack()

        val annonces = arrayListOf<Annonce>()

        val recyclerViewAnnonces = findViewById<RecyclerView>(R.id.recyclerViewAnnonces)
        recyclerViewAnnonces.layoutManager = LinearLayoutManager(this)
        val annonceAdapter = AnnonceAdapter(annonces)
        recyclerViewAnnonces.adapter = annonceAdapter

        val sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE)
        val token = sharedPreferences?.getString("token", "")

        val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()
        val mRequestUrl = ApiConfig.REQUESTS_ENDPOINT
        val request =
            Request.Builder().url(mRequestUrl).header("Authorization", "Bearer $token").cacheControl(
                CacheControl.FORCE_NETWORK).build()

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
    }
}