package com.example.chattingapp.Activity

import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import java.util.*
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.*
import com.example.chattingapp.R
import com.example.chattingapp.myReceiver
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity()
{
    val SPLASH_SCREEN = 5000

    private lateinit var topAnimation : Animation
    private lateinit var bottomAnimation : Animation

    private lateinit var auth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference

    lateinit var googleSignInClient : GoogleSignInClient

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        //hide status bar
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_main)

        //  FB Sign-In Start
        val fbID=findViewById<Button>(R.id.btnFB)
        // FB Sign-In End

        //  Google Sign-In Start
        // Configure Google Sign In
        val googleID=findViewById<Button>(R.id.btnGoogle)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleID.setOnClickListener(View.OnClickListener {
           signIn()
        })
        // Google Sign-In End

        val actionBar = supportActionBar
        actionBar!!.hide()

        topAnimation = AnimationUtils.loadAnimation(this,R.anim.top_animations)
        bottomAnimation = AnimationUtils.loadAnimation(this,R.anim.bottom_animations)

        val chattingApp_tv_main_id = findViewById<TextView>(R.id.chattingApp_tv_main)
        val chat_animated_view_id = findViewById<ImageView>(R.id.chat_animated_view)
        val SignUp_tv_main_id = findViewById<TextView>(R.id.SignUp_tv_main)
        val entername_id = findViewById<EditText>(R.id.entername)
        val enteremail_id = findViewById<EditText>(R.id.enteremail)

        val enterpassword_id = findViewById<EditText>(R.id.enterpassword)
        val enterconfirmpassword_id = findViewById<EditText>(R.id.enterconfirmpassword)
        val btnSignUp_id = findViewById<Button>(R.id.btnSignUp)
        val btnLogin_id = findViewById<Button>(R.id.btnLogin)

        chattingApp_tv_main_id.animation = topAnimation
        chat_animated_view_id.animation = topAnimation
        SignUp_tv_main_id.animation = topAnimation
        entername_id.animation = topAnimation
        enteremail_id.animation = topAnimation
        enterpassword_id.animation = bottomAnimation
        enterconfirmpassword_id.animation = bottomAnimation
        btnSignUp_id.animation = bottomAnimation
        btnLogin_id.animation = bottomAnimation

//        Handler().postDelayed({
//            val intent = Intent(this, ChatActivity::class.java)
//            startActivity(intent)
//            finish()
//        }, SPLASH_SCREEN.toLong())

        chat_animated_view_id.setOnClickListener{
            chat_animated_view_id.animate().apply {
                duration=1000
                rotationYBy(360f)
            }.withEndAction{
                chat_animated_view_id.animate().apply {
                    duration=1000
                    rotationYBy(360f)
                }.start()
            }
        }

        ////Broadcasting////
        val myReceiver = myReceiver()
        val intentfilter = IntentFilter()

        intentfilter.addAction("android.intent.action.AIRPLANE_MODE")
        registerReceiver(myReceiver,intentfilter)
        ////Broadcasting////


        auth = FirebaseAuth.getInstance()

        //val btnSignUp_id=findViewById<Button>(R.id.btnSignUp)
        btnSignUp_id.setOnClickListener{
            val entername_id=findViewById<EditText>(R.id.entername)
            val enteremail_id=findViewById<EditText>(R.id.enteremail)
            val enterpassword_id=findViewById<EditText>(R.id.enterpassword)
            val enterconfirmpassword_id=findViewById<EditText>(R.id.enterconfirmpassword)

            val userName = entername_id.text.toString()
            val email = enteremail_id.text.toString()
            val password = enterpassword_id.text.toString()
            val confirmPassword = enterconfirmpassword_id.text.toString()

            if (TextUtils.isEmpty(userName))
            {
                Toast.makeText(applicationContext,"username is required",Toast.LENGTH_SHORT).show()
            }
            if (TextUtils.isEmpty(email))
            {
                Toast.makeText(applicationContext,"email is required",Toast.LENGTH_SHORT).show()
            }

            if (TextUtils.isEmpty(password))
            {
                Toast.makeText(applicationContext,"password is required",Toast.LENGTH_SHORT).show()
            }

            if (TextUtils.isEmpty(confirmPassword))
            {
                Toast.makeText(applicationContext,"confirm password is required",Toast.LENGTH_SHORT).show()
            }

            if (password!=confirmPassword)
            {
                Toast.makeText(applicationContext,"password not match",Toast.LENGTH_SHORT).show()
            }
            registerUser(userName,email,password)

        }
        val btnlogin_id=findViewById<Button>(R.id.btnLogin)
        btnlogin_id.setOnClickListener {
            val intent = Intent(this@MainActivity,
                LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun registerUser(userName:String,email:String,password:String)
    {
        auth.createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(this){
                if (it.isSuccessful)
                {
                    val user: FirebaseUser? = auth.currentUser
                    val userId:String = user!!.uid

                    databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userId)

                    val hashMap:HashMap<String,String> = HashMap()
                    hashMap.put("userId",userId)
                    hashMap.put("userName",userName)
                    hashMap.put("profileImage","")

                    databaseReference.setValue(hashMap).addOnCompleteListener(this){
                        if (it.isSuccessful)
                        {
                            /* open home activity */
                            val intent = Intent(this@MainActivity, UsersActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
    }

    // Start Google Sign-In 2nd Step
    val RC_SIGN_IN=65
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("TAG", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithCredential:success")
                    val user = auth.currentUser

                    val intent=Intent(this@MainActivity,UsersActivity::class.java)
                    startActivity(intent)
                    Toast.makeText(this,"Sign-In Success",Toast.LENGTH_SHORT).show()
                    //updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    //updateUI(null)
                }
            }
    }
    // Start Google Sign-In 2nd Step
}