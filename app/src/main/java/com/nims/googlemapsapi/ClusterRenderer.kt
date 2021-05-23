package com.nims.googlemapsapi

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class ClusterRenderer(context: Context, googleMap: GoogleMap?, clusterManager: ClusterManager<MyItem>?): DefaultClusterRenderer<MyItem>(context, googleMap, clusterManager) {

    init {
        clusterManager?.renderer = this
    }

    override fun onBeforeClusterItemRendered(item: MyItem, markerOptions: MarkerOptions) {
        markerOptions
            .icon(item.getIcon())
            .title(item.title)
    }

    override fun onClusterItemUpdated(item: MyItem, marker: Marker) {
        marker.setIcon(item.getIcon())
        marker.title = item.title
        marker.tag = "custom"
    }

    override fun onClusterItemRendered(item: MyItem, marker: Marker) {
        marker.setIcon(item.getIcon())
        marker.title = item.title
        marker.tag = "custom"
    }
}