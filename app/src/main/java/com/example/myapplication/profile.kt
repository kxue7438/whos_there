package com.example.myapplication

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.ArrayList

class profile:AppCompatActivity {
    constructor():super(){

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        var nameTxt:TextView=findViewById(R.id.nameProfile)

    }
}