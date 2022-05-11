package com.example.myapplication

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.example.myapplication.databinding.ActivityMapsBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "Network location updates requested")
        mAuth = FirebaseAuth.getInstance()

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    override fun onStart() {
        super.onStart()

        val currentUser = mAuth.currentUser
        if(currentUser == null) {
            Toast.makeText(applicationContext, "not logged in", Toast.LENGTH_LONG).show()
        } else {
            Toast.makeText(applicationContext, currentUser.displayName, Toast.LENGTH_LONG).show()
        }
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),REQUEST_FINE_LOC_PERM_ONCREATE)
        } else {
            updateDisplay(currLocation!!)
        }
    }

    // Update display
    private fun updateDisplay(location: Location) {
        val curr = LatLng(location.latitude, location.longitude)
        mMap.clear()

        mMap.addMarker(MarkerOptions().position(curr).title("Current Location"))
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(curr, 10F))
    }



    companion object{
        private const val ONE_MIN = 1000 * 60.toLong()
        private const val TWO_MIN = ONE_MIN * 2
        private const val MEASURE_TIME = TWO_MIN
        private const val POLLING_FREQ = 1000 * 10.toLong()
        private const val MIN_ACCURACY = 5.0f
        private const val MIN_DISTANCE = 5.0f
        private const val REQUEST_FINE_LOC_PERM_ONCREATE = 200
        private const val REQUEST_FINE_LOC_PERM_ONRESUME = 201
        private const val TAG = "LocationGetLocation"
        var currLocation: Location? = null
    }
}