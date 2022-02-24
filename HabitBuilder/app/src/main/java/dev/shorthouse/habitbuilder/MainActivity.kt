package dev.shorthouse.habitbuilder

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import dev.shorthouse.habitbuilder.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            addReminderFab.setOnClickListener {
                findNavController(R.id.nav_host_container).navigate(R.id.action_activeReminderListFragment_to_addReminderFragment)
            }
            bottomNavView.background = null
        }
    }


}