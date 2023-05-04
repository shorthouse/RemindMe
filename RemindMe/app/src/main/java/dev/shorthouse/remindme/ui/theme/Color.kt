package dev.shorthouse.remindme.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

val md_theme_light_primary = Color(0xFF1051DD)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFDCE1FF)
val md_theme_light_onPrimaryContainer = Color(0xFF00164E)
val md_theme_light_secondary = Color(0xFF1051DD)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFDCE1FF)
val md_theme_light_onSecondaryContainer = Color(0xFF00164E)
val md_theme_light_tertiary = Color(0xFF1051DD)
val md_theme_light_onTertiary = Color(0xFFFFFFFF)
val md_theme_light_tertiaryContainer = Color(0xFFDCE1FF)
val md_theme_light_onTertiaryContainer = Color(0xFF00164E)
val md_theme_light_error = Color(0xFFBA1A1A)
val md_theme_light_errorContainer = Color(0xFFFFDAD6)
val md_theme_light_onError = Color(0xFFFFFFFF)
val md_theme_light_onErrorContainer = Color(0xFF410002)
val md_theme_light_background = Color(0xFFFFFFFF)
val md_theme_light_onBackground = Color(0xFF1B1B1F)
val md_theme_light_surface = Color(0xFFFFFFFF)
val md_theme_light_onSurface = Color(0xFF1B1B1F)
val md_theme_light_surfaceVariant = Color(0xFFE2E1EC)
val md_theme_light_onSurfaceVariant = Color(0xFF45464F)
val md_theme_light_outline = Color(0xFF767680)
val md_theme_light_inverseOnSurface = Color(0xFFF2F0F4)
val md_theme_light_inverseSurface = Color(0xFF303034)
val md_theme_light_inversePrimary = Color(0xFFB5C4FF)
val md_theme_light_surfaceTint = Color(0xFF1051DD)
val md_theme_light_outlineVariant = Color(0xFFC6C6D0)
val md_theme_light_scrim = Color(0xFF000000)

val md_theme_dark_primary = Color(0xFFB5C4FF)
val md_theme_dark_onPrimary = Color(0xFF00287C)
val md_theme_dark_primaryContainer = Color(0xFF003CAE)
val md_theme_dark_onPrimaryContainer = Color(0xFFDCE1FF)
val md_theme_dark_secondary = Color(0xFFB5C4FF)
val md_theme_dark_onSecondary = Color(0xFF00287C)
val md_theme_dark_secondaryContainer = Color(0xFF003CAE)
val md_theme_dark_onSecondaryContainer = Color(0xFFDCE1FF)
val md_theme_dark_tertiary = Color(0xFFB5C4FF)
val md_theme_dark_onTertiary = Color(0xFF00287C)
val md_theme_dark_tertiaryContainer = Color(0xFF003CAE)
val md_theme_dark_onTertiaryContainer = Color(0xFFDCE1FF)
val md_theme_dark_error = Color(0xFFFFB4AB)
val md_theme_dark_errorContainer = Color(0xFF93000A)
val md_theme_dark_onError = Color(0xFF690005)
val md_theme_dark_onErrorContainer = Color(0xFFFFDAD6)
val md_theme_dark_background = Color(0xFF1B1B1F)
val md_theme_dark_onBackground = Color(0xFFE4E2E6)
val md_theme_dark_surface = Color(0xFF37383D)
val md_theme_dark_onSurface = Color(0xFFE4E2E6)
val md_theme_dark_surfaceVariant = Color(0xFF45464F)
val md_theme_dark_onSurfaceVariant = Color(0xFFC6C6D0)
val md_theme_dark_outline = Color(0xFF8F909A)
val md_theme_dark_inverseOnSurface = Color(0xFF1B1B1F)
val md_theme_dark_inverseSurface = Color(0xFFE4E2E6)
val md_theme_dark_inversePrimary = Color(0xFF1051DD)
val md_theme_dark_surfaceTint = Color(0xFFB5C4FF)
val md_theme_dark_outlineVariant = Color(0xFF45464F)
val md_theme_dark_scrim = Color(0xFF000000)

val Green = Color(0xFF2E7D32)
val Red = Color(0xFFB00020)
val Blue = Color(0xFF2A60EB)
val Grey = Color(0xFF767680)

@Composable
fun ColorScheme.isLightColors() = this.background.luminance() > 0.5
