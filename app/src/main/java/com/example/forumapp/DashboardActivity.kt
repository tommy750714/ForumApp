package com.example.forumapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.forumapp.Fragments.HomeFragment
import com.example.forumapp.Fragments.MapsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class DashboardActivity : AppCompatActivity() {
    private lateinit var mAuth: FirebaseAuth
    private lateinit var idText : TextView
    private lateinit var nameText : TextView
    private lateinit var emailText : TextView
    private lateinit var profileImage : ImageView
    private lateinit var signOutButton: Button
    private lateinit var bottomNav : BottomNavigationView

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
        bottomNav = findViewById(R.id.bottomNav)


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
        bottomNav.setOnNavigationItemReselectedListener{ item ->
            when(item.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_map -> {
                    loadFragment(MapsFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.bottomNav,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}