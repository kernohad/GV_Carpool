package com.example.dndMobile.gvcarpool

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.app.ActivityCompat
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.places.ui.PlacePicker
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_radar.*
import kotlinx.android.synthetic.main.fragment_radar.view.*

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

        val fab = root!!.fab
        fab.setOnClickListener{
            loadPlacePicker()
        }

        return root
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gMap = googleMap


        //val allendale = LatLng(42.9639, -85.8889)
        //gMap.addMarker(MarkerOptions().position(allendale).title("Marker in Allendale"))
        //gMap.moveCamera(CameraUpdateFactory.newLatLng(allendale))
        //gMap.animateCamera(CameraUpdateFactory.zoomIn())
        //gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(allendale, 15.0f))

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
                placeMarker(currLatLon, "Current Location")
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currLatLon, 12f))
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == PLACE_PICKER_REQUEST){
            if(resultCode == RESULT_OK){
                val place = PlacePicker.getPlace(this.context, data)
                var addressText = place.name.toString()
                addressText +="\n" + place.address.toString()

                placeMarker(place.latLng, addressText)
                gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.latLng, 12f))
            }
        }
    }

    private fun placeMarker(location: LatLng, name: String){
        val markerOptions = MarkerOptions().position(location).title(name)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(
                BitmapDescriptorFactory.HUE_AZURE
        ))
        gMap.addMarker(markerOptions)
    }

    private fun loadPlacePicker() {
        val builder = PlacePicker.IntentBuilder()

        try{
            startActivityForResult(builder.build(this.activity), PLACE_PICKER_REQUEST)
        }catch(e: GooglePlayServicesRepairableException){
            e.printStackTrace()
        }catch(e: GooglePlayServicesNotAvailableException){
            e.printStackTrace()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val PLACE_PICKER_REQUEST = 3
    }

}
