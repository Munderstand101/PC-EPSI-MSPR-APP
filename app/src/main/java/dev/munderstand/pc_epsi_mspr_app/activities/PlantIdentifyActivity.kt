package dev.munderstand.pc_epsi_mspr_app.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import java.util.Base64
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.common.ApiConfig
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.Response
import java.io.IOException


class PlantIdentifyActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plant_identify)

        val imageViewBack = findViewById<ImageView>(R.id.iv_Left_Arrow)
        imageViewBack.setOnClickListener(View.OnClickListener {
            finish()
        })

        val img = intent.extras?.getString("urlImg")
//        //Log.e("WS", img.toString())
        val oriUrl = ApiConfig.BASE_URL_PHOTOS + img
        val encodedUrl = Base64.getUrlEncoder().encodeToString(img?.toByteArray())
//        //Log.e("WS", encodedUrl.toString())

        val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()
        val mRequestUrl = "https://plant.id/api/v2/identify"
        val mediaType = "application/json".toMediaTypeOrNull()
        val body = RequestBody.create(mediaType, """
            {
                "images": [
                    "data:image/jpeg;base64,$encodedUrl"
                ],
                "organs": ["leaf"],
                "modifiers": ["health_auto"],
                "plant_details": [
                    "common_names",
                    "url",
                    "wiki_description",
                    "taxonomy",
                    "synonyms"
                ],
                "disease_details": ["common_names", "url", "description", "treatment"]
            }""".trimIndent())

        val request = Request.Builder()
            .url(mRequestUrl)
            .method("POST", body)
            .addHeader("Api-Key", ApiConfig.API_PLANT_ID_KEY)
            .addHeader("Content-Type", "application/json")
            .cacheControl(CacheControl.FORCE_NETWORK)
            .build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                runOnUiThread(Runnable {
                    Toast.makeText(application, e.message, Toast.LENGTH_SHORT).show()
                })
            }

            override fun onResponse(call: Call, response: Response) {
                val data = response.body?.string()
//                val jsonArray = JSONArray(data)

                //Log.e("WS", data.toString())
            }

        })

    }

}