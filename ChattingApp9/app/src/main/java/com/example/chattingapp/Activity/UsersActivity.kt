package com.example.chattingapp.Activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattingapp.Adapter.UserAdapter
import com.example.chattingapp.Model.User
import com.example.chattingapp.R
import com.example.chattingapp.firebase.FirebaseService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging

@Suppress("DEPRECATION")
class UsersActivity : AppCompatActivity()
{
    var userList = ArrayList<User>()
    lateinit var auth:FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)

        FirebaseService.sharedPref = getSharedPreferences("sharedPref", Context.MODE_PRIVATE)
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
            FirebaseService.token = it.token
        }


        val rv=findViewById<RecyclerView>(R.id.userRecyclerView)
        rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val imgBack_id=findViewById<ImageView>(R.id.imgBack)
        imgBack_id.setOnClickListener{
            onBackPressed()
        }

        val imgProfile_id=findViewById<ImageView>(R.id.imgProfile)
        imgProfile_id.setOnClickListener{
            val intent= Intent(this@UsersActivity, ProfileActivity::class.java)
            startActivity(intent)
        }

        getUsersList()
        val userAdapter = UserAdapter(this@UsersActivity, userList)
        rv.adapter = userAdapter
    }
    fun getUsersList()
    {
        val firebase: FirebaseUser = FirebaseAuth.getInstance().currentUser!!

        var userid = firebase.uid
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/$userid")


        val databaseReference: DatabaseReference =
            FirebaseDatabase.getInstance().getReference("Users")

        databaseReference.addValueEventListener(object : ValueEventListener
        {
            override fun onCancelled(error: DatabaseError)
            {
                Toast.makeText(applicationContext, error.message, Toast.LENGTH_SHORT).show()
            }

            override fun onDataChange(snapshot: DataSnapshot)
            {
                userList.clear()
                val currentUser = snapshot.getValue(User::class.java)
                val imgProfile_id=findViewById<ImageView>(R.id.imgProfile)

                if(currentUser!!.profileImage  == "")
                {
                    imgProfile_id.setImageResource(R.drawable.profile_picture)
                }
                else
                {
                    Glide.with(this@UsersActivity).load(currentUser.profileImage ).into(imgProfile_id)
                }

                for (dataSnapShot: DataSnapshot in snapshot.children)
                {
                    val user = dataSnapShot.getValue(User::class.java)

                    if (!user!!.userId.equals(firebase.uid))
                    {
                        userList.add(user)
                    }
                }
                val userAdapter=UserAdapter(this@UsersActivity,userList)
                val rv=findViewById<RecyclerView>(R.id.userRecyclerView)
                rv.adapter=userAdapter
            }
        })

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater=menuInflater
        inflater.inflate(R.menu.menu_file,menu)
        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.help -> {
                Toast.makeText(this,"Help Selected", Toast.LENGTH_SHORT).show()
                return true
            }
            R.id.logout -> {
                auth.signOut()
                val intent=Intent(this@UsersActivity,LoginActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
}