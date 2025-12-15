package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.ComponentActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.menu_screen)

        findViewById<Button>(R.id.btnImpostor).setOnClickListener {
            startActivity(Intent(this, ImpostorActivity::class.java))
        }
    }
}
