package dev.munderstand.pc_epsi_mspr_app.fragments

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import dev.munderstand.pc_epsi_mspr_app.R

class CustomClusterRenderer(
    private val context: Context,
    map: GoogleMap,
    clusterManager: ClusterManager<MarkerClusterItem>
) : DefaultClusterRenderer<MarkerClusterItem>(context, map, clusterManager) {

    override fun onBeforeClusterItemRendered(item: MarkerClusterItem, markerOptions: MarkerOptions) {
        val icon = getBitmapDescriptorFromVector(R.drawable.custom_marker_icon)
        markerOptions.icon(icon)
    }

    override fun shouldRenderAsCluster(cluster: Cluster<MarkerClusterItem>): Boolean {
        // Customize the condition for clustering items on the map
        return cluster.size > 1
    }

    private fun getBitmapDescriptorFromVector(vectorResId: Int): BitmapDescriptor {
        val drawable: Drawable? = ContextCompat.getDrawable(context, vectorResId)
        val bitmap: Bitmap = Bitmap.createBitmap(
            drawable!!.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)
    }
}
