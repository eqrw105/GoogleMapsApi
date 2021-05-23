package com.nims.googlemapsapi

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.Marker
import com.google.maps.android.clustering.ClusterManager

class MainActivityViewModel: ViewModel() {
    private var _visiblity: MutableLiveData<Boolean>
    private var _title: MutableLiveData<String>
    private var _snippet: MutableLiveData<String>
    private var _price: MutableLiveData<Int>
    private var _myLocationEnabled: MutableLiveData<Boolean>

    init {
        _visiblity = MutableLiveData()
        _title = MutableLiveData()
        _snippet = MutableLiveData()
        _price = MutableLiveData()
        _myLocationEnabled = MutableLiveData()
    }

    val visiblity: LiveData<Boolean> get() {
        return this._visiblity
    }

    val title: LiveData<String> get() {
        return this._title
    }

    val snippet: LiveData<String> get() {
        return this._snippet
    }

    val price: LiveData<Int> get() {
        return this._price
    }

    val myLocationEnabled: LiveData<Boolean> get() {
        return this._myLocationEnabled
    }

    fun updateVisivility(boolean: Boolean){
        _visiblity.value = boolean
    }

    fun updateTitle(string: String?){
        _title.value = string
    }

    fun updateSnippet(string: String?){
        _snippet.value = string
    }

    fun updatePrice(int: Int){
        _price.value = int
    }

    fun updateMyLocationEnabled(boolean: Boolean){
        _myLocationEnabled.value = boolean
    }


}