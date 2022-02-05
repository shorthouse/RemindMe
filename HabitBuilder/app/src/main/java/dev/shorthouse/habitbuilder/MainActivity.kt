package dev.shorthouse.habitbuilder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

// Hosts all fragments
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }
}