package com.nims.googlemapsapi

import android.nfc.Tag
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class MyItem(
    private var lat: Double,
    private var lng: Double,
    private var title: String,
    private var snippet: String,
    private var price: Int,
    private var icon: BitmapDescriptor,
) : ClusterItem {

    private var position: LatLng

    init {
        this.position = LatLng(lat, lng)
    }

    override fun equals(other: Any?): Boolean {

        if (other is MyItem)
            return (other.position.latitude == position.latitude
                    && other.position.longitude == position.longitude
                    && other.title == title
                    && other.snippet == snippet
                    && other.price == price)

        return true
    }

    override fun hashCode(): Int {
        var hash = position.latitude.hashCode() * 31

        hash = hash * 31 + position.longitude.hashCode()
        hash = hash * 31 + title.hashCode()
        hash = hash * 31 + snippet.hashCode()
        hash = hash * 31 + price.hashCode()

        return hash
    }

    override fun getPosition(): LatLng {
        return position
    }

    override fun getTitle(): String? {
        return title
    }

    override fun getSnippet(): String? {
        return snippet
    }

    fun getPrice(): Int {
        return price
    }

    fun getIcon(): BitmapDescriptor {
        return icon
    }
}