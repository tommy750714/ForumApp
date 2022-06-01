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
    private lateinit var idText : TextView
    private lateinit var nameText : TextView
    private lateinit var emailText : TextView
    private lateinit var profileImage : ImageView
    private lateinit var signOutButton: Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        idText = findViewById(R.id.id_txt)
        nameText = findViewById(R.id.name_txt)
        emailText = findViewById(R.id.email_txt)
        profileImage = findViewById(R.id.profile_image)
        signOutButton = findViewById(R.id.sign_out_btn)



        idText.text = currentUser?.uid
        nameText.text = currentUser?.displayName
        emailText.text = currentUser?.email
        Glide.with(this).load(currentUser?.photoUrl).into(profileImage)
        signOutButton.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }

    }


}