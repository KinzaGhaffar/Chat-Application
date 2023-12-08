@file:Suppress("DEPRECATION")

package com.example.chattingapp.Activity

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import com.bumptech.glide.Glide
import com.example.chattingapp.Model.User
import com.example.chattingapp.R
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnProgressListener
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import java.io.IOException
import java.util.*

class ProfileActivity : AppCompatActivity()
{
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference

    private var filePath:Uri?=null
    private val PICK_IMAGE_REQUEST:Int=1040

    private lateinit var storage: FirebaseStorage
    private lateinit var storageRef: StorageReference
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        firebaseUser=FirebaseAuth.getInstance().currentUser!!
        databaseReference=FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.uid)

        storage= FirebaseStorage.getInstance()
        storageRef=storage.reference

        databaseReference.addValueEventListener(object : ValueEventListener{
            override fun onCancelled(error: DatabaseError)
            {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }
            override fun onDataChange(snapshot: DataSnapshot)
            {
                val user = snapshot.getValue(User::class.java)

                val etuserName_Id=findViewById<TextView>(R.id.etuserName)
                etuserName_Id.setText(user!!.userName)

                val userImage_id=findViewById<ImageView>(R.id.userImage)

                if(user.profileImage == "")
                {
                    userImage_id.setImageResource(R.drawable.profile_picture)
                }
                else
                {
                    Glide.with(this@ProfileActivity).load(user.profileImage).into(userImage_id)
                }
            }
        })
        val imgBack_id=findViewById<ImageView>(R.id.imgBack)
        imgBack_id.setOnClickListener{
            onBackPressed()
        }
        val userImage_id=findViewById<ImageView>(R.id.userImage)
        userImage_id.setOnClickListener{
            chooseImage()
        }
        val btnSave_Id=findViewById<Button>(R.id.btnSave)
        btnSave_Id.setOnClickListener{
            uploadImage()
            val progressBar_Id=findViewById<ProgressBar>(R.id.progressBar)
            progressBar_Id.visibility=View.VISIBLE
        }
    }
    private fun chooseImage()
    {
        val intent:Intent=Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST)
        {
            filePath = data!!.data
            try
            {
                var bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                val userImage_Id=findViewById<ImageView>(R.id.userImage)
                userImage_Id.setImageBitmap(bitmap)

                val btnSave_Id=findViewById<Button>(R.id.btnSave)
                btnSave_Id.visibility= View.VISIBLE
            }
            catch (e: IOException)
            {
                e.printStackTrace()
            }
        }
    }
    private fun uploadImage()
    {
        if(filePath!=null)
        {
            var ref: StorageReference = storageRef.child("image/" + UUID.randomUUID().toString())
            ref.putFile(filePath!!)
            .addOnSuccessListener {
                val hashMap:HashMap<String,String> = HashMap()
                val etuserName_Id=findViewById<EditText>(R.id.etuserName)
                hashMap.put("userName",etuserName_Id.text.toString())
                hashMap.put("profileImage",filePath.toString())
                databaseReference.updateChildren(hashMap as Map<String, Any>)


                val progressBar_Id=findViewById<ProgressBar>(R.id.progressBar)
                progressBar_Id.visibility=View.GONE

                Toast.makeText(applicationContext,"Uploaded",Toast.LENGTH_SHORT).show()
                val btnSave_Id=findViewById<Button>(R.id.btnSave)
                btnSave_Id.visibility= View.GONE
            }
            .addOnFailureListener{
                val progressBar_Id=findViewById<ProgressBar>(R.id.progressBar)
                progressBar_Id.visibility=View.GONE
                Toast.makeText(applicationContext,"Failed"+it.message,Toast.LENGTH_SHORT).show()
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater=menuInflater
        inflater.inflate(R.menu.menu_file,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean
    {
        when(item.itemId)
        {
            R.id.help ->
            {
                Toast.makeText(this,"Help Selected", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.logout ->
            {
                //auth.signOut()
                val intent=Intent(this,MainActivity::class.java)
                startActivity(intent)
                return true
            }
            else ->
            {
                return super.onOptionsItemSelected(item)
            }
        }
    }
}