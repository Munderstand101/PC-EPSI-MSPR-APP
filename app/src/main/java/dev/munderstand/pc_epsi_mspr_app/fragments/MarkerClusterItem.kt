package dev.munderstand.pc_epsi_mspr_app.fragments

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class MarkerClusterItem(
    private val position: LatLng,
    private val name: String,
    private val address: String
) : ClusterItem {

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String {
        return name
    }

    override fun getSnippet(): String {
        return address
    }

    override fun getZIndex(): Float? {
        TODO("Not yet implemented")
    }
}
