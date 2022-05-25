package com.example.hiwasiiot.TimePicker

import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.fragment.app.DialogFragment
import java.util.*

class timePicker(val listener: (String) -> Unit):DialogFragment(), TimePickerDialog.OnTimeSetListener {

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minuteOfDay: Int) {
        val hour = if(hourOfDay < 10){ "0$hourOfDay" }else{ hourOfDay.toString() }
        val minute = if(minuteOfDay < 10){ "0$minuteOfDay" }else{ minuteOfDay.toString() }
        listener("$hour:$minute")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)
        return TimePickerDialog(activity as Context, this, hour, minute, false)
    }
}