package com.example.myapplication

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    lateinit var names: ArrayList<contactsObject>
    lateinit var recyclerView: RecyclerView
    private var db = FirebaseFirestore.getInstance()
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        setContacts()


        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION), REQ)
        }

        var contactTest=findViewById<BottomNavigationView>(R.id.bottom_navigation)
        contactTest.setOnItemSelectedListener{ bottom_navigation ->
            when(bottom_navigation.itemId) {
                R.id.contactsB ->true
                R.id.profileB ->navBarButton(1)
                R.id.searchB ->navBarButton(2)
                R.id.settingsB ->navBarButton(3)
            }
            true
        }
    }

    override fun onStart() {
        super.onStart()
        if(checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION), BACK)
        }else{
            startForegroundService(Intent(applicationContext, MyService::class.java))
        }
    }

    fun navBarButton(int:Int){
        //0->contacts
        //1->profile
        //2->search
        //3->settings
        lateinit var intent:Intent
        when(int){
            0->intent=Intent(applicationContext,MainActivity::class.java)
            1->intent=Intent(applicationContext,profileAct::class.java)
            2->intent=Intent(applicationContext,searchAct::class.java)
            else->intent=Intent(applicationContext,settingsAct::class.java)
        }
        startActivity(intent)
    }
    fun setContacts(){
        names= ArrayList<contactsObject>()
        recyclerView=findViewById(R.id.recyclerView)
        dataInput()
    }
    fun setContactsStage2(){
        var adapter:recyclerAdapter = recyclerAdapter(names,applicationContext)
        var layoutMgr:RecyclerView.LayoutManager= LinearLayoutManager(applicationContext)
        recyclerView.layoutManager=layoutMgr
        recyclerView.itemAnimator= DefaultItemAnimator()
        recyclerView.adapter=adapter
    }
    fun dataInput(){
        val localDB=getSharedPreferences("myPref", Context.MODE_PRIVATE)
        var contactSet = localDB.getStringSet("contactsKey",HashSet<String>()) as HashSet<String>
        var taskSet:ArrayList<Task<DocumentSnapshot>>
        taskSet=ArrayList<Task<DocumentSnapshot>>()
        for(contact in contactSet){
            taskSet.add(db.collection("Users").document(contact.toString()).get())
        }
        val currentUser = mAuth.currentUser

        var combinedTask= Tasks.whenAllSuccess<DocumentSnapshot>(taskSet)
            .addOnSuccessListener { documentList ->
                for (doc in documentList) {
                    val firestoreUser = doc.data!!.get("name") as String
                    val coords=doc.data!!.get("coords") as HashMap<String,Double>
                    val lat:Double = coords.get("lat")!!.toDouble()
                    val long:Double = coords.get("long")!!.toDouble()
                    db.collection("Users").document(currentUser!!.email!!).get()
                        .addOnSuccessListener { document->
                            val coords=document.data!!.get("coords") as HashMap<String,Double>
                            val latitude:Double = coords.get("lat")!!.toDouble()
                            val longitude:Double = coords.get("long")!!.toDouble()
                            val matrix = floatArrayOf(3F)
                            Location.distanceBetween(latitude,longitude,
                                lat,long,matrix)
                            names.add(contactsObject("$firestoreUser", matrix[0].toInt()))
                            setContactsStage2()
                    }
                }

            }
    }

    fun test(int:Int):Boolean{
        Log.i("test$int", "test$int")

        overridePendingTransition(0,0)
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQ ->
                // Checking whether user granted the permission or not.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Showing the toast message
                    Toast.makeText(this, "Location Service Permission Granted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Location Service Permission Denied", Toast.LENGTH_SHORT).show();
                }
            BACK ->
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Showing the toast message
                    startForegroundService(Intent(applicationContext, MyService::class.java))
                    Toast.makeText(this, "Background Location Service Permission Granted", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "Background Location Service Permission Denied", Toast.LENGTH_SHORT).show();
                }
        }

    }
    companion object{
        private val REQ = 1
        private val BACK = 2
    }
}