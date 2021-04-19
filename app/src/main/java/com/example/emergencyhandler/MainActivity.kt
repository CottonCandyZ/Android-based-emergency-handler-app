package com.example.emergencyhandler

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import cn.leancloud.AVInstallation
import cn.leancloud.push.PushService
import com.example.emergencyhandler.databinding.ActivityMainBinding
import com.example.emergencyhandler.model.MyViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private val myViewModel: MyViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // channel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = getString(R.string.channel_name)
            val descriptionText = getString(R.string.channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel("emergency", name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
            PushService.setDefaultChannelId(this, "emergency")
        }

        AVInstallation.getCurrentInstallation().saveInBackground()
        PushService.setDefaultPushCallback(this, MainActivity::class.java)
        PushService.subscribe(this, "emergency", MainActivity::class.java)


        // enable dataBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val host =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = host.navController
        NavigationUI.setupActionBarWithNavController(this, navController)
    }

    override fun onResume() {
        super.onResume()
        myViewModel.refresh()
        myViewModel.initLiveQuery()
    }

    override fun onSupportNavigateUp(): Boolean {
        navController.navigateUp()
        return super.onSupportNavigateUp()
    }
}