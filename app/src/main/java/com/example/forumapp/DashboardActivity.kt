package com.example.forumapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        var id_txt = findViewById<TextView>(R.id.id_txt)
        id_txt.text = currentUser?.uid

        var name_txt = findViewById<TextView>(R.id.name_txt)
        name_txt.text = currentUser?.displayName

        var email_txt = findViewById<TextView>(R.id.email_txt)
        email_txt.text = currentUser?.email

        var profile_image = findViewById<ImageView>(R.id.profile_image)
        Glide.with(this).load(currentUser?.photoUrl).into(profile_image)

        var sign_out_btn = findViewById<Button>(R.id.sign_out_btn)
        sign_out_btn.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}