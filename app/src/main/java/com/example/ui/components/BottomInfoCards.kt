package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.CyanBlueDim
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

/**
 * Two Horizontal Information Cards below the Prayer Table:
 * 1. Left: আজকের তারিখ (Today's Bengali & English Date + Calendar Icon)
 * 2. Right: পরবর্তী নামাজ (Next Prayer Name + Live Countdown + Mosque Silhouette)
 */
@Composable
fun BottomInfoCards(
    bengaliDate: String = "২৫ বৈশাখ, ১৪৩১",
    englishDate: String = "8 May, 2025",
    nextPrayerName: String = "ফজর",
    nextPrayerCountdown: String = "02:34:56",
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // Left Card: আজকের তারিখ
        DateInformationCard(
            bengaliDate = bengaliDate,
            englishDate = englishDate,
            modifier = Modifier.weight(1f)
        )

        // Right Card: পরবর্তী নামাজ
        NextPrayerCard(
            nextPrayerName = nextPrayerName,
            countdown = nextPrayerCountdown,
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Left Card: Today's Date
 */
@Composable
fun DateInformationCard(
    bengaliDate: String,
    englishDate: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(
                width = 1.dp,
                color = CyanBlue.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(10.dp)
            .testTag("date_info_card")
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "আজকের তারিখ",
                    color = PrimaryGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = bengaliDate,
                    color = TextWhite,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = englishDate,
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Normal
                )
            }

            // Glowing Cyan Calendar Icon
            GlowingCalendarIcon(modifier = Modifier.size(38.dp))
        }
    }
}

/**
 * Right Card: Next Prayer Countdown
 */
@Composable
fun NextPrayerCard(
    nextPrayerName: String,
    countdown: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(
                width = 1.dp,
                color = CyanBlue.copy(alpha = 0.5f),
                shape = RoundedCornerShape(12.dp)
            )
            .padding(10.dp)
            .testTag("next_prayer_card")
    ) {
        // Subtle Mosque Silhouette in background right
        MosqueSilhouetteGraphic(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .size(width = 65.dp, height = 75.dp)
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "পরবর্তী নামাজ",
                color = CyanBlue,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = nextPrayerName,
                color = PrimaryGreen,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = countdown,
                color = TextWhite,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace,
                letterSpacing = 0.5.sp
            )
        }
    }
}

/**
 * Cyan Glowing Calendar Vector Icon
 */
@Composable
private fun GlowingCalendarIcon(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        // Outer Calendar Box
        drawRoundRect(
            color = CyanBlue,
            topLeft = Offset(w * 0.12f, h * 0.20f),
            size = Size(w * 0.76f, h * 0.70f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(6f, 6f),
            style = Stroke(width = 2.2f)
        )

        // Top Rings / Tabs
        drawRoundRect(
            color = CyanBlue,
            topLeft = Offset(w * 0.28f, h * 0.08f),
            size = Size(w * 0.10f, h * 0.20f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
        )
        drawRoundRect(
            color = CyanBlue,
            topLeft = Offset(w * 0.62f, h * 0.08f),
            size = Size(w * 0.10f, h * 0.20f),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(3f, 3f)
        )

        // Top Divider line inside calendar
        drawLine(
            color = CyanBlue,
            start = Offset(w * 0.12f, h * 0.42f),
            end = Offset(w * 0.88f, h * 0.42f),
            strokeWidth = 1.8f
        )

        // Date Grid Dots
        val dotRadius = 2.2f
        val startX = w * 0.26f
        val startY = h * 0.54f
        val gapX = w * 0.16f
        val gapY = h * 0.14f

        for (row in 0..1) {
            for (col in 0..3) {
                drawCircle(
                    color = CyanBlue.copy(alpha = 0.9f),
                    radius = dotRadius,
                    center = Offset(startX + (col * gapX), startY + (row * gapY))
                )
            }
        }
    }
}

/**
 * Blue Layered Mosque Silhouette for Next Prayer Card
 */
@Composable
private fun MosqueSilhouetteGraphic(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height

        val silhouetteColor = Color(0xFF153A54).copy(alpha = 0.65f)
        val foregroundColor = Color(0xFF1E5276).copy(alpha = 0.85f)

        // Background Domes & Minarets
        val bgPath = Path().apply {
            // Left mini dome
            moveTo(0f, h)
            lineTo(0f, h * 0.55f)
            cubicTo(0f, h * 0.35f, w * 0.25f, h * 0.35f, w * 0.25f, h * 0.55f)
            // Center Big Dome
            lineTo(w * 0.30f, h * 0.45f)
            cubicTo(w * 0.30f, h * 0.15f, w * 0.70f, h * 0.15f, w * 0.70f, h * 0.45f)
            // Right mini dome
            lineTo(w * 0.75f, h * 0.55f)
            cubicTo(w * 0.75f, h * 0.35f, w, h * 0.35f, w, h * 0.55f)
            lineTo(w, h)
            close()
        }
        drawPath(bgPath, silhouetteColor)

        // Minaret Spire
        val minaret1 = Path().apply {
            moveTo(w * 0.12f, h)
            lineTo(w * 0.12f, h * 0.28f)
            lineTo(w * 0.16f, h * 0.18f)
            lineTo(w * 0.20f, h * 0.28f)
            lineTo(w * 0.20f, h)
            close()
        }
        drawPath(minaret1, foregroundColor)

        val minaret2 = Path().apply {
            moveTo(w * 0.80f, h)
            lineTo(w * 0.80f, h * 0.28f)
            lineTo(w * 0.84f, h * 0.18f)
            lineTo(w * 0.88f, h * 0.28f)
            lineTo(w * 0.88f, h)
            close()
        }
        drawPath(minaret2, foregroundColor)

        // Center Dome Crescent
        drawLine(
            color = CyanBlue.copy(alpha = 0.8f),
            start = Offset(w * 0.50f, h * 0.12f),
            end = Offset(w * 0.50f, h * 0.22f),
            strokeWidth = 1.5f
        )
    }
}
