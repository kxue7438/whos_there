package com.example.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class recyclerAdapter: RecyclerView.Adapter<recyclerAdapter.viewHolder> {
    private lateinit var contacts:ArrayList<contacts>
    constructor(contacts:ArrayList<contacts>){
        this.contacts=contacts
    }
    class viewHolder: RecyclerView.ViewHolder{
        lateinit var nameBox:TextView
        lateinit var distBox:TextView

        constructor(view:View):super(view){
            nameBox=view.findViewById(R.id.nametext)
            distBox=view.findViewById(R.id.distBox)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): recyclerAdapter.viewHolder {
        var itemView:View=LayoutInflater.from(parent.context).inflate(R.layout.listcontact,parent,false)
        return viewHolder(itemView)
    }

    override fun onBindViewHolder(holder: recyclerAdapter.viewHolder, position: Int) {
        var name =  contacts.get(position).name_get()
        var dist = contacts.get(position).dist_get()
        holder.nameBox.setText(name)
        holder.distBox.setText(dist.toString())
    }

    override fun getItemCount(): Int {
       return contacts.size
    }
}