package dev.munderstand.pc_epsi_mspr_app.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.google.maps.android.clustering.ClusterManager
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.munderstand.pc_epsi_mspr_app.R
import dev.munderstand.pc_epsi_mspr_app.activities.BotanistDetailsActivity
import dev.munderstand.pc_epsi_mspr_app.activities.MainActivity
import dev.munderstand.pc_epsi_mspr_app.activities.common.ApiConfig
import org.json.JSONException
import org.json.JSONObject

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap
    private lateinit var nearbyCodesLayout: RelativeLayout
    private lateinit var nearbyCodesListView: ListView
    private lateinit var clusterManager: ClusterManager<MarkerClusterItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        view.findViewById<View>(R.id.map_nearby_codes_button).setOnClickListener {
            val targetFragment = BotanistesFragment()
            val mainActivity = requireActivity() as MainActivity
            mainActivity.replaceFragment(targetFragment)
           // Toast.makeText(context, "map_nearby_codes_button", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
        setupMap()
        fetchBotanists()
    }

    private fun getBitmapFromVectorDrawable(drawableId: Int): Bitmap {
        val drawable = ContextCompat.getDrawable(requireContext(), drawableId)
        val bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return bitmap
    }

    private fun fetchBotanists() {
        val sharedPreferences = activity?.getSharedPreferences("account", Context.MODE_PRIVATE)
        val accountInfo = sharedPreferences?.getString("accountInfo", "")
        val token = sharedPreferences?.getString("token", "")

        val botanistsRequest = object : JsonArrayRequest(
            Request.Method.GET,
            ApiConfig.BOTANIST_ENDPOINT,
            null,
            Response.Listener { response ->
                try {
                    val markerClusterItems = mutableListOf<MarkerClusterItem>()

                    for (i in 0 until response.length()) {
                        val botanist = response.getJSONObject(i)
                        val name = botanist.getString("name")
                        val address = botanist.getString("address")
                        val longitude = botanist.getDouble("longitude")
                        val latitude = botanist.getDouble("latitude")
                        val location = LatLng(latitude, longitude)

                        val markerClusterItem = MarkerClusterItem(location, name, address)
                        markerClusterItems.add(markerClusterItem)
                    }

                    // Initialize cluster manager
                    clusterManager = ClusterManager(requireContext(), googleMap)
                    googleMap.setOnCameraIdleListener(clusterManager)
                    googleMap.setOnMarkerClickListener(clusterManager)

                    // Add cluster items to cluster manager
                    clusterManager.addItems(markerClusterItems)

                    // Set custom renderer for clustered markers
                    val clusterRenderer = CustomClusterRenderer(requireContext(), googleMap, clusterManager)
                    clusterManager.renderer = clusterRenderer

                    // Zoom camera to show all markers
                    val padding = resources.getDimensionPixelSize(R.dimen.map_padding)
                    val builder = LatLngBounds.Builder()

                    for (clusterItem in clusterManager.algorithm.items) {
                        builder.include(clusterItem.position)
                    }

                    val bounds = builder.build()
                    val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                    googleMap.moveCamera(cameraUpdate)

                } catch (e: JSONException) {
                    e.printStackTrace()
                    Toast.makeText(
                        requireContext(),
                        "Error parsing botanists data",
                        Toast.LENGTH_LONG
                    ).show()
                }
            },
            Response.ErrorListener { error ->
                //Log.e(
//                    MapFragment::class.java.simpleName,
//                    "Error fetching botanists",
//                    error
//                )
                Toast.makeText(
                    requireContext(),
                    "Error fetching botanists: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        ) {
            override fun getHeaders(): MutableMap<String, String> {
                val headers = HashMap<String, String>()
                headers["Authorization"] = "Bearer $token"
                return headers
            }
        }

        val queue = Volley.newRequestQueue(requireContext())
        queue.add(botanistsRequest)
    }

    @SuppressLint("MissingPermission")
    private fun setupMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as? SupportMapFragment
        mapFragment?.getMapAsync { googleMap ->
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                googleMap.isMyLocationEnabled = true
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }

            // Show info window when clicked on a marker
            googleMap.setOnMarkerClickListener { marker ->
                val botanist = marker.tag as? JSONObject
                if (botanist != null) {
                    val name = botanist.getString("name")
                    val specialization = botanist.getString("specialization")
                    val address = botanist.getString("address")
                    val pictureUrl = if (botanist.has("pictureUrl")) botanist.getString("pictureUrl") else ""

                    val intent = Intent(requireContext(), BotanistDetailsActivity::class.java)
                    intent.putExtra("name", name)
                    intent.putExtra("specialization", specialization)
                    intent.putExtra("address", address)
                    intent.putExtra("pictureUrl", pictureUrl)
                    startActivity(intent)
                }
                true
            }
        }
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }
}
