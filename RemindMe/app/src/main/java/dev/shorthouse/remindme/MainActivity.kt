package dev.shorthouse.remindme

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.databinding.ActivityMainBinding

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val currentNavigationFragment: Fragment?
        get() = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_container)
            ?.childFragmentManager
            ?.fragments
            ?.first()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setTransitionAnimations()
    }

    private fun setTransitionAnimations() {
        currentNavigationFragment?.apply {
            exitTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
                duration = resources.getInteger(R.integer.transition_duration_medium).toLong()
                excludeTarget(R.id.app_bar, true)
            }
            reenterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
                duration = resources.getInteger(R.integer.transition_duration_medium).toLong()
                excludeTarget(R.id.app_bar, true)
            }
        }
    }
}
