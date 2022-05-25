package com.example.hiwasiiot

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.AlertDialog
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import com.example.hiwasiiot.AlarmFirebase.ReceiverOff
import com.example.hiwasiiot.AlarmFirebase.ReceiverOn
import com.example.hiwasiiot.TimePicker.timePicker
import com.example.hiwasiiot.databinding.ActivityScreenConfigBinding
import com.example.hiwasiiot.sharePreference.DataApplication
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import java.util.*

class ScreenConfig : AppCompatActivity() {
    lateinit var binding: ActivityScreenConfigBinding
    lateinit var alarmManager: AlarmManager
    lateinit var pendingIntentOn: PendingIntent
    lateinit var pendingIntentOff: PendingIntent
    private val TAG = "token"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        binding = ActivityScreenConfigBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //Get save data on SharePreference
        val state = DataApplication.prefer.getStateAlarm()
        val radioId = DataApplication.prefer.getStateRadioButton()
        binding.alarmActive.isChecked = state
        binding.alarmActive.text = if(state){"Activado"}else{"Desactivado"}

        binding.howOften.check(radioId)
        binding.timeOn.setText(DataApplication.prefer.getTimeOn())
        binding.timeOff.setText(DataApplication.prefer.getTimeOff())

        binding.setAlarm.isGone = !state

        //Button set Alarm to send data to database
        binding.setAlarm.setOnClickListener {
            //Create an AlertDialog
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Programacion de On/Off de luces")
            builder.setMessage("¿Desea guardar los cambios para el encendido y apagado de las luces?")
            builder.setPositiveButton("Guardar"){ btn,_ ->
                startAlarm(radioId)
                btn.cancel()
            }
            builder.setNegativeButton("Descartar"){btn,_ ->
                btn.cancel()
            }
            builder.show()
        }

        //Button to get token
        binding.getToken.setOnClickListener { getTokenId() }

        //Selecte time to on and off
        binding.timeOn.setOnClickListener { showTime(it as EditText) }
        binding.timeOff.setOnClickListener { showTime(it as EditText) }

        //RadioButton state
        binding.oneTime.setOnClickListener { DataApplication.prefer.changeStateRadioButton(it.id) }
        binding.everyDay.setOnClickListener { DataApplication.prefer.changeStateRadioButton(it.id) }

        //Case start configuration on/off
        binding.alarmActive.setOnClickListener { stateSwitch() }

        //Button Back
        binding.btnBack.setOnClickListener { startActivity(Intent(this,MainActivity::class.java)) }
    }

    private fun getTokenId() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            // Get new FCM registration token
            val token = task.result
            // Log and toast
            val msg = token

            FirebaseAuth.getInstance().signInWithEmailAndPassword("panduro1721@gmail.com","papaya11")
                .addOnCompleteListener {
                    val database1 = Firebase.database
                    val token_ref = database1.getReference("token")
                    token_ref.setValue(msg)
                }
        })
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun startAlarm(radioId: Int) {

        //Set the alarm On
        val time1 = binding.timeOn.text.split(":")
        val hour1 = time1[0]
        val minute1 = time1[1]
        println("${hour1.toInt()}-${minute1.toInt()}")

        val calendar1 = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY,hour1.toInt())
            set(Calendar.MINUTE,minute1.toInt())
            set(Calendar.SECOND,0)
            set(Calendar.MILLISECOND,0)
        }

        //Set alarm Off
        val time2 = binding.timeOff.text.split(":")
        val hour2 = time2[0]
        val minute2 = time2[1]
        println("${hour2.toInt()}-${minute2.toInt()}")
        val calendar2 = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY,hour2.toInt())
            set(Calendar.MINUTE,minute2.toInt())
            set(Calendar.SECOND,0)
            set(Calendar.MILLISECOND,0)
        }

        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val intentOn = Intent(this,ReceiverOn::class.java)
        val intentOff = Intent(this, ReceiverOff::class.java)
        pendingIntentOn = PendingIntent.getBroadcast(this,0,intentOn,PendingIntent.FLAG_ONE_SHOT)
        pendingIntentOff = PendingIntent.getBroadcast(this,1,intentOff,PendingIntent.FLAG_ONE_SHOT)


        if(radioId == binding.everyDay.id){

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar1.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntentOn
            )

            alarmManager.setRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar2.timeInMillis,
                AlarmManager.INTERVAL_DAY,
                pendingIntentOff
            )

        }else{
            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar1.timeInMillis,
                pendingIntentOn
            )

            alarmManager.set(
                AlarmManager.RTC_WAKEUP,
                calendar2.timeInMillis,
                pendingIntentOff
            )
        }
        Toast.makeText(this,"Programacion del encendido y apagado de luces exitosa",Toast.LENGTH_SHORT).show()
    }

    @SuppressLint("UnspecifiedImmutableFlag")
    private fun stateSwitch() {
        binding.setAlarm.isGone = true
        val state = binding.alarmActive.isChecked
        DataApplication.prefer.changeStateAlarm(state)
        binding.alarmActive.text = if(state){"Activado"}else{"Desactivado"}

        if(binding.alarmActive.isChecked.not()){
            alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intentOn = Intent(this,ReceiverOn::class.java)
            val intentOff = Intent(this, ReceiverOff::class.java)
            pendingIntentOn = PendingIntent.getBroadcast(this,0,intentOn,PendingIntent.FLAG_ONE_SHOT)
            pendingIntentOff = PendingIntent.getBroadcast(this,1,intentOff,PendingIntent.FLAG_ONE_SHOT)

            alarmManager.cancel(pendingIntentOn)
            alarmManager.cancel(pendingIntentOff)
            Log.d("cancel","Alarma cancelada")
        }else{
            binding.setAlarm.isGone = false
        }
    }

    private fun showTime(edit: EditText) {
        val Picker = timePicker { timeSelected(it,edit) }
        Picker.show(supportFragmentManager,"timePicker")
    }

    private fun timeSelected(time:String, view: EditText) {
        view.setText(time)
        if(binding.timeOn == view){
            DataApplication.prefer.changeTimeOn(time)
        }else{
            DataApplication.prefer.changeTimeOff(time)
        }
    }

}