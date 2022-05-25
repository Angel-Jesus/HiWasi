package com.example.hiwasiiot

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import com.example.hiwasiiot.databinding.ActivityMainBinding
import com.example.hiwasiiot.sharePreference.Acces
import java.util.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.SplashScreen)
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getTime()
        statusConecction()
        Acces().Access()


        binding.doorBtn.setOnClickListener {startActivity(Intent(this,ScreenDoor::class.java))}
        binding.lightBtn.setOnClickListener { startActivity(Intent(this,ScreenLight::class.java)) }
        binding.gassBtn.setOnClickListener { startActivity(Intent(this,ScreenGass::class.java)) }
        binding.settingBtn.setOnClickListener { startActivity(Intent(this,ScreenConfig::class.java)) }

    }

    @SuppressLint("ResourceAsColor")
    private fun statusConecction() {
        val cm = this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = cm.activeNetworkInfo
        val isConnected = activeNetwork?.isConnectedOrConnecting == true
        binding.statusConecction.text = if(isConnected){ "Conexion exitosa" }else{ "Conexion fallida" }
        binding.statusConecction.setTextColor(
            if(isConnected){
                Color.parseColor("#82BA83")
            }else{
                Color.parseColor("#E46161")
            }
        )

    }

    private fun getTime() {
        val calendar = Calendar.getInstance()
        val m = calendar.get(Calendar.MONTH) + 1
        val d = calendar.get(Calendar.DAY_OF_MONTH)
        val month = if(m < 10){ "0$m" }else{ m.toString() }
        val year = calendar.get(Calendar.YEAR)
        val day = if(d < 10){ "0$d" }else{ d.toString() }
        val date_txt = "$day/$month/$year"
        val time = calendar.get(Calendar.HOUR_OF_DAY)
        println(time)
        binding.dayTime.text = when (time) {
            in 0..6 -> {
                "Madrugada"
            }
            in 7..12 -> {
                "Mañana"
            }
            in 13..18 -> {
                "Tarde"
            }
            else -> {
                "Noche"
            }
        }
        binding.date.text = date_txt
    }



}