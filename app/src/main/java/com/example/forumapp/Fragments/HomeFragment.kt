package com.example.forumapp.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.forumapp.lists.PostAdaptor
import com.example.forumapp.lists.PostModel
import com.example.forumapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.ArrayList

class HomeFragment : Fragment() {

    companion object {
        fun newInstance() = HomeFragment ()
    }

    var firebaseAuth: FirebaseAuth?= null
    var recyclerView: RecyclerView?= null
    lateinit var posts: MutableList<PostModel>
    var adapterPosts: PostAdaptor?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        firebaseAuth = FirebaseAuth.getInstance()
        recyclerView = requireView().findViewById(R.id.postrecyclerview)
        recyclerView!!.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView!!.layoutManager = layoutManager
        posts = ArrayList<PostModel>()
        loadPosts()
    }
    private fun loadPosts() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Posts")
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                posts.clear()
                for (dataSnapshot1 in dataSnapshot.children) {
                    val modelPost: PostModel? = dataSnapshot1.getValue(PostModel::class.java)
                    posts.add(modelPost!!)
                    adapterPosts = PostAdaptor(activity!!, posts)
                    recyclerView!!.adapter = adapterPosts
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(activity, databaseError.message, Toast.LENGTH_LONG).show()
            }
        })
    }




}