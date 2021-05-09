package com.example.emergencyhandler

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

class PushCallBackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_push_call_back)
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}