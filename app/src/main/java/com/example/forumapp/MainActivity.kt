package com.example.forumapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import com.example.forumapp.Fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var mAuth: FirebaseAuth

    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.nav_home -> {
                loadFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_chat -> {
                loadFragment(ChatFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_dashboard -> {
                loadFragment(ProfileFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_add -> {
                loadFragment(AddPostFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_map -> {
                loadFragment(MapFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)



        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        /**If user is not authenticated, send him to SignInActivity to authenticate first.
         * Else send him to ProfileFragment*/
        Handler().postDelayed({
            if(user != null){
            //   val navIntent = Intent(this, NavigateViewActivity::class.java)
            //   val addPostIntent = Intent(this, AddPostActivity::class.java)
                loadFragment(ProfileFragment())
            }else{
                val signInIntent = Intent(this, SignUpActivity::class.java)
                startActivity(signInIntent)
            }
        }, 2000)

    }
    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

}