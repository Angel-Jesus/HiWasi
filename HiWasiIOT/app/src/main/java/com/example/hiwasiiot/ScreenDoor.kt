package com.example.hiwasiiot

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.hiwasiiot.databinding.ActivityScreenDoorBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ScreenDoor : AppCompatActivity() {
    lateinit var binding: ActivityScreenDoorBinding
    private val TAG = "door"
    private var previous_state = "0"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityScreenDoorBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //Send and check state of chapa on Database
        firebaseStatusDoor(this)

        binding.btnBack.setOnClickListener { startActivity(Intent(this,MainActivity::class.java)) }

    }

    private fun firebaseStatusDoor(context: Context) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("panduro1721@gmail.com","papaya11")
            .addOnCompleteListener {
                val database = Firebase.database
                val door = database.getReference("chapa")
                door.addValueEventListener(object: ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val stateDoor = snapshot.getValue<String>() ?: "0"
                        if(stateDoor != previous_state){
                            previous_state = stateDoor

                            if(stateDoor == "1"){
                                binding.doorStatus.background = ContextCompat.getDrawable(context,R.color.open_door)
                            }else {
                                binding.doorStatus.background = ContextCompat.getDrawable(context,R.color.white)
                            }
                        }
                        binding.btnDoor.setOnClickListener {
                            binding.doorStatus.background = ContextCompat.getDrawable(context,R.color.open_door)
                            door.setValue("1")
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.w(TAG, "Failed to read value.", error.toException())
                    }
                })
            }
    }
}