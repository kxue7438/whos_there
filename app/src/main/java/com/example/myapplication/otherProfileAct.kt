package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class otherProfileAct: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.other_contact_page)

        var name:String="Not Found"
        var savedInfo = intent.extras
        if(savedInfo!=null) {
            name = savedInfo.getString("contact_info").toString()
        }
        var contactTextBox:TextView=findViewById<TextView>(R.id.contact_name)
        contactTextBox.setText(name)


    }
}