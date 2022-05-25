package com.example.hiwasiiot.AlarmFirebase

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ReceiverOn : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("panduro1721@gmail.com", "papaya11")
            .addOnSuccessListener {
                val database1 = Firebase.database
                val led = database1.getReference("Led1")
                led.setValue("1")
                Log.d("on", "esta encendido")
            }
    }
}