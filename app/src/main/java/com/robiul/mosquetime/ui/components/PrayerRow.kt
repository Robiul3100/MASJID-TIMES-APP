package com.robiul.mosquetime.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.ui.theme.DarkGreen
import com.robiul.mosquetime.ui.theme.DarkSurfaceBorder
import com.robiul.mosquetime.ui.theme.GoldAccent
import com.robiul.mosquetime.ui.theme.GreenDigital
import com.robiul.mosquetime.ui.theme.NeonGreenGlow
import com.robiul.mosquetime.ui.theme.PrimaryGreen
import com.robiul.mosquetime.ui.theme.RedDigital

enum class PrayerType {
    FAJR, DHUHR, ASR, MAGHRIB, ISHA, JUMAH, SUNRISE_SEHRI, SUNSET_IFTAR
}

/**
 * Individual Prayer Timetable Row
 */
@Composable
fun PrayerRow(
    type: PrayerType,
    bengaliName: String,
    arabicName: String,
    timeString: String,
    isJumah: Boolean = false,
    isActive: Boolean = false,
    modifier: Modifier = Modifier
) {
    val prayerColor = if (isJumah) RedDigital else PrimaryGreen
    val timeActiveColor = if (isJumah) GreenDigital else RedDigital

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (isActive) {
                    Modifier.background(
                        DarkGreen.copy(alpha = 0.25f),
                        RoundedCornerShape(4.dp)
                    )
                } else Modifier
            )
            .padding(horizontal = 8.dp, vertical = 6.5.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Icon + Bengali Name
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1.3f)
            ) {
                PrayerIcon(type = type, isJumah = isJumah, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = bengaliName,
                    color = prayerColor,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Center: Arabic Badge
            Box(
                modifier = Modifier.weight(1.0f),
                contentAlignment = Alignment.Center
            ) {
                ArabicPrayerBadge(arabicName = arabicName)
            }

            // Right: Digital 7-Segment Time
            Box(
                modifier = Modifier.weight(1.1f),
                contentAlignment = Alignment.CenterEnd
            ) {
                TableDigitalTime(
                    timeString = timeString,
                    activeColor = timeActiveColor
                )
            }
        }
    }
}

/**
 * Custom vector prayer icon matching reference image
 */
