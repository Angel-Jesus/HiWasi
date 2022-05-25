package com.example.hiwasiiot.sharePreference

import android.annotation.SuppressLint
import android.app.Application
import com.google.firebase.auth.FirebaseAuth

class DataApplication: Application() {
    @SuppressLint("StaticFieldLeak")
    companion object{
        lateinit var prefer:Prefer
    }

    override fun onCreate() {
        super.onCreate()
        prefer = Prefer(applicationContext)
    }
}