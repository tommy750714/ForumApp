package com.example.forumapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.RelativeLayout
import com.example.forumapp.Fragments.HomeFragment
import com.google.firebase.auth.FirebaseAuth

class SplashScreenActivity : AppCompatActivity() {

    //Firebase Auth
    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

            val splashscreen = findViewById<RelativeLayout>(R.id.splashscreen)
            splashscreen.alpha = 0f
            splashscreen.animate().setDuration(2200).alpha(1f).withEndAction{
                // Firebase Auth get current user info
                mAuth = FirebaseAuth.getInstance()
                val user = mAuth.currentUser

                /**If user is not authenticated, send him to SignInActivity to authenticate first.
                 * Else send him to ProfileFragment*/
                Handler().postDelayed({
                    if(user != null){
                        val mainIntent = Intent(this, MainActivity::class.java)
                        startActivity(mainIntent)

                    }else{
                        val loginIntent = Intent(this, LoginActivity::class.java)
                        startActivity(loginIntent)
                    }
                }, 2000)
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)

        }

    }
}