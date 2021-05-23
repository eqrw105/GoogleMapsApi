package com.nims.googlemapsapi

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import kotlinx.android.synthetic.main.activity_main.view.*

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var clusterManager: ClusterManager<MyItem>
    private lateinit var clusterRenderer: ClusterRenderer
    private var selectedMarker: Marker? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun enableMyLocation(){
        val permissionChecker =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        //권한이 없으면 권한 요청
        if (permissionChecker == PackageManager.PERMISSION_GRANTED) {
            if(mMap != null) mMap.isMyLocationEnabled = true
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                0
            )
        }
    }

    //초기 위치 설정
    private fun getLastLocation(){
        val permissionChecker =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

        //권한이 없으면 권한 요청
        if (permissionChecker == PackageManager.PERMISSION_GRANTED) {
            LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener { location ->
                try {
                    val lat = location.latitude
                    val lng = location.longitude
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 17.5f))
                }catch (e: Exception){
                    e.printStackTrace()
                    val lat = 37.5542901
                    val lng = 126.9874977

                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 13f))
                }
            }
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                0
            )
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap


        enableMyLocation()
        getLastLocation()
        clusterManager = ClusterManager(this, mMap)
        clusterRenderer = ClusterRenderer(this, mMap, clusterManager)
        mMap.setOnCameraIdleListener(clusterManager)
        mMap.setOnMarkerClickListener(clusterManager)

        //mMap.setInfoWindowAdapter(clusterManager.markerManager)
        val infoWindowAdapter = InfoWindowAdapter(this)
        clusterManager.markerCollection.setInfoWindowAdapter(infoWindowAdapter)
        val content = findViewById<ConstraintLayout>(R.id.content)
        val title = findViewById<TextView>(R.id.title)
        val snippet = findViewById<TextView>(R.id.snippet)
        val price = findViewById<TextView>(R.id.price)

        //다른곳 클릭 시 하단 정보창 숨기기, 마커 unActive Icon으로 변경
        mMap.setOnMapClickListener {
            content.visibility = View.GONE
            if(selectedMarker?.tag == "custom") selectedMarker?.setIcon(clusterRenderer.getClusterItem(selectedMarker).getIcon())
        }

        //마커 클릭 시 하단 정보창 보이기(투명도 애니메이션), 마커 Active Icon으로 변경
        clusterManager.setOnClusterItemClickListener { item ->

            val marker = clusterRenderer.getMarker(item)
            if (selectedMarker == null) selectedMarker = marker
            if(marker.tag == "custom") {
                if(selectedMarker!!.tag == "custom") selectedMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_cafe_location))
                marker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_cafe_active))
            }
            selectedMarker = marker

            val animation = AnimationUtils.loadAnimation(this, R.anim.cafe_info_in)
            content.visibility = View.VISIBLE
            content.startAnimation(animation)
            title.text = item.title
            snippet.text = item.snippet
            price.text = item.getPrice().toString()
            content.setOnClickListener { Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show() }
            return@setOnClusterItemClickListener false
        }

        clusterRenderer.setOnClusterItemInfoWindowClickListener {
            Log.d("eee", it.title.toString())
        }

        addItems()

    }


    private fun addItems() {
        var lat = 37.5004383
        var lng = 127.0328307
        for (i in 1..30) {
            val offset = i / 6000.0
            lat += offset
            lng += offset
            val offsetItem = MyItem(
                lat,
                lng,
                "Title $i",
                "Snippet $i",
                1000 * i,
                BitmapDescriptorFactory.fromResource(R.drawable.map_cafe_location)
            )
            clusterManager.addItem(offsetItem)
        }
    }

    class ClusterRenderer(context: Context, googleMap: GoogleMap, clusterManager: ClusterManager<MyItem>): DefaultClusterRenderer<MyItem>(context, googleMap, clusterManager) {

        init {
            clusterManager.renderer = this
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

}