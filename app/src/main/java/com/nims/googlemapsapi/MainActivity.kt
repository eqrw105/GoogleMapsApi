package com.nims.googlemapsapi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.webkit.PermissionRequest
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.ClusterRenderer
import com.google.maps.android.clustering.view.DefaultClusterRenderer

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var clusterManager: ClusterManager<MyItem>
    private lateinit var clusterRenderer: ClusterRenderer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val permissionChecker = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        //권한이 없으면 권한 요청
        if (permissionChecker == PackageManager.PERMISSION_GRANTED) {
            val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
            mapFragment.getMapAsync(this)
        }else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        val myLoc = LatLng(51.5145160, -0.1270060)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLoc, 15f))
        clusterManager = ClusterManager(this, mMap)
        clusterRenderer = ClusterRenderer(this, mMap, clusterManager)
        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener(clusterManager)
        val content = findViewById<ConstraintLayout>(R.id.content)
        val title = findViewById<TextView>(R.id.title)
        val snippet = findViewById<TextView>(R.id.snippet)
        val price = findViewById<TextView>(R.id.price)
        val ok_btn = findViewById<Button>(R.id.ok_btn)

        clusterManager.setOnClusterItemClickListener { item ->
            val animation = AnimationUtils.loadAnimation(this, R.anim.cafe_info_in)
            content.visibility = View.VISIBLE
            content.startAnimation(animation)
            title.text = item.title
            snippet.text = item.snippet
            price.text = item.getPrice().toString()
            ok_btn.setOnClickListener {
                Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show()
            }
            return@setOnClusterItemClickListener false
        }


        addItems()
    }

    private fun addItems(){
        var lat = 51.5145160
        var lng = -0.1270060

        for (i in 0..9) {
            val offset = i / 60.0
            lat += offset
            lng += offset
            val offsetItem = MyItem(lat, lng, "Title $i", "Snippet $i", 1000*i, BitmapDescriptorFactory.fromResource(android.R.drawable.ic_menu_mylocation))
            clusterManager.addItem(offsetItem)
        }
    }

    class ClusterRenderer(context: Context?, map: GoogleMap?, clusterManager: ClusterManager<MyItem>?): DefaultClusterRenderer<MyItem>(context, map, clusterManager) {

        init {
            clusterManager?.renderer = this
        }

        override fun onBeforeClusterItemRendered(item: MyItem, markerOptions: MarkerOptions) {
            markerOptions.icon(item.getIcon())
            markerOptions.visible(true)
        }
    }

    inner class MyItem(
        lat: Double,
        lng: Double,
        title: String,
        snippet: String,
        price: Int,
        icon: BitmapDescriptor
    ) : ClusterItem {

        private val position: LatLng
        private val title: String
        private val snippet: String
        private val price: Int
        private val icon: BitmapDescriptor

        override fun equals(other: Any?): Boolean {

            if(other is MyItem)
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

        init {
            position = LatLng(lat, lng)
            this.title = title
            this.snippet = snippet
            this.price = price
            this.icon = icon
        }
    }

}