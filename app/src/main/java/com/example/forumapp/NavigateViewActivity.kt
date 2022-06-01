package com.example.forumapp

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.forumapp.Fragments.*


class NavigateViewActivity : AppCompatActivity() {

    private lateinit var bottomNav : BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigate_view)

        loadFragment(ProfileFragment())

        bottomNav = findViewById(R.id.bottomNav)
        bottomNav.setOnNavigationItemReselectedListener {
            when (it.itemId) {
                R.id.nav_home -> {
                    loadFragment(HomeFragment())
                    return@setOnNavigationItemReselectedListener
                }
                R.id.nav_chat -> {
                    loadFragment(ChatFragment())
                    return@setOnNavigationItemReselectedListener
                }
                R.id.nav_dashboard -> {
                    loadFragment(ProfileFragment())
                    return@setOnNavigationItemReselectedListener
                }
                R.id.nav_add -> {
                    loadFragment(AddBlogFragment())
                    return@setOnNavigationItemReselectedListener
                }
                R.id.nav_map -> {
                    loadFragment(MapFragment())
                    return@setOnNavigationItemReselectedListener
                }
            }
        }
    }

    private  fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}