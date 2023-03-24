package dev.shorthouse.remindme.ui

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.DestinationsNavHost

@Composable
fun AppNavHost() {
    DestinationsNavHost(navGraph = NavGraphs.root)
}
