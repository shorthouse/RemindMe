package dev.shorthouse.remindme.ui

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.DestinationsNavHost
import dev.shorthouse.remindme.ui.screen.NavGraphs

@Composable
fun AppNavHost() {
    DestinationsNavHost(navGraph = NavGraphs.root)
}
