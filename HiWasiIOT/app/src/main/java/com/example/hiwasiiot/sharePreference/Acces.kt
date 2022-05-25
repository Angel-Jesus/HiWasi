package com.example.hiwasiiot.sharePreference

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class Acces {
    fun Access(){
        FirebaseAuth.getInstance().signInWithEmailAndPassword("panduro1721@gmail.com","papaya11")
            .addOnCompleteListener{
                Firebase.database.getReference("Led1").addValueEventListener(object:
                    ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {}
                    override fun onCancelled(error: DatabaseError) {}
                    }
                )
            }
    }
}