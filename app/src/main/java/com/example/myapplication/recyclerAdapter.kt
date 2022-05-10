package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView

public class recyclerAdapter: RecyclerView.Adapter<recyclerAdapter.viewHolder> {
    lateinit var contactsList:ArrayList<contacts>
    var listener:RecyclerViewClickListener

    constructor(contacts:ArrayList<contacts>,listener: RecyclerViewClickListener){
        this.contactsList=contacts
        this.listener=listener
    }
    public inner class viewHolder: RecyclerView.ViewHolder,View.OnClickListener{
        lateinit var nameBox:TextView
        lateinit var distBox:TextView

        constructor(view:View):super(view){
            nameBox=view.findViewById(R.id.nametext)
            distBox=view.findViewById(R.id.distBox)
            view.setOnClickListener(this)
        }

        public override fun onClick(p0: View?) {
            if (p0 != null) {
                listener.onClick(p0,adapterPosition)
            }
        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): recyclerAdapter.viewHolder {
        var itemView:View=LayoutInflater.from(parent.context).inflate(R.layout.listcontact,parent,false)
        return viewHolder(itemView)
    }

    override fun onBindViewHolder(holder: recyclerAdapter.viewHolder, position: Int) {
        var name =  contactsList.get(position).name_get()
        var dist = contactsList.get(position).dist_get()
        holder.nameBox.setText(name)
        holder.distBox.setText(dist.toString())
    }

    override fun getItemCount(): Int {
       return contactsList.size
    }

    public interface RecyclerViewClickListener{

        fun onClick(view:View,pos:Int)
    }
}