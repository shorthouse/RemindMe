package dev.shorthouse.habitbuilder

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationBarView
import dev.shorthouse.habitbuilder.databinding.ActivityMainBinding
import dev.shorthouse.habitbuilder.fragments.AllReminderListFragmentDirections
import dev.shorthouse.habitbuilder.fragments.ActiveReminderListFragmentDirections

class MainActivity : AppCompatActivity() {
    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_container) as NavHostFragment
        val navController = navHostFragment.navController
        appBarConfiguration = AppBarConfiguration(navController.graph)
        setupActionBarWithNavController(navController, appBarConfiguration)

        binding.apply {
            addReminderFab.setOnClickListener {
                findNavController(R.id.nav_host_container).navigate(R.id.action_activeReminderListFragment_to_addReminderFragment)
            }

            bottomNavigation.setOnItemSelectedListener { item ->
                when(item.itemId) {
                    R.id.active_reminders -> {
                        val action = AllReminderListFragmentDirections
                            .actionAllReminderListFragmentToActiveReminderListFragment()
                        navController.navigate(action)
                        true
                    }
                    R.id.all_reminders -> {
                        val action = ActiveReminderListFragmentDirections
                            .actionActiveReminderListFragmentToAllReminderListFragment()
                        navController.navigate(action)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_container)
        return navController.navigateUp(appBarConfiguration)
                || super.onSupportNavigateUp()
    }


}