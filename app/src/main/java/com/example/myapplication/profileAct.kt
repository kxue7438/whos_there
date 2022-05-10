package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class profileAct: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile_page)


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