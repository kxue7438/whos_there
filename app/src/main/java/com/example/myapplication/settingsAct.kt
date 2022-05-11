package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class settingsAct: AppCompatActivity() {
    private lateinit var latTV: EditText
    private lateinit var lngTV: EditText
    private lateinit var currentButton: Button
    private lateinit var radiusTV: EditText
    private lateinit var addExclusion: Button
    private lateinit var sharedPreferences: SharedPreferences
    private var db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_page)

        latTV = findViewById(R.id.excl_lat)
        lngTV = findViewById(R.id.excl_lng)
        currentButton = findViewById(R.id.use_current)
        radiusTV = findViewById(R.id.excl_radius)
        addExclusion = findViewById(R.id.add_exclusion)

        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE)

        currentButton.setOnClickListener { useCurrentLocation() }
        addExclusion.setOnClickListener { addExclusionZone() }

        var contactTest=findViewById<BottomNavigationView>(R.id.bottom_navigation)
        contactTest.setOnItemSelectedListener{ bottom_navigation ->
            when(bottom_navigation.itemId) {
                R.id.contactsB ->navBarButton(0)
                R.id.profileB ->navBarButton(1)
                R.id.searchB ->navBarButton(2)
                R.id.settingsB ->true
            }
            true
        }
        contactTest.selectedItemId = R.id.settingsB
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

    private fun useCurrentLocation() {
        val currentUser = FirebaseAuth.getInstance().currentUser

        db.collection("Users").document(currentUser!!.email!!).get()
            .addOnSuccessListener { document ->
                val coords = document.data!!.get("coords") as HashMap<String, Double>
                val latitude:Double = coords.get("lat")!!.toDouble()
                val longitude:Double = coords.get("long")!!.toDouble()

                latTV.setText(latitude.toString())
                lngTV.setText(longitude.toString())
            }
    }

    private fun addExclusionZone() {
        val lat = latTV.text.toString()
        val lng = lngTV.text.toString()
        val radius = radiusTV.text.toString()

        if (lat.equals("") || lng.equals("") || radius.equals("")) {
            Toast.makeText(applicationContext, "Be sure to fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        var exclusionString = sharedPreferences.getString(exclusions, "") as String
        val editor = sharedPreferences.edit()
        exclusionString = "$exclusionString,$lat $lng $radius"
        editor.putString(exclusions, exclusionString)
        editor.apply()

        latTV.text.clear()
        lngTV.text.clear()
        radiusTV.text.clear()
    }

    companion object {
        val mypreference = "myPref"
        val exclusions = "exclusionKey"
    }
}