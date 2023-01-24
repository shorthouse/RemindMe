package dev.shorthouse.remindme.theme

import androidx.compose.material.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import dev.shorthouse.remindme.R

private val Inter = FontFamily(
    Font(R.font.inter_light, FontWeight.Light),
    Font(R.font.inter_regular, FontWeight.Normal),
    Font(R.font.inter_bold, FontWeight.Bold),
    Font(R.font.inter_tight_medium, FontWeight.SemiBold)
)

val RemindMeTypography = Typography(
    defaultFontFamily = Inter,
    h5 = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
    ),
    h6 = TextStyle(
        fontSize = 20.sp,
        fontWeight = FontWeight.Bold,
    ),
    body1 = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal,
    ),
    body2 = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    ),
    subtitle1 = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Normal,
        color = SubtitleGrey
    ),
    subtitle2 = TextStyle(
        fontSize = 14.sp,
        fontWeight = FontWeight.Normal,
        color = SubtitleGrey
    ),
    button = TextStyle(
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    ),
    caption = TextStyle(
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold
    )
)
