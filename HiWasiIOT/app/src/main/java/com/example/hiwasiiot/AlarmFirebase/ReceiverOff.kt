package com.example.hiwasiiot.AlarmFirebase

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class ReceiverOff :BroadcastReceiver() {
    override fun onReceive(p0: Context?, p1: Intent?) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("panduro1721@gmail.com", "papaya11")
            .addOnSuccessListener {
                val database1 = Firebase.database
                val led = database1.getReference("Led1")
                led.setValue("0")
                Log.d("off","esta apagado")
            }
    }
}