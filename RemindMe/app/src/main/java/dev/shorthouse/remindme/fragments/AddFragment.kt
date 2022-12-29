package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.AddReminderScreen
import dev.shorthouse.remindme.databinding.FragmentAddEditBinding
import dev.shorthouse.remindme.viewmodel.AddViewModel

@AndroidEntryPoint
class AddFragment : Fragment() {
    private lateinit var binding: FragmentAddEditBinding
    private val viewModel: AddViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitionAnimations()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddEditBinding.inflate(inflater, container, false)

        binding.addEditComposeView.apply {
            setViewCompositionStrategy(
                ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
            )

            setContent {
                MdcTheme {
                    AddReminderScreen(
                        addViewModel = viewModel,
                        onNavigateUp = { findNavController().navigateUp() },
                    )
                }
            }
        }

        return binding.root
    }

    private fun setTransitionAnimations() {
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.transition_duration_medium).toLong()
            excludeTarget(R.id.app_bar, true)
        }

        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.transition_duration_medium).toLong()
            excludeTarget(R.id.app_bar, true)
        }
    }
}
