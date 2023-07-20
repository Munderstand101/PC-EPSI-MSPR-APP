package dev.munderstand.pc_epsi_mspr_app.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.squareup.picasso.Picasso
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.MainActivity
import dev.munderstand.pc_epsi_mspr_app.activities.MyRequestsActivity
import dev.munderstand.pc_epsi_mspr_app.activities.common.ApiConfig
import org.json.JSONException
import org.json.JSONObject

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AccountFragment : Fragment() {

    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    private val items = mutableListOf<Plant>()
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: PlantAdapter

    private var accountId: String = ""

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = PlantAdapter(items)
        recyclerView.adapter = adapter

        val buttonMyRequests = view.findViewById<Button>(R.id.buttonMyRequests)

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

                val usernameTextView = view.findViewById<TextView>(R.id.text_user_name)
                val imageViewBotanist = view.findViewById<ImageView>(R.id.image_user)
                val textRole = view.findViewById<TextView>(R.id.textRole)

                val username = jsonObject.getString("username")
                val pictureUrl = jsonObject.getString("picture_url")

                // Set the account info to the respective TextViews
                usernameTextView.text = username.toString()
                textRole.text = "Role : Utilisateur normal"

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

        buttonMyRequests.setOnClickListener {
            val intent = Intent(activity?.applicationContext, MyRequestsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun fetchPlantes(user_id: String, token: String) {

        val queue = Volley.newRequestQueue(activity)
        val url = ApiConfig.PLANTS_ENDPOINT + user_id

        val jsonArrayRequest = object : JsonArrayRequest(
            Request.Method.GET, url, null,
            { response ->
                try {
                    items.clear() // Clear the previous data before adding new data
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val id = item.getInt("id")
                        val name = item.getString("name")
                        val description = item.getString("description")
                        val pictureUrl = ApiConfig.BASE_URL_PHOTOS + item.getString("photo")
                        items.add(Plant(id, name, description, pictureUrl))
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    Log.e(TAG, "Error parsing JSON", e)
                }
            },
            { error ->
                Log.e(TAG, "Error fetching data", error)
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
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Tab1Fragment.
         */
        // TODO: Rename and change types and number of parameters
        const val TAG = "AccountFragment"
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AccountFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


}
