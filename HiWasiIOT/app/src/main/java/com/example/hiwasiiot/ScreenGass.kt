package com.example.hiwasiiot

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.example.hiwasiiot.databinding.ActivityScreenGassBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlin.concurrent.thread

class ScreenGass : AppCompatActivity() {
    lateinit var binding: ActivityScreenGassBinding
    var hilo = true
    var previous_temp = ""
    var previous_humd = ""
    var previous_CO = ""
    var previous_GLP = ""
    val TAG = "tag1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityScreenGassBinding.inflate(layoutInflater)
        setContentView(binding.root)

        updateMessage()
        binding.btnBack.setOnClickListener { startActivity(Intent(this,MainActivity::class.java)) }

        thread(start = true) {

            while(hilo){
                runOnUiThread {
                    showData()
                    println("Se esta ejecutando el programa")
                }
                Thread.sleep(500)
            }
            println("Se cerro el programa correctamente")
        }

    }

    private fun showData() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("panduro1721@gmail.com","papaya11")
            .addOnCompleteListener{
                val database1 = Firebase.database
                val temp = database1.getReference("temp")
                val humd = database1.getReference("humd")
                val CO = database1.getReference("CO")
                val GLP = database1.getReference("GLP")

                temp.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val temp_data = snapshot.getValue<String>() ?: "0"
                        if(temp_data != previous_temp && temp_data != "nan"){
                            previous_temp = temp_data
                            binding.dateTemperature.text = temp_data
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.w(TAG, "Failed to read value.", error.toException())
                    }

                })

                humd.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val humd_data = snapshot.getValue<String>() ?: "0"
                        if(humd_data != previous_humd && humd_data != "nan"){
                            previous_humd = humd_data
                            binding.dateHumidity.text = humd_data
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.w(TAG, "Failed to read value.", error.toException())
                    }

                })

                CO.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val CO_data = snapshot.getValue<String>() ?: "0"
                        if(CO_data != previous_CO && CO_data != "nan"){
                            previous_CO = CO_data
                            binding.dateCo.text = CO_data
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.w(TAG, "Failed to read value.", error.toException())
                    }
                })

                GLP.addValueEventListener(object : ValueEventListener{
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val GLP_data = snapshot.getValue<String>() ?: "0"
                        if(GLP_data != previous_GLP && GLP_data != "nan"){
                            previous_GLP = GLP_data
                            binding.dateGlp.text = GLP_data
                        }
                    }
                    override fun onCancelled(error: DatabaseError) {
                        Log.w(TAG, "Failed to read value.", error.toException())
                    }

                })
                binding.progressBar.isGone = true
                binding.widgets.isGone = false

            }
    }

    private fun updateMessage() {
        FirebaseAuth.getInstance().signInWithEmailAndPassword("panduro1721@gmail.com","papaya11")
            .addOnCompleteListener{
                val database1 = Firebase.database
                val alarm = database1.getReference("alarm")
                alarm.setValue("no")
            }
    }

    override fun onPause() {
        //Funcion cuando se salga de la aplicacion
        super.onPause()
        hilo = false
        finish()
    }

}