package com.xauusd.pro2030

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val statusText = findViewById<TextView>(R.id.projectStatus)
        statusText.text = buildString {
            append("XAUUSD PRO 2030\n")
            append("Foundation started successfully")
        }
    }
}
