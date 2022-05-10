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
    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "Network location updates requested")
        mAuth = FirebaseAuth.getInstance()

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (null != mLocationManager) {
            Log.i(TAG, "Couldn't find the LocationManager")
            // Return a LocationListener
        }
        mLocationListener = createLocationListener()
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun createLocationListener(): LocationListener {
        return object : LocationListener {
            // callback executed on location change
            override fun onLocationChanged(location: Location) {
                Log.i(TAG, "Received new location$location")

                //Determine whether new location is better than current best estimate
                if (currentLocation == null || location.accuracy <= currentLocation!!.accuracy) {

                    // Update best estimate
                    currentLocation = location

                    // Update display
                    updateDisplay(location)

                }
            }
        }
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
    private fun getAndDisplayLastKnownLocation(): Location? {

        // Get best last location measurement
        currentLocation = bestLastKnownLocation()

        // Display last reading information
        if (null != currentLocation) {
            updateDisplay(currentLocation!!)
        } else {
            val cp = LatLng(-38.9897, -76.9378)
            mMap.addMarker(MarkerOptions().position(cp).title("Marker in College Park"))
            mMap.moveCamera(CameraUpdateFactory.newLatLng(cp))
        }

        // Return best reading or null
        return currentLocation
    }
    @SuppressLint("MissingPermission")
    private fun bestLastKnownLocation(): Location? {
        var bestResult: Location? = null
        var bestAccuracy = Float.MAX_VALUE
        var bestAge = Long.MIN_VALUE
        val matchingProviders =
            mLocationManager.allProviders
        for (provider in matchingProviders) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                val location =
                    mLocationManager.getLastKnownLocation(provider)
                if (location != null) {
                    val accuracy = location.accuracy
                    val time = location.time
                    if (accuracy < bestAccuracy) {
                        bestResult = location
                        bestAccuracy = accuracy
                        bestAge = time
                    }
                }
            }
        }

        // Return best reading or null
        return if (bestAccuracy > MIN_ACCURACY || System.currentTimeMillis() - bestAge > TWO_MIN) {
            null
        } else {
            bestResult
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
            getAndDisplayLastKnownLocation()
            installLocationListeners()
        }
        // Add a marker in Sydney and move the camera
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))
    }

    // Update display
    private fun updateDisplay(location: Location) {
        val curr = LatLng(location.latitude, location.longitude)
        mMap.clear()
        mMap.addMarker(MarkerOptions().position(curr).title("Current Location"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(curr))
    }

    private fun installLocationListeners() {

        //register for further location updates

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                REQUEST_FINE_LOC_PERM_ONRESUME
            )
        } else {
            continueInstallLocationListeners()
        }
    }

    @SuppressLint("MissingPermission")
    private fun continueInstallLocationListeners() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            // Register for network location updates
            if (null != mLocationManager.getProvider(LocationManager.NETWORK_PROVIDER)) {
                Log.i(TAG, "Network location updates requested")
                mLocationManager.requestLocationUpdates(
                    LocationManager.NETWORK_PROVIDER,
                    POLLING_FREQ,
                    MIN_DISTANCE,
                    mLocationListener
                )
            }

            // Register for GPS location updates
            if (null != mLocationManager.getProvider(LocationManager.GPS_PROVIDER)) {
                Log.i(TAG, "GPS location updates requested")
                mLocationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    POLLING_FREQ,
                    MIN_DISTANCE,
                    mLocationListener
                )
            }
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (REQUEST_FINE_LOC_PERM_ONCREATE == requestCode) {
                getAndDisplayLastKnownLocation()
                installLocationListeners()
            } else if (REQUEST_FINE_LOC_PERM_ONRESUME == requestCode) {
                continueInstallLocationListeners()
            }
        } else {
            Toast.makeText(
                this,
                "This app requires ACCESS_FINE_LOCATION permission",
                Toast.LENGTH_LONG
            ).show()
        }
    }
    companion object{
        const val PERMISSION_REQUEST_ACCESS_LOCATION=100
        const val granted = PackageManager.PERMISSION_GRANTED
        const val internet = Manifest.permission.INTERNET
        const val fine = Manifest.permission.ACCESS_FINE_LOCATION
        private const val ONE_MIN = 1000 * 60.toLong()
        private const val TWO_MIN = ONE_MIN * 2
        private const val MEASURE_TIME = TWO_MIN
        private const val POLLING_FREQ = 1000 * 10.toLong()
        private const val MIN_ACCURACY = 5.0f
        private const val MIN_DISTANCE = 5.0f
        private const val REQUEST_FINE_LOC_PERM_ONCREATE = 200
        private const val REQUEST_FINE_LOC_PERM_ONRESUME = 201
        private var mFirstUpdate = true
        private const val TAG = "LocationGetLocation"
    }
}