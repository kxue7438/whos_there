package com.example.myapplication

import android.Manifest
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {
    lateinit var names: ArrayList<contactsObject>
    lateinit var recyclerView: RecyclerView
    private var db = FirebaseFirestore.getInstance()
    private var mAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAuth = FirebaseAuth.getInstance()
        setContacts()

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
            checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION), REQ)
        } else {
            startForegroundService(Intent(applicationContext, MyService::class.java))
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
        exampleInput()
    }
    fun setContactsStage2(){
        var adapter:recyclerAdapter = recyclerAdapter(names,applicationContext)
        var layoutMgr:RecyclerView.LayoutManager= LinearLayoutManager(applicationContext)
        recyclerView.layoutManager=layoutMgr
        recyclerView.itemAnimator= DefaultItemAnimator()
        recyclerView.adapter=adapter
    }
    fun exampleInput(){
        val user = mAuth!!.currentUser
        val users = db.collection("Users").document("jkl@gmail.com")
        val TAG: String = MainActivity::class.java.getName()
        lateinit var firestoreUser:String
        //the task is run asynchronously, u must make it so that nothing goes on before
        //the task is complete if ur depending on the info from the task below
        var userMap=users.get().addOnSuccessListener { document ->
            if (document != null) {
                firestoreUser=document.data!!.get("name") as String
                names.add(contactsObject("$firestoreUser",5))
                names.add(contactsObject("Yessir",6))
                setContactsStage2()
                Log.d(TAG, "DocumentSnapshot data: ${document.data} \n" +
                        "firestoreuser: ${firestoreUser}")
            } else {
                Log.d(TAG, "No such document")
            }
        }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
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
        if (requestCode == REQ) {

            // Checking whether user granted the permission or not.
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Showing the toast message
                startForegroundService(Intent(applicationContext, MyService::class.java))
                Toast.makeText(this, "Location Service Permission Granted", Toast.LENGTH_SHORT).show();
            }else {
                Toast.makeText(this, "Location Service Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    companion object{
        private val REQ = 1
    }
}