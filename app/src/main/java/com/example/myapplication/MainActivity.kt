package com.example.myapplication
import java.util.ArrayList
import android.app.Application
import android.app.PendingIntent
import android.content.Intent
import android.content.ReceiverCallNotAllowedException
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.ProfileActivityBinding
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.ZoneId

class MainActivity: AppCompatActivity {
    lateinit var names:ArrayList<contacts>
    lateinit var recyclerView:RecyclerView
    lateinit var listener:recyclerAdapter.RecyclerViewClickListener
    lateinit var contactsList:ArrayList<contacts>
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


        var adapter:recyclerAdapter = recyclerAdapter(names,listener)
        var layoutMgr:RecyclerView.LayoutManager=LinearLayoutManager(applicationContext)
        recyclerView.layoutManager=layoutMgr
        recyclerView.itemAnimator=DefaultItemAnimator()
        recyclerView.adapter=adapter
    }

    public fun setOnClickListener() {
        listener = object:recyclerAdapter.RecyclerViewClickListener{
            public override fun onClick(v:View, pos:Int){
                var intent:Intent = Intent(applicationContext,profile::class.java)
                intent.putExtra("name",contactsList.get(pos).name_get())
                startActivity(intent)
            }
        }
    }

    fun exampleSet(){
        names.add(contacts("Yuh",5))
        names.add(contacts("Yessir",6))
    }

}