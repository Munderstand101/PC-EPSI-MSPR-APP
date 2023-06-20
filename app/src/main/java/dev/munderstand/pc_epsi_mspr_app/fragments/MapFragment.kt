package dev.munderstand.pc_epsi_mspr_app.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import dev.munderstand.pc_epsi_mspr_app.R
import org.json.JSONObject

private const val STORES_URL = "https://www.ugarit.online/epsi/stores.json"

class MapFragment : Fragment(), OnMapReadyCallback {

    private lateinit var googleMap: GoogleMap

    private lateinit var nearbyCodesLayout: RelativeLayout
    private lateinit var nearbyCodesListView: ListView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.google_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        nearbyCodesLayout = view.findViewById(R.id.nearby_codes_relative_layout)
        nearbyCodesListView = view.findViewById(R.id.nearby_codes_listview)

        view.findViewById<FloatingActionButton>(R.id.map_nearby_codes_button).setOnClickListener {
            showNearbyCodesLayout()
        }

        view.findViewById<Button>(R.id.close_nearby_button).setOnClickListener {
            hideNearbyCodesLayout()
        }

        return view
    }

    override fun onMapReady(googleMap: GoogleMap) {
        this.googleMap = googleMap
     //   setupMap()
        fetchStores()
    }

//    @SuppressLint("MissingPermission")
    private fun setupMap() {
        // Get the GoogleMap instance and perform necessary setup
        val mapFragment = childFragmentManager.findFragmentById(R.id.google_map) as? SupportMapFragment
        mapFragment?.getMapAsync { googleMap ->
            // Check if the location permission is granted
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                // Enable the "My Location" feature
                googleMap.isMyLocationEnabled = true
                // Additional map setup
                // ...
            } else {
                // Request the location permission
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ),
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }


    private fun fetchStores() {
        val storesRequest = JsonObjectRequest(
            Request.Method.GET,
            STORES_URL,
            null,
            { response ->
                val stores = response.getJSONArray("stores")
                val builder = LatLngBounds.Builder()

                for (i in 0 until stores.length()) {
                    val store = stores.getJSONObject(i)
                    val name = store.getString("name")
                    val address = store.getString("address")
                    val longitude = store.getDouble("longitude")
                    val latitude = store.getDouble("latitude")
                    val location = LatLng(latitude, longitude)
                    val marker = googleMap.addMarker(
                        MarkerOptions().position(location).title(name).snippet(address)
                    )
                    marker?.tag = store
                    builder.include(location)

                    googleMap.setOnInfoWindowClickListener { clickedMarker ->
                        val store = clickedMarker.tag as? JSONObject
                        if (store != null) {
//                            val intent = Intent(requireContext(), StoreDetailsActivity::class.java)
//                            intent.putExtra("store", store.toString())
//                            startActivity(intent)
                        }
                    }
                    marker?.showInfoWindow()
                }

                val bounds = builder.build()
                val padding = resources.getDimensionPixelSize(R.dimen.map_padding)
                val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
                googleMap.moveCamera(cameraUpdate)
            },
            { error ->
                Toast.makeText(
                    requireContext(),
                    "Error fetching stores: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )

        val queue = Volley.newRequestQueue(requireContext())
        queue.add(storesRequest)
    }

    private fun showNearbyCodesLayout() {
        nearbyCodesLayout.visibility = View.VISIBLE
    }

    private fun hideNearbyCodesLayout() {
        nearbyCodesLayout.visibility = View.GONE
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1001
    }

}
