package dev.munderstand.pc_epsi_mspr_app.fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.NewPlantActivity
import dev.munderstand.pc_epsi_mspr_app.activities.common.ApiConfig
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class AcceuilFragment : Fragment() {

    private lateinit var searchEditText: EditText
    private lateinit var searchButton: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlantAdapter
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout
    private val items = mutableListOf<Plant>()
    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var addressTextView: TextView
    private lateinit var zipcodeTextView: TextView
    private lateinit var cityTextView: TextView

    private var accountId: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_acceuil, container, false)

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)

        recyclerView = view.findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = PlantAdapter(items)
        recyclerView.adapter = adapter

        val sharedPreferences = activity?.getSharedPreferences("account", Context.MODE_PRIVATE)
        val accountInfo = sharedPreferences?.getString("accountInfo", "")

        val token = sharedPreferences?.getString("token", "")

        if (accountInfo.isNullOrEmpty()) {
            // Handle the case when there is no account info
            Toast.makeText(activity, "No account info available", Toast.LENGTH_SHORT).show()
        } else {
            try {
                val jsonObject = JSONObject(accountInfo)
                accountId = jsonObject.getInt("id").toString()
                fetchPlantes(accountId, token.toString())

                usernameTextView = view.findViewById(R.id.text_user_name)
                val imageViewBotanist = view.findViewById<ImageView>(R.id.image_user)
                val details1TextView = view.findViewById<TextView>(R.id.text_details1)
                val details2TextView = view.findViewById<TextView>(R.id.text_details2)

                val username = jsonObject.getString("username")
                val firstName = jsonObject.getString("firstName")
                val pictureUrl = jsonObject.getString("picture_url")

                // Set the account info to the respective TextViews
                usernameTextView.text = firstName.toString()
                details1TextView.text = username.toString()
                details2TextView.text = "Role : Utilisateur normal"

                if (!pictureUrl.isNullOrEmpty()) {
                    Picasso.get().load(pictureUrl).into(imageViewBotanist)
                } else {
                    // Load a fallback image if pictureUrl is empty
                    Picasso.get().load(R.drawable.ic_growing_plant_black).into(imageViewBotanist)
                }
            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }

        // Set up the OnRefreshListener
        swipeRefreshLayout.setOnRefreshListener {
            fetchPlantes(accountId, token.toString())
        }

        return view
    }

    private fun fetchPlantes(user_id: String, token: String) {
        swipeRefreshLayout.isRefreshing = true // Show the refresh indicator

        val queue = Volley.newRequestQueue(activity)
        val url = ApiConfig.PLANTS_ENDPOINT + user_id

        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    items.clear() // Clear the previous data before adding new data
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val name = item.getString("name")
                        val description = item.getString("description")
                        val pictureUrl = "http://88.125.155.66:32768/photos/" + item.getString("photo")
                        items.add(Plant(name, description, pictureUrl))
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    Log.e(TAG, "Error parsing JSON", e)
                } finally {
                    swipeRefreshLayout.isRefreshing = false // Hide the refresh indicator
                }
            },
            { error ->
                Log.e(TAG, "Error fetching data", error)
                swipeRefreshLayout.isRefreshing = false // Hide the refresh indicator
            }) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }

        queue.add(jsonArrayRequest)
    }

    companion object {
        private const val TAG = "AcceuilFragment"

        @JvmStatic
        fun newInstance() = AcceuilFragment()
    }
}
