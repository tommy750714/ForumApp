package com.example.forumapp

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import java.util.HashMap

class LoginActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var mLogin: Button
    private lateinit var createAccount: TextView
    private lateinit var currentUser: FirebaseUser
    private lateinit var loadingBar: ProgressDialog
    private lateinit var mAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // initialising the components

        email = findViewById(R.id.login_email)
        password = findViewById(R.id.login_password)
        createAccount = findViewById(R.id.create_new_account)
        mAuth = FirebaseAuth.getInstance()
        mLogin = findViewById(R.id.login_button)
        loadingBar = ProgressDialog(this)
        mAuth = FirebaseAuth.getInstance()


        mLogin.setOnClickListener{
            val emailLogin = email.text.toString().trim { it <= ' ' }
            val passwordLogin = password.text.toString().trim { it <= ' ' }

            // return null when the format of email doesn't matches
            if (!Patterns.EMAIL_ADDRESS.matcher(emailLogin).matches()) {
                email.error = "Invalid Email"
                email.isFocusable = true
            } else {
                loginUser(emailLogin, passwordLogin)
            }
        }

        // If new account then move to Registration Activity
        createAccount.setOnClickListener{
            startActivity(Intent(this@LoginActivity, RegistrationActivity::class.java))
        }
    }

    private fun loginUser(emailLogin: String, passwordLogin: String) {
        loadingBar.setMessage("Logging In....")
        loadingBar.show()

        // sign in with email and password after authenticating
        mAuth.signInWithEmailAndPassword(emailLogin, passwordLogin).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                loadingBar.dismiss()
                val user = mAuth.currentUser
                if (task.result.additionalUserInfo!!.isNewUser) {
                    val email = user!!.email
                    val uid = user.uid
                    val hashMap = HashMap<Any, String?>()
                    hashMap["email"] = email
                    hashMap["uid"] = uid
                    hashMap["name"] = ""
                    hashMap["phone"] = ""
                    hashMap["image"] = ""
                    hashMap["cover"] = ""
                    val database = FirebaseDatabase.getInstance()

                    // store the value in Database in "Users" Node
                    val reference = database.getReference("Users")

                    // storing the value in Firebase
                    reference.child(uid).setValue(hashMap)
                }
                Toast.makeText(this@LoginActivity, "User " + user!!.email, Toast.LENGTH_LONG).show()

                val mainIntent = Intent(this@LoginActivity, MainActivity::class.java)
                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(mainIntent)
                finish()
            } else {
                loadingBar.dismiss()
                Toast.makeText(this@LoginActivity, "Login Failed", Toast.LENGTH_LONG).show()
            }
        }.addOnFailureListener {
            loadingBar.dismiss()
            Toast.makeText(this@LoginActivity, "Error Occured", Toast.LENGTH_LONG).show()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

}