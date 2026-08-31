package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextWhite

enum class ShortcutType {
    TODAY_SCHEDULE,
    MONTHLY_SCHEDULE,
    ARABIC_CALENDAR,
    QIBLA_DIRECTION
}

/**
 * 4 Feature Shortcut Cards in a single horizontal row matching reference image
 */
@Composable
fun ShortcutCardsRow(
    onShortcutClick: (ShortcutType) -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // 1. আজকের সময়সূচি (Green)
        ShortcutCard(
            type = ShortcutType.TODAY_SCHEDULE,
            titleLine1 = "আজকের",
            titleLine2 = "সময়সূচি",
            accentColor = PrimaryGreen,
            onClick = { onShortcutClick(ShortcutType.TODAY_SCHEDULE) },
            modifier = Modifier.weight(1f)
        )

        // 2. মাসিক সময়সূচি (Purple)
        ShortcutCard(
            type = ShortcutType.MONTHLY_SCHEDULE,
            titleLine1 = "মাসিক",
            titleLine2 = "সময়সূচি",
            accentColor = PurpleAccent,
            onClick = { onShortcutClick(ShortcutType.MONTHLY_SCHEDULE) },
            modifier = Modifier.weight(1f)
        )

        // 3. আরবী ক্যালেন্ডার (Gold)
        ShortcutCard(
            type = ShortcutType.ARABIC_CALENDAR,
            titleLine1 = "আরবী",
            titleLine2 = "ক্যালেন্ডার",
            accentColor = GoldAccent,
            onClick = { onShortcutClick(ShortcutType.ARABIC_CALENDAR) },
            modifier = Modifier.weight(1f)
        )

        // 4. কিবলার দিক (Cyan)
        ShortcutCard(
            type = ShortcutType.QIBLA_DIRECTION,
            titleLine1 = "কিবলার",
            titleLine2 = "দিক",
            accentColor = CyanBlue,
            onClick = { onShortcutClick(ShortcutType.QIBLA_DIRECTION) },
            modifier = Modifier.weight(1f)
        )
    }
}

/**
 * Individual Custom Glowing Feature Card
 */
@Composable
fun ShortcutCard(
    type: ShortcutType,
    titleLine1: String,
    titleLine2: String,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(108.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.75f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 8.dp, horizontal = 3.dp)
            .testTag("shortcut_${type.name.lowercase()}"),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Icon
            ShortcutIcon(type = type, color = accentColor, modifier = Modifier.size(34.dp))

            Spacer(modifier = Modifier.height(6.dp))

            // Two-line Bengali Label
            Text(
                text = titleLine1,
                color = TextWhite,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = titleLine2,
                color = TextWhite,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Custom vector graphics for each shortcut card icon
 */
@Composable
private fun ShortcutIcon(
    type: ShortcutType,
    color: Color,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val cX = w / 2f
        val cY = h / 2f

        when (type) {
            ShortcutType.TODAY_SCHEDULE -> {
                // Circular Clock
                drawCircle(
                    color = color,
                    radius = w * 0.40f,
                    center = Offset(cX, cY),
                    style = Stroke(width = 2.4f)
                )
                // Hour hand
                drawLine(
                    color = color,
                    start = Offset(cX, cY),
                    end = Offset(cX, cY - (h * 0.22f)),
                    strokeWidth = 2.2f
                )
                // Minute hand
                drawLine(
                    color = color,
                    start = Offset(cX, cY),
                    end = Offset(cX + (w * 0.18f), cY),
                    strokeWidth = 2.2f
                )
                // Center point
                drawCircle(color, radius = 2.5f, center = Offset(cX, cY))
            }
            ShortcutType.MONTHLY_SCHEDULE -> {
                // Grid Calendar
                drawRoundRect(
                    color = color,
                    topLeft = Offset(w * 0.15f, h * 0.20f),
                    size = Size(w * 0.70f, h * 0.65f),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f),
                    style = Stroke(width = 2.2f)
                )
                // Top header rings
                drawLine(color, Offset(w * 0.30f, h * 0.10f), Offset(w * 0.30f, h * 0.20f), strokeWidth = 2.5f)
                drawLine(color, Offset(w * 0.70f, h * 0.10f), Offset(w * 0.70f, h * 0.20f), strokeWidth = 2.5f)
                // Top inner line
                drawLine(color, Offset(w * 0.15f, h * 0.40f), Offset(w * 0.85f, h * 0.40f), strokeWidth = 1.6f)
                // Dots grid (3x3)
                for (r in 0..1) {
                    for (c in 0..3) {
                        drawCircle(
                            color = color,
                            radius = 1.8f,
                            center = Offset(w * 0.28f + (c * w * 0.15f), h * 0.54f + (r * h * 0.16f))
                        )
                    }
                }
            }
            ShortcutType.ARABIC_CALENDAR -> {
                // Quran Rehal / Open Book on Stand
                val bookLeft = Path().apply {
                    moveTo(cX, h * 0.50f)
                    cubicTo(w * 0.35f, h * 0.35f, w * 0.20f, h * 0.42f, w * 0.14f, h * 0.38f)
                    lineTo(w * 0.14f, h * 0.62f)
                    cubicTo(w * 0.20f, h * 0.66f, w * 0.35f, h * 0.58f, cX, h * 0.74f)
                    close()
                }
                drawPath(bookLeft, color, style = Stroke(width = 1.8f))

                val bookRight = Path().apply {
                    moveTo(cX, h * 0.50f)
                    cubicTo(w * 0.65f, h * 0.35f, w * 0.80f, h * 0.42f, w * 0.86f, h * 0.38f)
                    lineTo(w * 0.86f, h * 0.62f)
                    cubicTo(w * 0.80f, h * 0.66f, w * 0.65f, h * 0.58f, cX, h * 0.74f)
                    close()
                }
                drawPath(bookRight, color, style = Stroke(width = 1.8f))

                // X-Stand / Rehal base
                drawLine(color, Offset(w * 0.22f, h * 0.88f), Offset(w * 0.78f, h * 0.66f), strokeWidth = 2f)
                drawLine(color, Offset(w * 0.78f, h * 0.88f), Offset(w * 0.22f, h * 0.66f), strokeWidth = 2f)
            }
            ShortcutType.QIBLA_DIRECTION -> {
                // Compass
                drawCircle(
                    color = color,
                    radius = w * 0.40f,
                    center = Offset(cX, cY),
                    style = Stroke(width = 2.2f)
                )
                // Compass Needle (Diamond)
                val needleNorth = Path().apply {
                    moveTo(cX, cY)
                    lineTo(cX - (w * 0.10f), cY - (h * 0.10f))
                    lineTo(cX + (w * 0.18f), cY - (h * 0.28f))
                    close()
                }
                drawPath(needleNorth, color)

                val needleSouth = Path().apply {
                    moveTo(cX, cY)
                    lineTo(cX + (w * 0.10f), cY + (h * 0.10f))
                    lineTo(cX - (w * 0.18f), cY + (h * 0.28f))
                    close()
                }
                drawPath(needleSouth, color, style = Stroke(width = 1.5f))
            }
        }
    }
}
