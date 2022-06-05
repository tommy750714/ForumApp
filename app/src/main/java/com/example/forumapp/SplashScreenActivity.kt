package com.example.forumapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

            val splashscreen = findViewById<RelativeLayout>(R.id.splashscreen)

            splashscreen.alpha = 0f
            splashscreen.animate().setDuration(2200).alpha(1f).withEndAction{

            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)

            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            finish()
        }

    }
}