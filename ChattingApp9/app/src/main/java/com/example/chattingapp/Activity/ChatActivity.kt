package com.example.chattingapp.Activity

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattingapp.Adapter.ChatAdapter
import com.example.chattingapp.Model.Chat
import com.example.chattingapp.Model.NotificationData
import com.example.chattingapp.Model.PushNotification
import com.example.chattingapp.Model.User
import com.example.chattingapp.R
import com.example.chattingapp.RetrofitInstance
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class ChatActivity : AppCompatActivity()
{
    var firebaseUser: FirebaseUser? = null
    var reference: DatabaseReference? = null
    var chatList = ArrayList<Chat>()

    private lateinit var auth: FirebaseAuth

    var topic="/topics/myTopic"
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        var btn:Button = findViewById(R.id.btn_capture)

        btn.setOnClickListener()
        {
            var takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(takePictureIntent,5)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        if(requestCode == 5)
        {
            val takenImage = data?.extras?.get("data") as Bitmap
//            var tvObj:ImageView = findViewById(R.id.imageView)
//            tvObj.setImageBitmap(takenImage)
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data)
        }


        FirebaseMessaging.getInstance().subscribeToTopic(topic)

        val cv=findViewById<RecyclerView>(R.id.chatRecyclerView)
        cv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val imgBack_id=findViewById<ImageView>(R.id.imgBack)
        imgBack_id.setOnClickListener{
            onBackPressed()
        }

        var intent = getIntent()
        var userId = intent.getStringExtra("userId")
        var userName = intent.getStringExtra("userName")

        firebaseUser = FirebaseAuth.getInstance().currentUser
        reference = FirebaseDatabase.getInstance().getReference("Users").child(userId !!)

        reference!!.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                val user = snapshot.getValue(User::class.java)

                val tvUserName_Id = findViewById<TextView>(R.id.tvUserName)
                tvUserName_Id.text = user !!.userName

                val imgProfile_id = findViewById<ImageView>(R.id.imgProfile)

                if (user.profileImage == "") {
                    imgProfile_id.setImageResource(R.drawable.profile_picture)
                } else {
                    Glide.with(this@ChatActivity).load(user.profileImage).into(imgProfile_id)
                }
            }
        })

        var btnSendMessage_Id=findViewById<ImageButton>(R.id.btnSendMessage)
        btnSendMessage_Id.setOnClickListener{
            val etMessage_Id=findViewById<EditText>(R.id.etMessage)
            var message: String = etMessage_Id.text.toString()

            if (message.isEmpty())
            {
                Toast.makeText(applicationContext, "message is empty", Toast.LENGTH_SHORT).show()
                etMessage_Id.setText("")
            }
            else
            {
                sendMessage(firebaseUser !!.uid, userId, message)
                etMessage_Id.setText("")
                topic = "/topics/$userId"
                PushNotification(NotificationData(userName !!, message),
                        topic).also {
                    sendNotification(it)
                }
            }
            readMessage(firebaseUser !!.uid, userId)
        }
    }
    private fun sendMessage(senderId: String, receiverId: String, message: String)
    {
        var reference: DatabaseReference? = FirebaseDatabase.getInstance().getReference()

        var hashMap: HashMap<String, String> = HashMap()
        hashMap.put("senderId", senderId)
        hashMap.put("receiverId", receiverId)
        hashMap.put("message", message)

        reference!!.child("Chat").push().setValue(hashMap)





        val t: Thread = object : Thread()
        {
            override fun run()
            {
                try
                {
                    while (! isInterrupted)
                    {
                        sleep(1000)
                        runOnUiThread{
                            val time_date = findViewById<TextView>(R.id.time_date)
                            val date = System.currentTimeMillis()
                            val sdf = SimpleDateFormat("MMM dd yyyy\nhh-mm-ss a")
                            val dateString = sdf.format(date)
                            time_date.text = dateString
                        }
                    }
                }
                catch (e: InterruptedException)
                {

                }
            }
        }
        t.start()
    }

    fun readMessage(senderId: String, receiverId: String)
    {
        val databaseReference: DatabaseReference = FirebaseDatabase.getInstance().getReference("Chat")

        databaseReference.addValueEventListener(object : ValueEventListener
        {
            override fun onCancelled(error: DatabaseError)
            {
                TODO("Not yet implemented")
            }

            override fun onDataChange(snapshot: DataSnapshot)
            {
                chatList.clear()
                for (dataSnapShot: DataSnapshot in snapshot.children)
                {
                    val chat = dataSnapShot.getValue(Chat::class.java)

                    if (chat !!.senderId.equals(senderId) && chat.receiverId.equals(receiverId) ||
                            chat.senderId.equals(receiverId) && chat.receiverId.equals(senderId)
                    )
                    {
                        chatList.add(chat)
                    }
                }
                val chatAdapter = ChatAdapter(this@ChatActivity, chatList)
                val cv = findViewById<RecyclerView>(R.id.chatRecyclerView)
                cv.adapter = chatAdapter
            }
        })
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO).launch {
        try
        {
            val response = RetrofitInstance.api.postNotification(notification)
            if(response.isSuccessful)
            {
                Log.d("TAG", "Response: ${Gson().toJson(response)}")
            }
            else
            {
                Log.e("TAG", response.errorBody().toString())
            }
        }
        catch (e: Exception)
        {
            Log.e("TAG", e.toString())
        }
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
                val intent=Intent(this@ChatActivity,LoginActivity::class.java)
                startActivity(intent)
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
}