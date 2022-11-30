package dev.shorthouse.remindme.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Blue500,
    onPrimary = OffBlack,
    secondary = Blue300,
    onSecondary = White,
    surface = White,
    onBackground = White
)

private val DarkColors = darkColorScheme(
    primary = Grey500,
    onPrimary = White,
    secondary = Blue500,
    onSecondary = OffBlack,
    surface = Grey900,
)

@Composable
fun RemindMeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        shapes = shapes,
        typography = typography,
        content = content
    )
}

