package com.example.forumapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.IOException
import java.util.*


class AddPostActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 71
    private var filePath: Uri? = null
    private var firebaseStorage : FirebaseStorage? = null
    private var storageRef : StorageReference? = null
    private lateinit var imagePreview :ImageView
    private lateinit var browseImageButton: Button
    private lateinit var  postImageButton: Button
    private lateinit var cancelPostImageButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)


        imagePreview = findViewById(R.id.image_preview)
        browseImageButton = findViewById(R.id.browse_image_btn)
        postImageButton = findViewById(R.id.post_image_btn)
        cancelPostImageButton = findViewById(R.id.cancel_post_image_btn)

        firebaseStorage = FirebaseStorage.getInstance()
        storageRef = FirebaseStorage.getInstance().reference



        cancelPostImageButton.setOnClickListener {
            val intent=Intent(this@AddPostActivity,DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        browseImageButton.setOnClickListener {
            browseImage()
        }

        postImageButton.setOnClickListener{
            postImage()
            val intent=Intent(this@AddPostActivity,DashboardActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()

        }


    }

    private fun browseImage() {

        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE_REQUEST)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK){
            if(data == null || data.data == null) {
                return
            }
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                imagePreview.setImageBitmap(bitmap)
            } catch (e:IOException){
                e.printStackTrace()
            }
        }
    }

    private fun postImage() {
        if(filePath !== null){
            val ref = storageRef?.child("myImage/" + UUID.randomUUID().toString())
            val uploadTask = ref?.putFile(filePath!!)
            Toast.makeText(this,"Upload Successfully", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this,"Please Upload an Picture", Toast.LENGTH_SHORT).show()
        }
    }

}