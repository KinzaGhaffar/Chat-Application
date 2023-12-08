package com.example.chattingapp.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.media.Image
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chattingapp.Activity.ChatActivity
import com.example.chattingapp.Model.Chat
import com.example.chattingapp.Model.User
import com.example.chattingapp.R
import com.github.pgreze.reactions.ReactionPopup
import com.github.pgreze.reactions.dsl.reactionConfig
import com.github.pgreze.reactions.dsl.reactions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*
import kotlin.collections.ArrayList
import kotlin.String.Companion as String1

class ChatAdapter(private val context: Context, private val chatList: ArrayList<Chat>,val recid:String) :
        RecyclerView.Adapter<ChatAdapter.ViewHolder>()
{
    private val MESSAGE_TYPE_LEFT = 0
    private val MESSAGE_TYPE_RIGHT = 1
    var firebaseUser: FirebaseUser? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        if (viewType == MESSAGE_TYPE_RIGHT)
        {
            val view =LayoutInflater.from(parent.context).inflate(R.layout.item_right, parent, false)
            return ViewHolder(view)
        }
        else
        {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_left, parent, false)
            return ViewHolder(view)
        }
    }
    override fun getItemCount(): Int
    {
        return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val chat = chatList[position]
        holder.txtUserName.text = chat.message
        holder.itemView.setOnClickListener {
            val builder= AlertDialog.Builder(context)
            builder.setTitle("Delete")
            builder.setMessage("Do You Want to Delete?")
            builder.setPositiveButton("yes",{ dialogInterface: DialogInterface, i: Int ->
                val database= FirebaseDatabase.getInstance()
                val sender: String? =FirebaseAuth.getInstance().uid+recid
            })
            builder.setNegativeButton("no",{ dialogInterface: DialogInterface, i: Int ->
                dialogInterface.dismiss()
            })
            builder.show()
        }
       //chatList: ArrayList<Chat>
        //val chat = chatList[position]
        //val arry:ArrayList<Chat>

        val config = reactionConfig(context) {
            reactions {
                resId    { R.drawable.ic_fb_like }
                resId    { R.drawable.ic_fb_love }
                resId    { R.drawable.ic_fb_laugh }
                resId    { R.drawable.ic_fb_sad }
                resId    { R.drawable.ic_fb_wow }
                resId    { R.drawable.ic_fb_angry }
            }
        }
        val popup = ReactionPopup(context, config) { position -> true.also {
//            if (holder.itemViewType==MESSAGE_TYPE_RIGHT){
//                val viewholder=MESSAGE_TYPE_RIGHT
//                chat.message
//            }
        } }

        holder.itemView.setOnTouchListener { v, event ->
            popup.onTouch(v,event)
        }
        //Glide.with(context).load(user.profileImage).placeholder(R.drawable.profile_image).into(holder.imgUser)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view)
    {
        val txtUserName: TextView = view.findViewById(R.id.tvMessage)
        val imgUser: CircleImageView = view.findViewById(R.id.userImage)
        //val layoutUser: LinearLayout = view.findViewById(R.id.layoutUser)
    }

    override fun getItemViewType(position: Int): Int
    {
        firebaseUser = FirebaseAuth.getInstance().currentUser

        if (chatList[position].senderId == firebaseUser!!.uid)
        {
            return MESSAGE_TYPE_RIGHT
        }
        else
        {
            return MESSAGE_TYPE_LEFT
        }
    }
}