package dev.shorthouse.remindme.compose.navigation

sealed class NavArgument {
    class Detail {
        companion object {
            const val reminderId = "reminderId"
        }
    }

    class Edit {
        companion object {
            const val reminderId = "reminderId"
        }
    }
}
