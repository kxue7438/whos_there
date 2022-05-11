package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class profileAct: AppCompatActivity() {

    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_page)
        var button=findViewById(R.id.locButton) as Button
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth!!.currentUser
        findViewById<TextView>(R.id.yourEmail).setText("${user!!.email!!}")
        findViewById<TextView>(R.id.yourName).setText("${user!!.displayName!!}")
        button.setOnClickListener{
            val intent = Intent(applicationContext,MapsActivity::class.java)
            startActivity(intent)
        }
        var contactTest=findViewById<BottomNavigationView>(R.id.bottom_navigation)
        contactTest.setOnItemSelectedListener{ bottom_navigation ->
            when(bottom_navigation.itemId) {
                R.id.contactsB ->navBarButton(0)
                R.id.profileB ->true
                R.id.searchB ->navBarButton(2)
                R.id.settingsB ->navBarButton(3)
            }
            true
        }
        contactTest.selectedItemId=R.id.profileB
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
}