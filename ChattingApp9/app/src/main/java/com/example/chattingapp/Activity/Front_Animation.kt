package com.example.chattingapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import com.example.chattingapp.R
import com.google.firebase.auth.FirebaseAuth

class Front_Animation : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_front__animation)

        val chat_animated_view_id = findViewById<ImageView>(R.id.chat_animated_view)

        chat_animated_view_id.setOnClickListener{
            chat_animated_view_id.animate().apply {
                duration = 1000
                rotationYBy(360f)
            }.withEndAction{
                chat_animated_view_id.animate().apply {
                    duration = 1000
                    rotationYBy(360f)
                }.start()
            }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}