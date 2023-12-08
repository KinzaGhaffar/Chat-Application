@file:Suppress("ImplicitThis")

package com.example.chattingapp.Activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.view.animation.Animation
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.chattingapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity()
{
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser


    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        //hide status bar
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_login)

        val actionBar = supportActionBar
        actionBar!!.hide()

        val topAnimation = AnimationUtils.loadAnimation(this,R.anim.top_animations)
        val bottomAnimation = AnimationUtils.loadAnimation(this,R.anim.bottom_animations)

        val SignIn_tv_loginActivity_id = findViewById<TextView>(R.id.SignIn_tv_loginActivity)
        val enteremail_id = findViewById<EditText>(R.id.enteremail)
        val enterpassword_id = findViewById<EditText>(R.id.enterpassword)
        val btnLogin_id = findViewById<Button>(R.id.btnLogin)
        val btnSignUp_id = findViewById<Button>(R.id.btnSignUp)

        SignIn_tv_loginActivity_id.animation = topAnimation
        enteremail_id.animation = topAnimation
        enterpassword_id.animation = topAnimation
        btnSignUp_id.animation = bottomAnimation
        btnLogin_id.animation = bottomAnimation


        auth = FirebaseAuth.getInstance()
//        firebaseUser=auth.currentUser!!

        //check if user login then navigate user screen
//        if (firebaseUser != null)
//        {
//            val intent = Intent(this@LoginActivity,UsersActivity::class.java)
//            startActivity(intent)
//            finish()
//        }


        val btnlogin_id=findViewById<Button>(R.id.btnLogin)

        btnlogin_id.setOnClickListener {
            val enteremail_id=findViewById<EditText>(R.id.enteremail)
            val enterpassword_id=findViewById<EditText>(R.id.enterpassword)

            val email = enteremail_id.text.toString()
            val password = enterpassword_id.text.toString()

            if (TextUtils.isEmpty(email) && TextUtils.isEmpty(password))
            {
                Toast.makeText(
                    applicationContext,
                    "email and password are required",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else
            {
                auth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this)
                    {
                        if(it.isSuccessful)
                        {
                            enteremail_id.setText("")
                            enterpassword_id.setText("")
                            val intent=Intent(this@LoginActivity, UsersActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                        else
                        {
                            Toast.makeText(
                                applicationContext,
                                "email or password invalid!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
            }
        }
        val signupbtn_id=findViewById<Button>(R.id.btnSignUp)
        signupbtn_id.setOnClickListener{
            val intent=Intent(this@LoginActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}