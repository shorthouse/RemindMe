package dev.shorthouse.habitbuilder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dev.shorthouse.habitbuilder.databinding.ActivityMainBinding

// Hosts all fragments
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
    }
}