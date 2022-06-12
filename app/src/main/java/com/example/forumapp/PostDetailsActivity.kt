package com.example.forumapp

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.provider.MediaStore
import android.text.format.DateFormat
import android.widget.*
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.*
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL
import java.util.*
import java.util.concurrent.Executors

class PostDetailsActivity : AppCompatActivity() {

    lateinit var ptime: String
    var uimage: String? = null
    var postId: String? = null
    var image: ImageView? = null
    lateinit var name: TextView
    lateinit var time: TextView
    lateinit var title: TextView
    lateinit var description: TextView
    lateinit var delbtn: Button
    lateinit var download: Button
    var progressDialog: ProgressDialog? = null
    lateinit var backbtn: Button
    val myExecutor = Executors.newSingleThreadExecutor()
    val myHandler = Handler(Looper.getMainLooper())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_details)

        postId = intent.getStringExtra("pid").toString()
        image = findViewById(R.id.pimagetvco)
        time = findViewById(R.id.utimeco)
        title = findViewById(R.id.ptitleco)
        description = findViewById(R.id.descriptco)
        delbtn = findViewById(R.id.delete)
        download = findViewById(R.id.download)
        progressDialog = ProgressDialog(this)
        backbtn = findViewById(R.id.back_btn)

        loadPostInfo()

        delbtn.setOnClickListener{
            deletePost(ptime, uimage!!)
            finish()
        }

        backbtn.setOnClickListener{
            finish()
        }

        download.setOnClickListener {
            myExecutor.execute {
                var mImage:Bitmap? = mLoad(uimage!!)
                myHandler.post {
                if(mImage!=null) {
                    mSaveMediaToStorage(mImage)
                    finish()
                }
                }
            }
        }

    }



    private fun loadPostInfo() {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Posts")
        val query = databaseReference.orderByChild("time").equalTo(postId)
        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (dataSnapshot1 in dataSnapshot.children) {
                    val ptitle = dataSnapshot1.child("title").value.toString()
                    val descriptions = dataSnapshot1.child("description").value.toString()
                    uimage = dataSnapshot1.child("image").value.toString()

                    ptime = dataSnapshot1.child("time").value.toString()
                    val calendar = Calendar.getInstance(Locale.ENGLISH)
                    calendar.timeInMillis = ptime.toLong()
                    val timedate = DateFormat.format("dd/MM/yyyy hh:mm aa", calendar).toString()


                    title.text = ptitle
                    description.text = descriptions
                    time.text = timedate

                    if (uimage!!.isNotEmpty()){
                        Picasso.get().load(uimage).into(image)
                    } else {
                        Picasso.get().load(R.drawable.placeholder_image_square).into(image)
                    }

                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    private fun deletePost(pid: String, image: String) {
        progressDialog!!.setMessage("Deleting")
        val pic = FirebaseStorage.getInstance().getReferenceFromUrl(uimage!!)
        pic.delete().addOnSuccessListener {
            val query = FirebaseDatabase.getInstance().getReference("Posts").orderByChild("time")
                .equalTo(pid)
            query.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    for (dataSnapshot1 in dataSnapshot.children) {
                        dataSnapshot1.ref.removeValue()
                    }
                    progressDialog!!.dismiss()
                    Toast.makeText(this@PostDetailsActivity, "Deleted Successfully", Toast.LENGTH_LONG).show()
                }

                override fun onCancelled(databaseError: DatabaseError) {}
            })
        }.addOnFailureListener { }
    }

    private fun mLoad(string: String): Bitmap? {
        val url: URL = mStringToURL(string)!!
        val connection: HttpURLConnection?
        try {
            connection = url.openConnection() as HttpURLConnection
            connection.connect()
            val inputStream: InputStream = connection.inputStream
            val bufferedInputStream = BufferedInputStream(inputStream)
            return BitmapFactory.decodeStream(bufferedInputStream)
        } catch (e: IOException) {
            e.printStackTrace()
            Toast.makeText(applicationContext, "Error", Toast.LENGTH_LONG).show()
        }
        return null
    }

    private fun mStringToURL(string: String): URL? {
        try {
            return URL(string)
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }
        return null
    }

    private fun mSaveMediaToStorage(bitmap: Bitmap?) {
        val filename = "${System.currentTimeMillis()}.jpg"
        var fos: OutputStream? = null
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.contentResolver?.also { resolver ->
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                }
                val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                fos = imageUri?.let { resolver.openOutputStream(it) }
            }
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
            val image = File(imagesDir, filename)
            fos = FileOutputStream(image)
        }
        fos?.use {
            bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, it)
            Toast.makeText(this , "Saved to Gallery" , Toast.LENGTH_SHORT).show()
        }
    }

}