package com.example.dndMobile.gvcarpool

import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Location
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions

class RadarFragment : Fragment(), OnMapReadyCallback {

    //the view
    var root: View? = null

    //The map
    private lateinit var gMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_radar, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.activity!!)

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap

        //TODO: replace allendale with device's current location.
        val allendale = LatLng(42.9639, -85.8889)
        gMap.addMarker(MarkerOptions().position(allendale).title("Marker in Allendale"))
        gMap.moveCamera(CameraUpdateFactory.newLatLng(allendale))
        gMap.animateCamera(CameraUpdateFactory.zoomIn())
        gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(allendale, 15.0f))

        setUpMap()
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this.context!!,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity!!,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }

        gMap.isMyLocationEnabled = true
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->

            if(location != null){
                lastLocation = location
                val currLatLon = LatLng(location.latitude, location.longitude)
                placeMarker(currLatLon)
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLatLon, 12f))
            }
        }
    }

    private fun placeMarker(location: LatLng){
        val markerOptions = MarkerOptions().position(location)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_AZURE
        ))
        gMap.addMarker(markerOptions)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

}
