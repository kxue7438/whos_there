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
import com.google.android.material.navigation.NavigationBarView
import java.util.ArrayList
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager


class MainActivity : AppCompatActivity() {
    lateinit var names: ArrayList<contactsObject>
    lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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
            0->intent=Intent(this,MainActivity::class.java)
            1->intent=Intent(this,profileAct::class.java)
            2->intent=Intent(this,searchAct::class.java)
            else->intent=Intent(this,settingsAct::class.java)
        }
        startActivity(intent)
    }
    fun setContacts(){
        names= ArrayList<contactsObject>()

        recyclerView=findViewById(R.id.recyclerView)
        exampleInput()
        var adapter:recyclerAdapter = recyclerAdapter(names,applicationContext)
        var layoutMgr:RecyclerView.LayoutManager= LinearLayoutManager(applicationContext)
        recyclerView.layoutManager=layoutMgr
        recyclerView.itemAnimator= DefaultItemAnimator()
        recyclerView.adapter=adapter
    }
    fun exampleInput(){
        names.add(contactsObject("Yuh",5))
        names.add(contactsObject("Yessir",6))
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