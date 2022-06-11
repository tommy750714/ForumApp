package com.example.forumapp.Fragments

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.view.LayoutInflater
import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.forumapp.MainActivity
import com.example.forumapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import java.io.ByteArrayOutputStream
import java.util.HashMap

class AddPostFragment : Fragment() {

    companion object {
        fun newInstance() = AddPostFragment ()
        private const val CAMERA_REQUEST = 100
        private const val STORAGE_REQUEST = 200
        private const val IMAGEPICK_GALLERY_REQUEST = 300
        private const val IMAGE_PICKCAMERA_REQUEST = 400
    }

    var firebaseAuth: FirebaseAuth?= null
    lateinit var title: EditText
    lateinit var des: EditText
    lateinit var cameraPermission: Array<String>
    lateinit var storagePermission: Array<String>
    lateinit var pd: ProgressDialog
    lateinit var image: ImageView
    lateinit var upload: Button
    lateinit var edititle: String
    lateinit var editdes: String
    lateinit var editimage: String
    var imageUri: Uri?= null
    var name: String?= null
    var email: String?= null
    var uid: String?= null
    var dp: String?= null
    var dbReference: DatabaseReference?= null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_add_post, container, false)
        val intent = requireActivity().intent
        title = view.findViewById(R.id.ptitle)
        des = view.findViewById(R.id.pdes)
        image = view.findViewById(R.id.imagep)
        upload = view.findViewById(R.id.pupload)
        pd = ProgressDialog(context)
        pd.setCanceledOnTouchOutside(false)

        // Retrieving the user data like name ,email and profile pic using query
        dbReference = FirebaseDatabase.getInstance().getReference("Users")
        val query = dbReference!!.orderByChild("email").equalTo(email)
        query.addValueEventListener(object : ValueEventListener {


            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (dataSnapshot1 in dataSnapshot.children) {
                    name = dataSnapshot1.child("name").value.toString()
                    email = "" + dataSnapshot1.child("email").value
                    dp = "" + dataSnapshot1.child("image").value.toString()
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })


        // Initialising camera and storage permission
        cameraPermission =
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        storagePermission = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        // After click on image we will be selecting an image
        image.setOnClickListener(View.OnClickListener { showImagePicDialog() })

        // Now we will upload out Post
        upload.setOnClickListener(View.OnClickListener {
            val postTitle = "" + title.text.toString().trim { it <= ' ' }
            val description = "" + des.text.toString().trim { it <= ' ' }

            // If empty set error
            if (TextUtils.isEmpty(postTitle)) {
                title.error = "Title Cant be empty"
                Toast.makeText(context, "Title can't be left empty", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }

            // If empty set error
            if (TextUtils.isEmpty(description)) {
                des.error = "Description Cant be empty"
                Toast.makeText(context, "Description can't be left empty", Toast.LENGTH_LONG).show()
                return@OnClickListener
            }

            // If empty show error
            if (imageUri == null) {
                Toast.makeText(context, "Select an Image", Toast.LENGTH_LONG).show()
                return@OnClickListener
            } else {
                uploadData(postTitle, description)
            }
        })
        return view
    }


    private fun showImagePicDialog() {

        val options = arrayOf("Camera", "Gallery")
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Pick Image From")
        builder.setItems(options) { dialog, which -> // check for the camera and storage permission if
            // not given the request for permission
            if (which == 0) {
                if (!checkCameraPermission()) {
                    requestCameraPermission()
                } else {
                    pickFromCamera()
                }
            } else if (which == 1) {
                if (!checkStoragePermission()) {
                    requestStoragePermission()
                } else {
                    pickFromGallery()
                }
            }
        }
        builder.create().show()
    }

    // check for storage permission
    private fun checkStoragePermission(): Boolean {
        return (ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
                == PackageManager.PERMISSION_GRANTED)
    }

    // if not given then request for permission after that check if request is given or not
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_REQUEST -> {
                if (grantResults.isNotEmpty()) {
                    val cameraAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED
                    val writeStorageaccepted = grantResults[1] == PackageManager.PERMISSION_GRANTED

                    // if request access given the pick data
                    if (cameraAccepted && writeStorageaccepted) {
                        pickFromCamera()
                    } else {
                        Toast.makeText(
                            context,
                            "Please Enable Camera and Storage Permissions",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
            STORAGE_REQUEST -> {
                if (grantResults.isNotEmpty()) {
                    val writeStorageaccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED

                    // if request access given the pick data
                    if (writeStorageaccepted) {
                        pickFromGallery()
                    } else {
                        Toast.makeText(
                            context,
                            "Please Enable Storage Permissions",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }
    }

    // request for permission to write data into storage
    private fun requestStoragePermission() {
        requestPermissions(storagePermission, STORAGE_REQUEST)
    }

    // check camera permission to click picture using camera
    private fun checkCameraPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED
        val result1 = ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
        return result && result1
    }

    // request for permission to click photo using camera in app
    private fun requestCameraPermission() {
        requestPermissions(cameraPermission, CAMERA_REQUEST)
    }

    // if access is given then pick image from camera and then put
    // the imageuri in intent extra and pass to startactivityforresult
    private fun pickFromCamera() {
        val contentValues = ContentValues()
        contentValues.put(MediaStore.Images.Media.TITLE, "Temp_pic")
        contentValues.put(MediaStore.Images.Media.DESCRIPTION, "Temp Description")
        imageUri = requireActivity().contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        )
        val camerIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        camerIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)
        startActivityForResult(camerIntent, IMAGE_PICKCAMERA_REQUEST)
    }

    // if access is given then pick image from gallery
    private fun pickFromGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK)
        galleryIntent.type = "image/*"
        startActivityForResult(galleryIntent, IMAGEPICK_GALLERY_REQUEST)
    }

    // Upload the value of blog data into firebase
    private fun uploadData(postTitle: String, description: String) {
        // show the progress dialog box
        pd.setMessage("Publishing Post")
        pd.show()
        val timestamp = System.currentTimeMillis().toString()
        val filePath = "Posts/post$timestamp"
        val bitmap = (image.drawable as BitmapDrawable).bitmap
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val data = byteArrayOutputStream.toByteArray()

        // initialising the storage reference for updating the data
        val storageReference = FirebaseStorage.getInstance().reference.child(filePath)
        storageReference.putBytes(data).addOnSuccessListener { taskSnapshot ->
            // getting the url of image uploaded
            var uriTask = taskSnapshot.storage.downloadUrl
            while (!uriTask.isSuccessful);
            var downloadUri = uriTask.result.toString()
            if (uriTask.isSuccessful) {
                // if task is successful the update the data into firebase
                val hashMap = HashMap<Any, String?>()
                hashMap["uid"] = uid
                hashMap["uname"] = name
                hashMap["email"] = email
                hashMap["udp"] = dp
                hashMap["title"] = postTitle
                hashMap["description"] = description
                hashMap["image"] = downloadUri
                hashMap["time"] = timestamp

                // set the data into firebase and then empty the title ,description and image data
                val dbReference = FirebaseDatabase.getInstance().getReference("Posts")
                dbReference.child(timestamp).setValue(hashMap)
                    .addOnSuccessListener {
                        pd.dismiss()
                        Toast.makeText(context, "Published", Toast.LENGTH_LONG).show()
                        title.setText("")
                        des.setText("")
                        image.setImageURI(null)
                        imageUri = null
                        startActivity(Intent(context, MainActivity::class.java))
                        requireActivity().finish()
                    }.addOnFailureListener {
                        pd.dismiss()
                        Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
                    }
            }
        }.addOnFailureListener {
            pd.dismiss()
            Toast.makeText(context, "Failed", Toast.LENGTH_LONG).show()
        }
    }

    // Here we are getting data from image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == IMAGEPICK_GALLERY_REQUEST) {
                imageUri = data!!.data
                image.setImageURI(imageUri)
            }
            if (requestCode == IMAGE_PICKCAMERA_REQUEST) {
                image.setImageURI(imageUri)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}