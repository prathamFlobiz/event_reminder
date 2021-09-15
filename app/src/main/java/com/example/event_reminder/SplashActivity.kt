package com.example.event_reminder

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.WindowManager
import java.util.*
import java.util.TimerTask





class SplashActivity : AppCompatActivity() {
    //Timer for starting MainActivity
    var timer = Timer()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        startTimer()
    }
    override fun onPause() {
        super.onPause()
        timer.cancel()
    }
    fun launchMainActivity(){
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun startTimer(){
        val timerTask: TimerTask = object : TimerTask() {
            override fun run() {
                launchMainActivity()
            }
        }
        timer.schedule(timerTask,5000)
    }
}