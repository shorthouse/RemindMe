package dev.shorthouse.remindme.compose.navigation

sealed class Screen(val route: String) {
    object ListHome : Screen("list_home_screen")
    object Details : Screen("details_screen/{reminderId}") {
        fun createRoute(reminderId: Long) = "details_screen/$reminderId"
    }

    object Add : Screen("add_screen")
    object Edit : Screen("edit_screen/{reminderId}") {
        fun createRoute(reminderId: Long) = "edit_screen/$reminderId"
    }
}
