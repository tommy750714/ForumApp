package com.example.forumapp

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.icu.text.CaseMap
import android.media.Image
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.*
import androidx.core.content.ContextCompat
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URI
import java.util.*
import java.util.jar.Manifest


class AddPostActivity : AppCompatActivity() {

    private val PICK_IMAGE_REQUEST = 300
    private val CAMERA_IMAGE_REQUEST = 400
    private var filePath: Uri? = null
    private var firebaseStorage : FirebaseStorage? = null
    private var storageRef : StorageReference? = null
    private lateinit var imagePreview :ImageView
    private lateinit var browseImageButton: Button
    private lateinit var  postImageButton: Button
    private lateinit var cancelPostImageButton: Button
    private lateinit var cameraImageButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_post)


        imagePreview = findViewById(R.id.image_preview)
        browseImageButton = findViewById(R.id.browse_image_btn)
        postImageButton = findViewById(R.id.post_image_btn)
        cancelPostImageButton = findViewById(R.id.cancel_post_image_btn)
        cameraImageButton = findViewById(R.id.camera_image_btn)

        firebaseStorage = FirebaseStorage.getInstance()
        storageRef = FirebaseStorage.getInstance().reference



        cancelPostImageButton.setOnClickListener {
            val intent=Intent(this@AddPostActivity,MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }

        browseImageButton.setOnClickListener {
            browseImage()
        }

        cameraImageButton.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            try {
                startActivityForResult(
                    intent,
                    CAMERA_IMAGE_REQUEST
                )
            } catch (e:ActivityNotFoundException) {

                Toast.makeText(this, "Can not open camera", Toast.LENGTH_SHORT).show()
            }

        }

        postImageButton.setOnClickListener{
            postImage()
            val intent=Intent(this@AddPostActivity,MainActivity::class.java)
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
        if (resultCode == Activity.RESULT_OK) {
            when(requestCode){
                PICK_IMAGE_REQUEST -> {
                    if (data == null || data.data == null) {
                        return
                    }
                    filePath = data.data
                    try {
                        val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                        imagePreview.setImageBitmap(bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
                CAMERA_IMAGE_REQUEST -> {
                    try {
                        val bitmap = (data!!.extras!!.get("data") as Bitmap)
                        imagePreview.setImageBitmap(bitmap)
                        filePath = getImageUri(applicationContext, bitmap)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
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

    private fun getImageUri(inContext: Context, inImage: Bitmap): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG,100, bytes)
        val filePath = MediaStore.Images.Media.insertImage(
            inContext.contentResolver,
            inImage,
            "Title",
            null
        )

        return Uri.parse(filePath)
    }

}