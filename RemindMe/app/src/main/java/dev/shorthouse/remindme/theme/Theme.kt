package dev.shorthouse.remindme.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightColors = lightColors(
    primary = Blue500,
    primaryVariant = Blue500,
    secondary = Blue500,
    secondaryVariant = Blue500,
    background = White,
    surface = White,
    error = Red,
    onPrimary = White,
    onSecondary = White,
    onBackground = Black,
    onSurface = Black,
    onError = White
)

private val DarkColors = darkColors(
    primary = Blue500,
    primaryVariant = Blue100,
    secondary = Blue500,
    secondaryVariant = Blue500,
    background = Grey700,
    surface = Grey500,
    error = Red,
    onPrimary = White,
    onSecondary = White,
    onBackground = White,
    onSurface = White,
    onError = White
)

@Composable
fun RemindMeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        shapes = RemindMeShapes,
        typography = RemindMeTypography,
        content = content
    )
}
