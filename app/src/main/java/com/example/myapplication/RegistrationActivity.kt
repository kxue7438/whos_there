package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore


class RegistrationActivity : AppCompatActivity() {

    private lateinit var emailTV: EditText
    private lateinit var passwordTV: EditText
    private lateinit var nameTV: EditText
    private lateinit var regBtn: Button
    private lateinit var goLogin: Button
    private lateinit var progressBar: ProgressBar
    private var validator = Validators()
    private var db = FirebaseFirestore.getInstance()

    private var mAuth: FirebaseAuth? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        mAuth = FirebaseAuth.getInstance()

        initializeUI()

        regBtn.setOnClickListener { registerNewUser() }

        goLogin.setOnClickListener { startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java)) }
    }

    private fun registerNewUser() {
        progressBar.visibility = View.VISIBLE

        val email: String = emailTV.text.toString()
        val password: String = passwordTV.text.toString()
        val name: String = nameTV.text.toString()

        if (!validator.validEmail(email)) {
            Toast.makeText(applicationContext, "Please enter valid email...", Toast.LENGTH_LONG).show()
            return
        }
        if (!validator.validPassword(password)) {
            Toast.makeText(applicationContext, "Password must contain 8 characters with one letter and one number!", Toast.LENGTH_LONG).show()
            return
        }

        val x = mAuth!!.createUserWithEmailAndPassword(email, password)

        x.addOnCompleteListener { task ->
            progressBar.visibility = View.GONE
            if (task.isSuccessful) {
                val user = mAuth!!.currentUser

                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(name).build()

                user!!.updateProfile(profileUpdates)

                val userinfo = mapOf("email" to email, "name" to name)

                db.collection("Users").document(user.uid).set(userinfo)

                Toast.makeText(applicationContext, getString(R.string.register_success_string), Toast.LENGTH_LONG).show()
                startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
            } else {
                Toast.makeText(applicationContext, getString(R.string.register_failed_string), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun initializeUI() {
        emailTV = findViewById(R.id.email)
        passwordTV = findViewById(R.id.password)
        nameTV = findViewById(R.id.full_name)
        regBtn = findViewById(R.id.register)
        progressBar = findViewById(R.id.progressBar)
        goLogin = findViewById(R.id.goto_login)
    }
}
