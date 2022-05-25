package com.example.hiwasiiot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.hiwasiiot.databinding.ActivityScreenLightBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase

class ScreenLight : AppCompatActivity() {
    lateinit var binding: ActivityScreenLightBinding
    val TAG = "tag"
    var previus_state = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityScreenLightBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBack.setOnClickListener{startActivity(Intent(this,MainActivity::class.java))}
        StateLed()

    }

    private fun StateLed() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("panduro1721@gmail.com","papaya11")
            .addOnCompleteListener {
                val database1 = Firebase.database
                val led = database1.getReference("Led1")

                led.addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {

                        var state = snapshot.getValue<String>() ?: "0"
                        if(previus_state != state){
                            previus_state = state
                            if(state == "0"){
                                binding.fondo1.background = ContextCompat.getDrawable(this@ScreenLight,R.color.white)
                                binding.fondo2.background = ContextCompat.getDrawable(this@ScreenLight,R.color.white)
                            }else{
                                binding.fondo1.background = ContextCompat.getDrawable(this@ScreenLight,R.drawable.light_on)
                                binding.fondo2.background = ContextCompat.getDrawable(this@ScreenLight,R.drawable.light_on)
                            }
                        }
                        binding.lightStatus.setOnClickListener {
                            if(state == "0"){
                                led.setValue("1")
                                binding.fondo1.background = ContextCompat.getDrawable(this@ScreenLight,R.drawable.light_on)
                                binding.fondo2.background = ContextCompat.getDrawable(this@ScreenLight,R.drawable.light_on)
                            }else{
                                led.setValue("0")
                                binding.fondo1.background = ContextCompat.getDrawable(this@ScreenLight,R.color.white)
                                binding.fondo2.background = ContextCompat.getDrawable(this@ScreenLight,R.color.white)
                            }
                        }

                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.w(TAG, "Failed to read value.", error.toException())
                    }
                })

            }

    }
}