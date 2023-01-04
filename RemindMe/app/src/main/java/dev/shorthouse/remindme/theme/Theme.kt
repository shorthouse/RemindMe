package dev.shorthouse.remindme.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.MaterialTheme.shapes
import androidx.compose.material.MaterialTheme.typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightColors = lightColors(
    primary = Blue500,
    onPrimary = OffBlack,
    secondary = Blue300,
    onSecondary = White,
    surface = White,
    onSurface = Blue500,
    onBackground = White
)

private val DarkColors = darkColors(
    primary = Grey500,
    onPrimary = White,
    secondary = Blue500,
    onSecondary = OffBlack,
    surface = Grey900,
    onSurface = Blue500
)

@Composable
fun RemindMeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        shapes = shapes,
        typography = typography,
        content = content
    )
}
