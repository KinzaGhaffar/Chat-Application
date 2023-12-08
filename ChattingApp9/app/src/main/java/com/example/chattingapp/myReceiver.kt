package com.example.chattingapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class myReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
       Toast.makeText(context,"Chal gya",Toast.LENGTH_LONG).show()
    }
}