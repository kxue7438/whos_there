package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class searchAct: AppCompatActivity() {
    private lateinit var emailTV: EditText
    private lateinit var addContactButton: Button
    private var db = FirebaseFirestore.getInstance()
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_page)

        sharedPreferences = getSharedPreferences(mypreference, Context.MODE_PRIVATE)

        emailTV = findViewById(R.id.add_email)
        addContactButton = findViewById(R.id.add_button)

        addContactButton.setOnClickListener { addContact()}

        var contactTest=findViewById<BottomNavigationView>(R.id.bottom_navigation)
        contactTest.setOnItemSelectedListener{ bottom_navigation ->
            when(bottom_navigation.itemId) {
                R.id.contactsB ->navBarButton(0)
                R.id.profileB ->navBarButton(1)
                R.id.searchB ->true
                R.id.settingsB ->navBarButton(3)
            }
            true
        }
        contactTest.selectedItemId = R.id.searchB
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

    private fun addContact() {
        val email: String = emailTV.text.toString()
        val userRef = db.collection("Users").document(email)
        val user = FirebaseAuth.getInstance().currentUser
        if (user!!.email.equals(email)) {
            emailTV.text.clear()
            Toast.makeText(applicationContext, "Cannot add self as contact", Toast.LENGTH_SHORT).show()
            return
        }

        userRef.get().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val doc = task.result
                if(doc != null) {
                    if(doc.exists()) {
                        val newContactName = doc.getString("name")
                        // user with email exists. check if already added and then add if not
                        val contactsSet = sharedPreferences.getStringSet(contacts, HashSet<String>()) as HashSet<String>
                        if (contactsSet.contains(email)) {
                            Toast.makeText(applicationContext, "$newContactName is already a contact", Toast.LENGTH_SHORT).show()
                        } else {
                            val newContactsSet = contactsSet.plusElement(email)
                            val editor = sharedPreferences.edit()
                            editor.putStringSet(contacts, newContactsSet)
                            editor.apply()
                            Toast.makeText(applicationContext, "Added $newContactName as a contact", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "User not found", Toast.LENGTH_SHORT).show()
                    }
                    emailTV.text.clear()
                }
            } else {
                Toast.makeText(applicationContext, "Error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        val mypreference = "myPref"
        val contacts = "contactsKey"
    }
}