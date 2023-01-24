package dev.shorthouse.remindme.compose.navigation

import androidx.compose.runtime.Composable
import com.ramcosta.composedestinations.DestinationsNavHost
import dev.shorthouse.remindme.compose.screen.NavGraphs

@Composable
fun RemindMeNavHost() {
    DestinationsNavHost(navGraph = NavGraphs.root)
}
