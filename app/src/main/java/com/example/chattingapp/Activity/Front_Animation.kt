package com.example.chattingapp.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import com.example.chattingapp.R

class Front_Animation : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_front__animation)

        val chat_animated_view_id = findViewById<ImageView>(R.id.chat_animated_view)

        val chatIcon = AnimationUtils.loadAnimation(this,R.anim.front_animation)
        chat_animated_view_id.animation = chatIcon

        chat_animated_view_id.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

//        chat_animated_view_id.setOnClickListener{
//            chat_animated_view_id.animate().apply {
//                duration = 1000
//                rotationYBy(360f)
//            }.withEndAction{
//                chat_animated_view_id.animate().apply {
//                    duration = 1000
//                    rotationYBy(360f)
//                }.start()
//            }
//
//            val intent = Intent(this, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        }

    }
}