package com.nims.googlemapsapi

import android.Manifest
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
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.maps.android.clustering.ClusterManager
import java.security.Permission

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mMap: GoogleMap? = null
    private lateinit var clusterManager: ClusterManager<MyItem>
    private lateinit var clusterRenderer: ClusterRenderer
    private var selectedMarker: Marker? = null
    private lateinit var viewModel: MainActivityViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        val content = findViewById<ConstraintLayout>(R.id.content)
        val title = findViewById<TextView>(R.id.title)
        val snippet = findViewById<TextView>(R.id.snippet)
        val price = findViewById<TextView>(R.id.price)

        viewModel = ViewModelProvider(this)[MainActivityViewModel::class.java]
        viewModel.visiblity.observe(this, Observer { result ->
            if(result) {
                content.visibility = View.VISIBLE
                val animation = AnimationUtils.loadAnimation(this, R.anim.cafe_info_in)
                content.startAnimation(animation)
            }
            else content.visibility = View.GONE
        })

        viewModel.title.observe(this, Observer { result ->
            title.text = result
        })

        viewModel.snippet.observe(this, Observer { result ->
            snippet.text = result
        })

        viewModel.price.observe(this, Observer { result ->
            price.text = result.toString()
        })

        viewModel.myLocationEnabled.observe(this, Observer { result ->
            if (result) {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    if(mMap != null) mMap?.isMyLocationEnabled = result
                }else {
                    viewModel.updateMyLocationEnabled(false)
                }
            }
        })

        content.setOnClickListener { Toast.makeText(this, viewModel.title.value, Toast.LENGTH_SHORT).show() }
    }

    //초기 위치 설정
    private fun getLastLocation() {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnSuccessListener { location ->
                    try {
                        val lat = location.latitude
                        val lng = location.longitude
                        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 17.5f))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        val lat = 37.5542901
                        val lng = 126.9874977

                        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(lat, lng), 13f))
                    }
                }
                return
            }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        viewModel.updateMyLocationEnabled(true)
        getLastLocation()
        setupCluster(mMap!!)
    }

    fun setupCluster(map: GoogleMap){
        clusterManager = ClusterManager(this , map)
        clusterRenderer = ClusterRenderer(this, map, clusterManager)
        map.setOnCameraIdleListener(clusterManager)
        map.setOnMarkerClickListener(clusterManager)
        val infoWindowAdapter = InfoWindowAdapter(this)
        clusterManager.markerCollection.setInfoWindowAdapter(infoWindowAdapter)

        //다른곳 클릭 시 하단 정보창 숨기기, 마커 unActive Icon으로 변경
        map.setOnMapClickListener {
            viewModel.updateVisivility(false)
            if(selectedMarker != null) initSelectedMarker()
        }

        //마커 클릭 시 하단 정보창 보이기(투명도 애니메이션), 마커 Active Icon으로 변경
        clusterManager.setOnClusterItemClickListener { item ->
            val marker = clusterRenderer.getMarker(item)
            updateSelectedMarker(marker)
            viewModel.updateVisivility(true)
            viewModel.updateTitle(item.title)
            viewModel.updateSnippet(item.snippet)
            viewModel.updatePrice(item.getPrice())
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

    fun updateSelectedMarker(newMarker: Marker){
        if(selectedMarker == null) selectedMarker = newMarker
        if(newMarker.tag == "custom") {
            if(selectedMarker?.tag == "custom") selectedMarker?.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.map_cafe_location))
            newMarker.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.icon_cafe_active))
        }
        selectedMarker = newMarker
    }

    fun initSelectedMarker(){
        if(selectedMarker != null && selectedMarker?.tag == "custom") {
            val item = clusterRenderer.getClusterItem(selectedMarker)
            selectedMarker?.setIcon(item.getIcon())
        }
    }




}