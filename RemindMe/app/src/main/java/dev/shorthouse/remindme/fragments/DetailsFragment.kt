package dev.shorthouse.remindme.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.android.material.transition.MaterialSharedAxis
import dagger.hilt.android.AndroidEntryPoint
import dev.shorthouse.remindme.R
import dev.shorthouse.remindme.compose.ReminderDetailsScreen
import dev.shorthouse.remindme.databinding.FragmentDetailsBinding
import dev.shorthouse.remindme.viewmodel.DetailsViewModel

@AndroidEntryPoint
class DetailsFragment : Fragment() {
    private lateinit var binding: FragmentDetailsBinding
    private val navigationArgs: DetailsFragmentArgs by navArgs()
    private val viewModel: DetailsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTransitionAnimations()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false).apply {
            detailsComposeView.apply {
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                )

                val editAction = DetailsFragmentDirections.actionDetailsToEdit(navigationArgs.id)

                setContent {
                    MdcTheme {
                        ReminderDetailsScreen(
                            detailsViewModel = viewModel,
                            onEdit = { findNavController().navigate(editAction) },
                            onNavigateUp = { findNavController().navigateUp() },
                        )
                    }
                }
            }
        }

        return binding.root
    }

    private fun setTransitionAnimations() {
        val forwardTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true).apply {
            duration = resources.getInteger(R.integer.transition_duration_medium).toLong()
            excludeTarget(R.id.app_bar, true)
        }

        val backwardTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false).apply {
            duration = resources.getInteger(R.integer.transition_duration_medium).toLong()
            excludeTarget(R.id.app_bar, true)
        }

        enterTransition = forwardTransition
        exitTransition = forwardTransition
        returnTransition = backwardTransition
        reenterTransition = backwardTransition
    }
}