@Composable
fun PrayerIcon(
    type: PrayerType,
    isJumah: Boolean,
    modifier: Modifier = Modifier
) {
    val iconColor = if (isJumah) RedDigital else PrimaryGreen

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cX = w / 2f
        val cY = h / 2f

        when (type) {
            PrayerType.FAJR -> {
                // Sunrise rising above horizon
                drawLine(
                    color = iconColor,
                    start = Offset(2f, h * 0.72f),
                    end = Offset(w - 2f, h * 0.72f),
                    strokeWidth = 2f
                )
                // Half sun
                drawArc(
                    color = iconColor,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(w * 0.22f, h * 0.38f),
                    size = Size(w * 0.56f, h * 0.56f)
                )
                // Rays
                drawLine(iconColor, Offset(cX, h * 0.16f), Offset(cX, h * 0.28f), strokeWidth = 1.8f)
                drawLine(iconColor, Offset(w * 0.20f, h * 0.26f), Offset(w * 0.30f, h * 0.36f), strokeWidth = 1.8f)
                drawLine(iconColor, Offset(w * 0.80f, h * 0.26f), Offset(w * 0.70f, h * 0.36f), strokeWidth = 1.8f)
            }
            PrayerType.DHUHR -> {
                // Bright Midday Sun
                drawCircle(iconColor, radius = w * 0.24f, center = Offset(cX, cY))
                // 8 Rays
                for (i in 0 until 8) {
                    val angle = (i * 45) * (Math.PI / 180.0)
                    val x1 = cX + (w * 0.32f * Math.cos(angle)).toFloat()
                    val y1 = cY + (h * 0.32f * Math.sin(angle)).toFloat()
                    val x2 = cX + (w * 0.46f * Math.cos(angle)).toFloat()
                    val y2 = cY + (h * 0.46f * Math.sin(angle)).toFloat()
                    drawLine(iconColor, Offset(x1, y1), Offset(x2, y2), strokeWidth = 1.8f)
                }
            }
            PrayerType.ASR -> {
                // Afternoon Sun descending
                drawCircle(iconColor, radius = w * 0.22f, center = Offset(cX, cY - 2f))
                drawLine(iconColor, Offset(cX, 2f), Offset(cX, 6f), strokeWidth = 1.8f)
                drawLine(iconColor, Offset(2f, cY), Offset(6f, cY), strokeWidth = 1.8f)
                drawLine(iconColor, Offset(w - 2f, cY), Offset(w - 6f, cY), strokeWidth = 1.8f)
                drawLine(iconColor, Offset(2f, h * 0.82f), Offset(w - 2f, h * 0.82f), strokeWidth = 1.8f)
            }
            PrayerType.MAGHRIB -> {
                // Sun setting behind horizon
                drawLine(iconColor, Offset(2f, h * 0.62f), Offset(w - 2f, h * 0.62f), strokeWidth = 2f)
                drawArc(
                    color = iconColor,
                    startAngle = 180f,
                    sweepAngle = 180f,
                    useCenter = true,
                    topLeft = Offset(w * 0.25f, h * 0.32f),
                    size = Size(w * 0.50f, h * 0.50f)
                )
                // Downward setting arrow / water ripples
                drawLine(iconColor, Offset(4f, h * 0.80f), Offset(w - 4f, h * 0.80f), strokeWidth = 1.5f)
            }
            PrayerType.ISHA -> {
                // Crescent Moon
                val moonPath = Path().apply {
                    moveTo(w * 0.65f, h * 0.15f)
                    cubicTo(w * 0.25f, h * 0.25f, w * 0.25f, h * 0.75f, w * 0.65f, h * 0.85f)
                    cubicTo(w * 0.42f, h * 0.72f, w * 0.42f, h * 0.28f, w * 0.65f, h * 0.15f)
                    close()
                }
                drawPath(moonPath, iconColor)
            }
            PrayerType.JUMAH -> {
                // Red Mosque Silhouette with Minarets
                val mosquePath = Path().apply {
                    // Left Minaret
                    moveTo(w * 0.10f, h * 0.90f)
                    lineTo(w * 0.10f, h * 0.32f)
                    lineTo(w * 0.18f, h * 0.20f)
                    lineTo(w * 0.26f, h * 0.32f)
                    lineTo(w * 0.26f, h * 0.90f)
                    // Center Dome
                    lineTo(w * 0.32f, h * 0.90f)
                    lineTo(w * 0.32f, h * 0.52f)
                    cubicTo(w * 0.32f, h * 0.28f, w * 0.42f, h * 0.22f, w * 0.50f, h * 0.15f)
                    cubicTo(w * 0.58f, h * 0.22f, w * 0.68f, h * 0.28f, w * 0.68f, h * 0.52f)
                    lineTo(w * 0.68f, h * 0.90f)
                    // Right Minaret
                    lineTo(w * 0.74f, h * 0.90f)
                    lineTo(w * 0.74f, h * 0.32f)
                    lineTo(w * 0.82f, h * 0.20f)
                    lineTo(w * 0.90f, h * 0.32f)
                    lineTo(w * 0.90f, h * 0.90f)
                    close()
                }
                drawPath(mosquePath, RedDigital)
            }
            PrayerType.SUNRISE_SEHRI -> {
                // Bowl / Morning meal dish with steam
                val bowlPath = Path().apply {
                    moveTo(w * 0.20f, h * 0.45f)
                    lineTo(w * 0.80f, h * 0.45f)
                    cubicTo(w * 0.78f, h * 0.82f, w * 0.22f, h * 0.82f, w * 0.20f, h * 0.45f)
                    close()
                }
                drawPath(bowlPath, iconColor)
                // Chopsticks / Spoon diagonal line
                drawLine(iconColor, Offset(w * 0.12f, h * 0.20f), Offset(w * 0.70f, h * 0.55f), strokeWidth = 1.8f)
            }
            PrayerType.SUNSET_IFTAR -> {
                // Evening Sun and dish / plate
                drawLine(GoldAccent, Offset(2f, h * 0.72f), Offset(w - 2f, h * 0.72f), strokeWidth = 2f)
                drawCircle(GoldAccent, radius = w * 0.22f, center = Offset(cX, h * 0.48f))
                // Top rays
                drawLine(GoldAccent, Offset(cX, h * 0.12f), Offset(cX, h * 0.22f), strokeWidth = 1.8f)
                drawLine(GoldAccent, Offset(w * 0.24f, h * 0.24f), Offset(w * 0.32f, h * 0.32f), strokeWidth = 1.8f)
                drawLine(GoldAccent, Offset(w * 0.76f, h * 0.24f), Offset(w * 0.68f, h * 0.32f), strokeWidth = 1.8f)
            }
        }
    }
}
