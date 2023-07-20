package dev.munderstand.pc_epsi_mspr_app.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.squareup.picasso.Picasso
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
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import kotlin.properties.Delegates

class BotanistDetailsActivity : BaseActivity() {

    var id by Delegates.notNull<Int>()
    lateinit var firstname: String
    lateinit var lastname: String
    lateinit var username: String
    lateinit var pictureUrl: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_botanist_details)

        // Retrieve botanist details from intent extras
        id = intent.getIntExtra("id", 0)
        firstname = intent.getStringExtra("firstname").toString()
        lastname = intent.getStringExtra("lastname").toString()
        username = intent.getStringExtra("username").toString()
        val specialization = intent.getStringExtra("specialization")
        val address = intent.getStringExtra("address")
        pictureUrl = intent.getStringExtra("pictureUrl").toString()

        val sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")!!

        setHeaderTxt("$firstname $lastname")
        showBack()

        // Update the views in the layout with the botanist details
        val textViewName = findViewById<TextView>(R.id.textViewName)
        val textViewSpecialization = findViewById<TextView>(R.id.textViewSpecialization)
        val textViewAddress = findViewById<TextView>(R.id.textViewAddress)
        val imageViewBotanist = findViewById<ImageView>(R.id.imageViewBotanist)
        val buttonSendMessages = findViewById<ImageView>(R.id.buttonSendMessages)
//
        textViewName.text = "$firstname $lastname"
        textViewSpecialization.text = specialization
        textViewAddress.text = address

        if (!pictureUrl.isNullOrEmpty()) {
            Picasso.get().load(pictureUrl).into(imageViewBotanist)
        } else {
            // Load a fallback image if pictureUrl is empty
            Picasso.get().load(R.drawable.ic_growing_plant_black).into(imageViewBotanist)
        }

        buttonSendMessages.setOnClickListener {

            val okHttpClient: OkHttpClient = OkHttpClient.Builder().build()
            val mRequestUrl = ApiConfig.START_CONVERSATIONS_ENDPOINT + id
            val request = Request.Builder()
                .url(mRequestUrl)
                .header("Authorization", "Bearer $token")
                .cacheControl(CacheControl.FORCE_NETWORK)
                .build()

            okHttpClient.newCall(request).enqueue(object : Callback {

                override fun onResponse(call: Call, response: Response) {
                    val intent = Intent(applicationContext, MessagesActivity::class.java)
                    intent.putExtra("user_cible_id", id)
                    intent.putExtra("user_cible_firstName", firstname)
                    intent.putExtra("user_cible_lastName", lastname)
                    intent.putExtra("user_cible_username", username)
                    intent.putExtra("user_cible_pictureUrl", pictureUrl)
                    startActivity(intent)
                }
                override fun onFailure(call: Call, e: IOException) {
                    runOnUiThread(Runnable {
                        Toast.makeText(application, e.message, Toast.LENGTH_SHORT).show()
                    })
                }
            })
        }

    }
}
