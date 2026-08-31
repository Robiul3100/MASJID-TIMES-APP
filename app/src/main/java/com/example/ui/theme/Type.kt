package com.example.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.R

val SolaimanLipiFontFamily = FontFamily(
    Font(R.font.solaiman_lipi, FontWeight.Normal),
    Font(R.font.solaiman_lipi, FontWeight.Medium),
    Font(R.font.solaiman_lipi, FontWeight.Bold)
)

val AmiriFontFamily = FontFamily(
    Font(R.font.amiri_regular, FontWeight.Normal),
    Font(R.font.amiri_regular, FontWeight.Bold)
)

val Typography = Typography(
    headlineLarge = TextStyle(
        fontFamily = SolaimanLipiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 28.sp,
        lineHeight = 34.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = SolaimanLipiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = SolaimanLipiFontFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = SolaimanLipiFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = SolaimanLipiFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = SolaimanLipiFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 18.sp
    ),
    labelLarge = TextStyle(
        fontFamily = SolaimanLipiFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = SolaimanLipiFontFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp,
        lineHeight = 14.sp
    )
)

object AppTypography {
    val screenTitle = TextStyle(
        fontFamily = SolaimanLipiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        color = TextWhite
    )
    val screenSubtitle = TextStyle(
        fontFamily = SolaimanLipiFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        color = TextMuted
    )
    val cardTitle = TextStyle(
        fontFamily = SolaimanLipiFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 15.sp,
        color = PrimaryGreen
    )
    val bodyBengali = TextStyle(
        fontFamily = SolaimanLipiFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        color = TextWhite
    )
    val arabicLarge = TextStyle(
        fontFamily = AmiriFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 24.sp,
        color = TextWhite
    )
}
