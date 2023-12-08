@file:Suppress("ImplicitThis")

package com.example.chattingapp.Activity

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.WindowManager
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.chattingapp.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.DatabaseReference

@Suppress("DEPRECATION")
class LoginActivity : AppCompatActivity()
{
    private lateinit var auth: FirebaseAuth
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var databaseReference: DatabaseReference

    lateinit var googleSignInClient : GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        //hide status bar
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_login)


        //  Google Sign-In Start
        // Configure Google Sign In
        val googleID=findViewById<Button>(R.id.btnGoogle)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googleID.setOnClickListener{
            signIn()
        }

        // Google Sign-In End
        val actionBar = supportActionBar
        actionBar!!.hide()

        val topAnimation = AnimationUtils.loadAnimation(this,R.anim.top_animations)
        val bottomAnimation = AnimationUtils.loadAnimation(this,R.anim.bottom_animations)

        val SignIn_tv_loginActivity_id = findViewById<TextView>(R.id.SignIn_tv_loginActivity)
        val enteremail_id = findViewById<EditText>(R.id.enteremail)
        val enterpassword_id = findViewById<EditText>(R.id.enterpassword)
        val btnLogin_id = findViewById<Button>(R.id.btnLogin)
        val btnSignUp_id = findViewById<Button>(R.id.btnSignUp)
        val btnGoogle_id = findViewById<Button>(R.id.btnGoogle)

        SignIn_tv_loginActivity_id.animation = topAnimation
        enteremail_id.animation = topAnimation
        enterpassword_id.animation = topAnimation
        btnSignUp_id.animation = bottomAnimation
        btnLogin_id.animation = bottomAnimation
        btnGoogle_id.animation = bottomAnimation


        auth = FirebaseAuth.getInstance()


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

    // Start Google Sign-In 2nd Step
    val RC_SIGN_IN=65
    private fun signIn()
    {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN)
        {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try
            {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("TAG", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException)
            {
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

                    val intent=Intent(this,UsersActivity::class.java)
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