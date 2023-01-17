package dev.shorthouse.remindme.compose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dev.shorthouse.remindme.compose.screen.ReminderAddScreen
import dev.shorthouse.remindme.compose.screen.ReminderDetailsScreen
import dev.shorthouse.remindme.compose.screen.ReminderEditScreen
import dev.shorthouse.remindme.compose.screen.ReminderListHomeScreen

@Composable
fun RemindMeNavHost(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.ListHome.route,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(route = Screen.ListHome.route) {
            ReminderListHomeScreen(
                onNavigateAdd = { navController.navigate(Screen.Add.route) },
                onNavigateDetails = { reminderId ->
                    navController.navigate(Screen.Details.createRoute(reminderId))
                }
            )
        }

        composable(route = Screen.Add.route) {
            ReminderAddScreen(
                onNavigateUp = { navController.navigateUp() }
            )
        }

        composable(
            route = Screen.Details.route,
            arguments = listOf(navArgument(NavArgument.Detail.reminderId) { type = NavType.LongType })
        ) { backStackEntry ->
            ReminderDetailsScreen(
                reminderId = backStackEntry.arguments?.getLong(NavArgument.Detail.reminderId),
                onNavigateUp = { navController.navigateUp() },
                onEdit = { reminderId ->
                    navController.navigate(Screen.Edit.createRoute(reminderId))
                }
            )
        }

        composable(
            route = Screen.Edit.route,
            arguments = listOf(navArgument(NavArgument.Edit.reminderId) { type = NavType.LongType })
        ) { backStackEntry ->
            ReminderEditScreen(
                reminderId = backStackEntry.arguments?.getLong(NavArgument.Edit.reminderId),
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}
