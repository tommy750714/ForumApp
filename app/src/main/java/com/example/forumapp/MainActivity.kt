package com.example.forumapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.forumapp.Fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {


    // Bottom Navigation Bar Pointer
    private val onNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener {
        when (it.itemId) {
            R.id.nav_home -> {
                loadFragment(HomeFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.nav_chat -> {
                loadFragment(EventFragment())
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
                val mapIntent = Intent(this, MapsActivity::class.java)
                startActivity(mapIntent)

                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }

  override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Bottom Navigation Bar
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)



    }

    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }



}