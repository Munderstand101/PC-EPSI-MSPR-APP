package dev.munderstand.pc_epsi_mspr_app.fragments
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.android.volley.Request
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.NewPlantActivity
import dev.munderstand.pc_epsi_mspr_app.activities.common.ApiConfig
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private const val TAG = "PlantesFragment"

class PlantesFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: PlantAdapter
    private val items = mutableListOf<Plant>()
    private lateinit var capturePhotoButton: View
    private val REQUEST_IMAGE_CAPTURE = 1
    private lateinit var currentPhotoPath: String
    private val REQUEST_CAMERA_PERMISSION = 2
    private lateinit var addPlantButton: View
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
        return inflater.inflate(R.layout.fragment_plantes, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.rcv_products)
        recyclerView.layoutManager = LinearLayoutManager(activity)

        adapter = PlantAdapter(items)
        recyclerView.adapter = adapter



        addPlantButton = view.findViewById(R.id.iv_add)
        addPlantButton.setOnClickListener {
            val intent = Intent(requireContext(), NewPlantActivity::class.java)
            startActivity(intent)
        }

        val sharedPreferences = activity?.getSharedPreferences("account", Context.MODE_PRIVATE)
        val accountInfo = sharedPreferences?.getString("accountInfo", "")
        // Get the SharedPreferences instance
//        val sharedPreferences = getSharedPreferences("account", Context.MODE_PRIVATE)

        // Retrieve the token from SharedPreferences
        val token = sharedPreferences?.getString("token", "")

        val jsonObject = JSONObject(accountInfo.toString())
        val accountId = jsonObject.getInt("id").toString()
        fetchPlantes(accountId, token.toString())
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout)
        swipeRefreshLayout.setOnRefreshListener {
            // Refresh data when the user swipes down
            fetchPlantes(accountId, token.toString())
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, proceed with the camera operation
            } else {
                // Camera permission denied, handle accordingly (e.g., show a message)
            }
        }
    }

    private fun fetchPlantes(user_id: String, token: String) {
        val queue = Volley.newRequestQueue(activity)
        val url = ApiConfig.PLANTS_ENDPOINT + user_id

        val jsonArrayRequest = object : JsonArrayRequest(
            Method.GET, url, null,
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
                } finally {
                    // Hide the refresh indicator
                    swipeRefreshLayout.isRefreshing = false
                }
            },
            { error ->
                Log.e(TAG, "Error fetching data", error)
                // Hide the refresh indicator
                swipeRefreshLayout.isRefreshing = false
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
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            PlantesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}
