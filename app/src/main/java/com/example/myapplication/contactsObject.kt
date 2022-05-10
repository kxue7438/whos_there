package com.example.myapplication

class contactsObject {
    lateinit var name:String
    var Dist=0

    constructor(name:String,dist: Int){
        this.name=name
        this.Dist=dist

    }
    fun dist_get():Int{
        return Dist
    }
    fun dist_set(dist:Int){
        this.Dist=dist
    }

    fun name_get():String{
        return name
    }
    fun name_set(name:String){
        this.name=name
    }
}