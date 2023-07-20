package dev.munderstand.pc_epsi_mspr_app.fragments

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.common.ApiConfig
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BotanistesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BotanistesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: BotanistAdapter
    private val items = mutableListOf<Botanist>()
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_botanistes, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rcv_products)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = BotanistAdapter(items)
        recyclerView.adapter = adapter

      //  fetchOffers

        val sharedPreferences = activity?.getSharedPreferences("account", Context.MODE_PRIVATE)
        val accountInfo = sharedPreferences?.getString("accountInfo", "")
        // Retrieve the token from SharedPreferences
        val token = sharedPreferences?.getString("token", "")

        val jsonObject = JSONObject(accountInfo.toString())
        val accountId = jsonObject.getInt("id").toString()
        fetchBotanistes( token.toString())
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            // Refresh data when the user swipes down
            fetchBotanistes( token.toString())
        }
    }


    private fun fetchBotanistes(token: String) {
        val queue = Volley.newRequestQueue(activity)
        val url = ApiConfig.BOTANIST_ENDPOINT

        val jsonArrayRequest = object : JsonArrayRequest(
            Method.GET, url, null,
            { response ->
                try {
                    items.clear() // Clear the previous data before adding new data
                    for (i in 0 until response.length()) {
                        val item = response.getJSONObject(i)
                        val id = item.getInt("id")
                        val name = item.getString("name")
                        val specialization = item.getString("specialization")
                        val address = item.getString("address")
                        val zipcode = item.getString("zipcode")
                        val city = item.getString("city")
                        val longitude = item.getDouble("longitude")
                        val latitude = item.getDouble("latitude")
                        val pictureUrl = item.getString("picture_url")

                        val botanist = Botanist(
                            id,
                            name,
                            specialization,
                            address,
                            zipcode,
                            city,
                            longitude,
                            latitude,
                            pictureUrl
                        )
                        items.add(botanist)
                    }
                    adapter.notifyDataSetChanged()
                } catch (e: JSONException) {
                    //Log.e(TAG, "Error parsing JSON", e)
                }
                finally {
                    // Hide the refresh indicator
                    swipeRefreshLayout.isRefreshing = false
                }
            },
            { error ->
                //Log.e(TAG, "Error fetching data", error)
                // Hide the refresh indicator
                swipeRefreshLayout.isRefreshing = false
            }
        ) {
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
         * @return A new instance of fragment BotanistesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BotanistesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }

        private const val TAG = "BotanistFragment"
    }
}