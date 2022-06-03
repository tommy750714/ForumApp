package com.example.forumapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.forumapp.Fragments.*
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import java.util.concurrent.Executor

class MainActivity : AppCompatActivity() {

    //Firebase Auth
    private lateinit var mAuth: FirebaseAuth

    //Biometric Auth
    private lateinit var executor: Executor
    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo


    // Bottom Navigation Bar Pointer
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
                val addPostIntent = Intent(this, AddPostActivity::class.java)
                startActivity(addPostIntent)
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

        // Bottom Navigation Bar
        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNav)
        bottomNav.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        // Firebase Auth get current user info
        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        // For creating a thread and run a long-running task.
        executor = ContextCompat.getMainExecutor(this)

        // Manage a system-provided biometric prompt
        biometricPrompt = BiometricPrompt(this, executor,
          object : BiometricPrompt.AuthenticationCallback() {

              override fun onAuthenticationError(errorCode: Int,
                                                 errString: CharSequence) {
                  super.onAuthenticationError(errorCode, errString)
                  Toast.makeText(applicationContext,
                      "Authentication error: $errString", Toast.LENGTH_SHORT)
                      .show()
              }

              override fun onAuthenticationSucceeded(
                  result: BiometricPrompt.AuthenticationResult) {
                  super.onAuthenticationSucceeded(result)
                  Toast.makeText(applicationContext,
                      "Authentication succeeded!", Toast.LENGTH_SHORT)
                      .show()

              }

              override fun onAuthenticationFailed() {
                  super.onAuthenticationFailed()
                  Toast.makeText(applicationContext, "Authentication failed",
                      Toast.LENGTH_SHORT)
                      .show()
              }

          })

      // Instantiate the promptInfo object
      promptInfo = BiometricPrompt.PromptInfo.Builder()
          .setTitle("Biometric login for my app")
          .setSubtitle("Log in using your biometric credential")
          .setNegativeButtonText("Use account password")
          .build()

      /**If user is not authenticated, send him to SignInActivity to authenticate first.
         * Else send him to ProfileFragment*/
        Handler().postDelayed({
            if(user != null){
            //   val navIntent = Intent(this, NavigateViewActivity::class.java)
            //   val addPostIntent = Intent(this, AddPostActivity::class.java)
                biometricPrompt.authenticate(promptInfo)

                loadFragment(ProfileFragment())
            }else{
                val signUpIntent = Intent(this, SignUpActivity::class.java)
                startActivity(signUpIntent)
            }
        }, 2000)

    }

    override fun onResume() {
        super.onResume()
        biometricPrompt.authenticate(promptInfo)
    }
    private fun loadFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }



}