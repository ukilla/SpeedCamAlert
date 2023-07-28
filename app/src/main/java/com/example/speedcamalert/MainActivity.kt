package com.example.speedcamalert

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        try {
            val toolbar: Toolbar = findViewById(R.id.my_toolbar)
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayShowTitleEnabled(false)
        } catch (e: Exception) {
            e.printStackTrace()
            Log.d("greska","MAJMUNEEE")
            Log.d("greska",e.toString())
        }
    }
}