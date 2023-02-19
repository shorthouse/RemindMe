package dev.shorthouse.remindme.ui.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.shorthouse.remindme.R

private val Roboto = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_bold, FontWeight.Bold)
)

val RemindMeTypography = Typography(
    defaultFontFamily = Roboto,
    h5 = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
    ),
    h6 = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Medium,
    ),
    body1 = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal,
    ),
    body2 = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        letterSpacing = 0.25.sp
    ),
    subtitle1 = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal,
        color = SubtitleGrey
    ),
    subtitle2 = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.Normal,
    ),
    button = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    ),
    caption = TextStyle(
        fontSize = 15.sp,
        letterSpacing = 0.2.sp,
        fontWeight = FontWeight.Medium
    )
)
