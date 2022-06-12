package com.example.forumapp.Fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.forumapp.LoginActivity
import com.example.forumapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class ProfileFragment : Fragment() {

    companion object {
        fun newInstance() = ProfileFragment ()
    }

    private lateinit var mAuth: FirebaseAuth
    lateinit var idText : TextView
    lateinit var nameText : TextView
    lateinit var emailText : TextView
    lateinit var profileImage : ImageView
    lateinit var signOutButton:  Button


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        getUserInfo()
    }

    private fun getUserInfo(){

        idText = requireView().findViewById(R.id.id_txt)
        nameText = requireView().findViewById(R.id.name_txt)
        emailText = requireView().findViewById(R.id.email_txt)
        profileImage = requireView().findViewById(R.id.profile_image)
        signOutButton = requireView().findViewById(R.id.sign_out_btn)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        idText.text = currentUser?.uid
        nameText.text = currentUser?.displayName
        emailText.text = currentUser?.email
        Glide.with(this).load(currentUser?.photoUrl).into(profileImage)

        signOutButton.setOnClickListener {
            mAuth.signOut()
            val intent = Intent(activity, LoginActivity::class.java)
            this.startActivity(intent)
        }

    }
}