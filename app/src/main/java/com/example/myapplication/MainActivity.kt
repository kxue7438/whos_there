package com.example.myapplication
import java.util.ArrayList
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.content.ReceiverCallNotAllowedException
import android.os.Bundle
import android.view.KeyEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

class MainActivity: AppCompatActivity {
    lateinit var names:ArrayList<contacts>
    lateinit var recyclerView:RecyclerView
    constructor():super(){

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        names= ArrayList<contacts>()

        recyclerView=findViewById(R.id.recyclerContacts)
        exampleSet()
        setAdapter()
    }
    fun setAdapter(){
        var adapter:recyclerAdapter = recyclerAdapter(names)
        var layoutMgr:RecyclerView.LayoutManager=LinearLayoutManager(applicationContext)
        recyclerView.layoutManager=layoutMgr
        recyclerView.itemAnimator=DefaultItemAnimator()
        recyclerView.adapter=adapter
    }

    fun exampleSet(){
        names.add(contacts("Yuh",5))
        names.add(contacts("Yessir",6))
    }
    
}