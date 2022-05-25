package com.example.hiwasiiot.sharePreference

import android.content.Context

class Prefer(context: Context) {
    private val ACTIVITY = "activity"
    private val ALARM = "destroy"
    private val RADIOBUTTON = "radioButton"
    private val TIMEON = "alarm_on"
    private val TIMEOFF = "alarm_off"

    private val setting = context.getSharedPreferences(ACTIVITY,0)

    fun getTimeOn():String{
        return setting.getString(TIMEON,"00:00") ?: ""
    }

    fun changeTimeOn(on:String){
        setting.edit().putString(TIMEON,on).apply()
    }

    fun getTimeOff():String{
        return setting.getString(TIMEOFF,"00:00") ?: ""
    }

    fun changeTimeOff(off:String){
        setting.edit().putString(TIMEOFF,off).apply()
    }

    fun getStateAlarm():Boolean{
        return setting.getBoolean(ALARM,false)
    }

    fun changeStateAlarm(state:Boolean){
        setting.edit().putBoolean(ALARM,state).apply()
    }

    fun getStateRadioButton():Int{
        return setting.getInt(RADIOBUTTON,-1)
    }

    fun changeStateRadioButton(id:Int){
        setting.edit().putInt(RADIOBUTTON,id).apply()
    }
}