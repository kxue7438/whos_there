package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

class recyclerAdapter: RecyclerView.Adapter<recyclerAdapter.viewHolder> {
    lateinit var contactsList:ArrayList<contactsObject>
    lateinit var context:Context
    var pos:Int=-1
    constructor(contacts:ArrayList<contactsObject>,context:Context){
        this.contactsList=contacts
        this.context=context

    }

    public inner class viewHolder: RecyclerView.ViewHolder{
        lateinit var nameBox: TextView
        lateinit var distBox: TextView

        constructor(view: View):super(view){
            nameBox=view.findViewById(R.id.nametext)
            distBox=view.findViewById(R.id.distBox)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): recyclerAdapter.viewHolder {
        var itemView: View =
            LayoutInflater.from(parent.context).inflate(R.layout.listcontact,parent,false)
        /*
        itemView.setOnClickListener{
            val intent:Intent=Intent(context,otherProfileAct::class.java)
            if(pos!=-1) {
                intent.putExtra("contact_info", contactsList.get(pos).name_get())
            }
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        }*/
        return viewHolder(itemView)
    }

    override fun onBindViewHolder(holder: recyclerAdapter.viewHolder, position: Int) {
        var name =  contactsList.get(position).name_get()
        var dist = contactsList.get(position).dist_get()
        pos=position
        holder.nameBox.setText(name)
        holder.distBox.setText(dist.toString())
    }

    override fun getItemCount(): Int {
        return contactsList.size
    }
}