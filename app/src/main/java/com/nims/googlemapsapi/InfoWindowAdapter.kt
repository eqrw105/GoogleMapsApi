package com.nims.googlemapsapi

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.Marker

class InfoWindowAdapter(var context: Context) : GoogleMap.InfoWindowAdapter {

    override fun getInfoWindow(marker: Marker): View? {
        val view =
            (context as Activity).layoutInflater.inflate(R.layout.info_layout, null, false)
        view.findViewById<TextView>(R.id.textview).text = marker.title
        return view
    }

    override fun getInfoContents(marker: Marker): View? {
        return null
    }
}