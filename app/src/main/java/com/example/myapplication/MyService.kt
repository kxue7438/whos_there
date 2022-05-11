package com.example.myapplication

import android.Manifest
import android.R
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot


class MyService : Service() {
    private lateinit var mLocationManager: LocationManager
    private lateinit var mLocationListener: LocationListener
    lateinit var manager: NotificationManager
    private var db = FirebaseFirestore.getInstance()
    var currentLocation: Location? = null
    private var counter = 0
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O) createNotificationChanel() else startForeground(
            1,
            Notification()
        )
        mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        mLocationListener = createLocationListener()
        mAuth = FirebaseAuth.getInstance()
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED) {
            return
        } else {
            installLocationListeners()
        }
    }
    override fun onBind(intent: Intent): IBinder {
        throw UnsupportedOperationException("Not yet implemented")
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        return START_STICKY
    }
    override fun onDestroy() {
        super.onDestroy()
        val broadcastIntent = Intent()
        broadcastIntent.action = "restartservice"
        broadcastIntent.setClass(this, RestartBackgroundService::class.java)
        this.sendBroadcast(broadcastIntent)
    }
    private fun createNotificationChanel() {
        val NOTIFICATION_CHANNEL_ID = "com.example.myapplication"
        val channelName = "Background Service"
        val chan = NotificationChannel(
            NOTIFICATION_CHANNEL_ID,
            channelName,
            NotificationManager.IMPORTANCE_NONE
        )
        chan.lightColor = Color.BLUE
        chan.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
        manager =
            (getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
        manager.createNotificationChannel(chan)
        val notificationBuilder =
            NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
        val notification: Notification = notificationBuilder.setOngoing(true)
            .setContentTitle("App is running count::" + counter)
            .setPriority(NotificationManager.IMPORTANCE_MIN)
            .setCategory(Notification.CATEGORY_SERVICE)
            .build()
        startForeground(2, notification)
    }
    private fun installLocationListeners() {

        //register for further location updates

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            return
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
    private fun createLocationListener(): LocationListener {
        return object : LocationListener {
            // callback executed on location change
            override fun onLocationChanged(location: Location) {
                Log.i(MyService.TAG, "Received new location$location")

                //Determine whether new location is better than current best estimate
                if (currentLocation == null || location.accuracy <= currentLocation!!.accuracy) {

                    // Update best estimate
                    currentLocation = location
                    MapsActivity.currLocation = currentLocation

                    //update database
                    val coord = hashMapOf(
                        "lat" to currentLocation!!.latitude,
                        "long" to currentLocation!!.longitude
                    )
                    val currentUser = mAuth.currentUser
                    val userDoc = db.collection("Users").document(currentUser!!.email!!)
                    userDoc
                        .update("coords", coord)
                        .addOnSuccessListener { Log.d(TAG, "DocumentSnapshot successfully updated!") }
                        .addOnFailureListener { e -> Log.w(TAG, "Error updating document", e) }
                    //start retrieving the other people's locations
                    val localDB=getSharedPreferences("myPref", Context.MODE_PRIVATE)
                    var contactSet = localDB.getStringSet("contactsKey",HashSet<String>()) as HashSet<String>
                    var taskSet:ArrayList<Task<DocumentSnapshot>>
                    taskSet=ArrayList<Task<DocumentSnapshot>>()
                    for(contact in contactSet){
                        taskSet.add(db.collection("Users").document(contact.toString()).get())
                    }
                    var combinedTask= Tasks.whenAllSuccess<DocumentSnapshot>(taskSet)
                        .addOnSuccessListener { documentList ->
                            for (doc in documentList) {
                                val coords=doc.data!!.get("coords") as HashMap<String,Double>
                                val lat:Double = coords.get("lat")!!.toDouble()
                                val long:Double = coords.get("long")!!.toDouble()
                                val name = doc.data!!.get("name") as String
                                val matrix = floatArrayOf(3F)
                                val localDB=getSharedPreferences("myPref", Context.MODE_PRIVATE)
                                var str=localDB.getString("exclusionKey","") as String
                                var bool = true
                                if(str.length>0) {
                                    var splitList = str.split(",")
                                    val submatrix = floatArrayOf(3F)
                                    for (coordinate_set in splitList) {
                                        val exclusion_coord: List<String>
                                        exclusion_coord = coordinate_set.split(" ")
                                        val currDist = Location.distanceBetween(
                                            currentLocation!!.latitude,
                                            currentLocation!!.longitude,
                                            exclusion_coord[0].toDouble(),
                                            exclusion_coord[1].toDouble(),
                                            submatrix
                                        )
                                        if (submatrix[0] <= exclusion_coord[2].toDouble()) {
                                            bool = false
                                        }
                                    }
                                }
                                Location.distanceBetween(currentLocation!!.latitude,currentLocation!!.longitude,
                                lat,long,matrix)
                                //dist is in meters
                                if(matrix[0]<1000&&bool){
                                Log.i("test","if it did")
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                        val notificationChannel = NotificationChannel(
                                            CHANNEL_ID,
                                            CHANNEL_NAME,
                                            // Change importance
                                            NotificationManager.IMPORTANCE_LOW
                                        )

                                        notificationChannel.enableLights(true)
                                        notificationChannel.lightColor = Color.RED
                                        notificationChannel.enableVibration(true)
                                        notificationChannel.description = "friend near"

                                        manager.createNotificationChannel(notificationChannel)
                                        val builder = NotificationCompat.Builder(
                                            applicationContext,CHANNEL_ID
                                        )
                                            .setSmallIcon(R.drawable.sym_def_app_icon)
                                            .setContentTitle("Who is There")
                                            .setContentText("$name is less than 1000 meters away")
                                        manager.notify(1234, builder.build())
                                    }
                                }
                            }

                        }
                }
            }
        }
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
        private const val TAG = "LocationService"
        private val CHANNEL_ID = "com.example.myapplication"
        private val CHANNEL_NAME = "NOTI"
    }
}