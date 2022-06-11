package com.example.forumapp

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap

class RegistrationActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var name: EditText
    private lateinit var mRegister: Button
    private lateinit var existaccount: TextView
    private lateinit var progressDialog: ProgressDialog
    private lateinit var mAuth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration)

        /* val actionBar = supportActionBar
        actionBar!!.title = "Create Account"
        actionBar.setDisplayShowHomeEnabled(true)
        actionBar.setDisplayHomeAsUpEnabled(true)*/

        // initialising the components
        email = findViewById(R.id.register_email)
        name = findViewById(R.id.register_name)
        password = findViewById(R.id.register_password)
        mRegister = findViewById(R.id.register_button)
        existaccount = findViewById(R.id.homepage)
        mAuth = FirebaseAuth.getInstance()
        progressDialog = ProgressDialog(this)
        progressDialog!!.setMessage("Register")

        mRegister.setOnClickListener{
            val emailLogin = email.text.toString().trim { it <= ' ' }
            val userName = name.text.toString().trim { it <= ' ' }
            val passwordLogin = password.text.toString().trim { it <= ' ' }
            if (!Patterns.EMAIL_ADDRESS.matcher(emailLogin).matches()) {
                email.error = "Invalid Email"
                email.isFocusable = true
            } else if (passwordLogin.length < 6) {
                password.error = "Length Must be greater than 6 character"
                password.isFocusable = true
            } else {
                registerUser(emailLogin, passwordLogin, userName)
            }
        }

        existaccount.setOnClickListener{
            startActivity(Intent(this@RegistrationActivity, LoginActivity::class.java))
        }
    }
    private fun registerUser(emailLogin: String, passwordLogin: String, userName: String) {
        progressDialog.show()
        mAuth.createUserWithEmailAndPassword(emailLogin, passwordLogin).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                progressDialog.dismiss()
                val user = mAuth.currentUser
                val email = user!!.email
                val uid = user.uid
                val hashMap = HashMap<Any, String?>()
                hashMap["email"] = email
                hashMap["uid"] = uid
                hashMap["name"] = userName
                hashMap["image"] = ""
                val database = FirebaseDatabase.getInstance()
                val reference = database.getReference("Users")
                reference.child(uid).setValue(hashMap)
                Toast.makeText(
                    this@RegistrationActivity,
                    "Registered User " + user.email,
                    Toast.LENGTH_LONG
                ).show()
                val mainIntent = Intent(this@RegistrationActivity, MainActivity::class.java)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(mainIntent)
                finish()
            } else {
                progressDialog.dismiss()
                Toast.makeText(this@RegistrationActivity, "Error", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            progressDialog.dismiss()
            Toast.makeText(this@RegistrationActivity, "Error Occured", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }
}