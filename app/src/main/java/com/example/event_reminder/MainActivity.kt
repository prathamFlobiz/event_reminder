package com.example.event_reminder

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.viewpager2.widget.ViewPager2
import com.example.event_reminder.adapters.MonthAdapter
import com.example.event_reminder.util.ActionType
import com.example.event_reminder.util.AlarmReceiver
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import io.realm.Realm
import java.util.*

class MainActivity : AppCompatActivity() {
    private val months =
        arrayOf("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep", "oct", "nov", "dec")
    var calendar: Calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Realm.init(this)

        val tabLayout: TabLayout = findViewById(R.id.tabLayout)
        val viewPager: ViewPager2 = findViewById(R.id.viewPager)

        val adapter = MonthAdapter(supportFragmentManager, lifecycle)
        viewPager.adapter = adapter
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = months[position]
        }.attach()
        tabLayout.getTabAt(calendar.get(Calendar.MONTH))?.select()
        createChannel("channel", "Daily")
        Log.d("NOTIFICATION", isNotificationSet().toString())
        if (!isNotificationSet())
            setupDailyNotification()
    }

    private fun createChannel(channelId: String, channelName: String) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                channelId,
                channelName,

                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "For Daily Reminder"

            val notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    private fun setupDailyNotification() {
        //Setting Shared Preference
        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putBoolean("notification", true)
            apply()
        }
        val alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager
        val contentIntent = Intent(applicationContext, AlarmReceiver::class.java)
        val contentPendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            0,
            contentIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        cal.set(Calendar.HOUR_OF_DAY, 9)
        cal.set(Calendar.MINUTE, 0)
        if (cal.before(Calendar.getInstance())) {
            cal.add(Calendar.DAY_OF_YEAR, 1);
        }
        Log.d("NOTIFICATION", "${cal.timeInMillis},${Calendar.getInstance().timeInMillis}")
        alarmManager.setWindow(
            AlarmManager.RTC_WAKEUP,
            cal.timeInMillis,
            1000,
            contentPendingIntent
        )
    }

    private fun isNotificationSet(): Boolean {
        val sharedPref = this?.getPreferences(Context.MODE_PRIVATE)
        return sharedPref.getBoolean("notification", false)
    }
}