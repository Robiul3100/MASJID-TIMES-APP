package com.robiul.mosquetime.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.ui.theme.CyanBlue
import com.robiul.mosquetime.ui.theme.CyanBlueDim
import com.robiul.mosquetime.ui.theme.GoldAccent
import com.robiul.mosquetime.ui.theme.NeonGreenGlow
import com.robiul.mosquetime.ui.theme.PrimaryGreen
import com.robiul.mosquetime.ui.theme.RedDigital

/**
 * Custom Canvas drawing the majestic Mosque Arch Dome outline with neon glowing effect,
 * decorative minarets on left & right, and arabesque floral flourishes.
 * Also exposed as MosqueHeroShape for modular architecture.
 */
@Composable
fun MosqueHeroShape(
    modifier: Modifier = Modifier
) {
    MosqueArchCanvas(modifier = modifier)
}

@Composable
fun MosqueCrestIcon(
    modifier: Modifier = Modifier
) {
    MosqueArchCanvas(modifier = modifier)
}

@Composable
fun MosqueArchCanvas(
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Outer Arch Path (Multi-lobed Moorish / Ogee pointed dome)
        val outerArchPath = Path().apply {
            // Left base start
            moveTo(14f, h)
            lineTo(14f, h * 0.46f)

            // Left shoulder curve
            cubicTo(
                14f, h * 0.28f,
                w * 0.18f, h * 0.16f,
                w * 0.32f, h * 0.16f
            )

            // Left arch upward swell
            cubicTo(
                w * 0.40f, h * 0.16f,
                w * 0.42f, h * 0.05f,
                w * 0.50f, 10f
            )

            // Right arch downward swell
            cubicTo(
                w * 0.58f, h * 0.05f,
                w * 0.60f, h * 0.16f,
                w * 0.68f, h * 0.16f
            )

            // Right shoulder curve
            cubicTo(
                w * 0.82f, h * 0.16f,
                w - 14f, h * 0.28f,
                w - 14f, h * 0.46f
            )

            // Right base line
            lineTo(w - 14f, h)
        }

        // 1. Soft Outer Glow (Layer 1)
        drawPath(
            path = outerArchPath,
            color = NeonGreenGlow.copy(alpha = 0.20f),
            style = Stroke(
                width = 12f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 2. Medium Glow (Layer 2)
        drawPath(
            path = outerArchPath,
            color = PrimaryGreen.copy(alpha = 0.45f),
            style = Stroke(
                width = 6f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // 3. Crisp Core Neon Green Line
        drawPath(
            path = outerArchPath,
            color = NeonGreenGlow,
            style = Stroke(
                width = 2.8f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Inner Sub-arch Green Contour
        val innerArchPath = Path().apply {
            moveTo(24f, h * 0.45f)
            cubicTo(
                24f, h * 0.32f,
                w * 0.22f, h * 0.24f,
                w * 0.34f, h * 0.24f
            )
            cubicTo(
                w * 0.42f, h * 0.24f,
                w * 0.44f, h * 0.14f,
                w * 0.50f, h * 0.10f
            )
            cubicTo(
                w * 0.56f, h * 0.14f,
                w * 0.58f, h * 0.24f,
                w * 0.66f, h * 0.24f
            )
            cubicTo(
                w * 0.78f, h * 0.24f,
                w - 24f, h * 0.32f,
                w - 24f, h * 0.45f
            )
        }

        drawPath(
            path = innerArchPath,
            color = PrimaryGreen.copy(alpha = 0.65f),
            style = Stroke(
                width = 1.8f,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Draw Left and Right Side Floral Ornaments & Minarets
        drawSideOrnament(isLeft = true, w = w, h = h)
        drawSideOrnament(isLeft = false, w = w, h = h)

        drawMinaretPillar(isLeft = true, w = w, h = h)
        drawMinaretPillar(isLeft = false, w = w, h = h)
    }
}

/**
 * Draws the ornamental flourish on the shoulder of the arch:
 * Cyan petals + white vines + gold accent dot
 */
private fun DrawScope.drawSideOrnament(isLeft: Boolean, w: Float, h: Float) {
    val centerX = if (isLeft) w * 0.14f else w * 0.86f
    val centerY = h * 0.30f

    // 4 Petal Cyan Flower
    val petalRadius = 6.5f
    val flowerColor = CyanBlue

    drawCircle(
        color = flowerColor,
        radius = petalRadius,
        center = Offset(centerX - 6f, centerY - 6f)
    )
    drawCircle(
        color = flowerColor,
        radius = petalRadius,
        center = Offset(centerX + 6f, centerY - 6f)
    )
    drawCircle(
        color = flowerColor,
        radius = petalRadius,
        center = Offset(centerX - 6f, centerY + 6f)
    )
    drawCircle(
        color = flowerColor,
        radius = petalRadius,
        center = Offset(centerX + 6f, centerY + 6f)
    )

    // Center Gold Core
    drawCircle(
        color = GoldAccent,
        radius = 3.5f,
        center = Offset(centerX, centerY)
    )

    // White / Silver flourish scrolls
    val flourishPath = Path().apply {
        if (isLeft) {
            moveTo(centerX - 2f, centerY + 14f)
            cubicTo(
                centerX - 10f, centerY + 24f,
                centerX - 14f, centerY + 38f,
                centerX - 6f, centerY + 50f
            )
            moveTo(centerX + 2f, centerY + 16f)
            cubicTo(
                centerX + 6f, centerY + 28f,
                centerX, centerY + 42f,
                centerX - 2f, centerY + 48f
            )
        } else {
            moveTo(centerX + 2f, centerY + 14f)
            cubicTo(
                centerX + 10f, centerY + 24f,
                centerX + 14f, centerY + 38f,
                centerX + 6f, centerY + 50f
            )
            moveTo(centerX - 2f, centerY + 16f)
            cubicTo(
                centerX - 6f, centerY + 28f,
                centerX, centerY + 42f,
                centerX + 2f, centerY + 48f
            )
        }
    }

    drawPath(
        path = flourishPath,
        color = Color.White.copy(alpha = 0.85f),
        style = Stroke(width = 2.2f, cap = StrokeCap.Round)
    )

    // Yellow accent bud at bottom of flourish
    val budX = if (isLeft) centerX - 6f else centerX + 6f
    drawCircle(
        color = GoldAccent,
        radius = 4f,
        center = Offset(budX, centerY + 52f)
    )
}

/**
 * Draws vertical minaret columns on left and right sides of clock
 */
private fun DrawScope.drawMinaretPillar(isLeft: Boolean, w: Float, h: Float) {
    val x = if (isLeft) 20f else w - 34f
    val topY = h * 0.40f
    val pillarW = 14f
    val pillarH = h * 0.48f

    // Minaret Spire Point
    val spirePath = Path().apply {
        moveTo(x + (pillarW / 2f), topY - 14f)
        lineTo(x + pillarW, topY)
        lineTo(x, topY)
        close()
    }
    drawPath(spirePath, GoldAccent)

    // Minaret Balcony Dome / Ring
    drawRect(
        color = GoldAccent,
        topLeft = Offset(x - 2f, topY),
        size = Size(pillarW + 4f, 6f)
    )

    // Upper green section
    drawRect(
        color = PrimaryGreen,
        topLeft = Offset(x, topY + 6f),
        size = Size(pillarW, pillarH * 0.20f)
    )

    // Middle Gold Ring
    drawRect(
        color = GoldAccent,
        topLeft = Offset(x - 1f, topY + 6f + (pillarH * 0.20f)),
        size = Size(pillarW + 2f, 5f)
    )

    // Red Column Shaft
    drawRect(
        color = RedDigital,
        topLeft = Offset(x, topY + 11f + (pillarH * 0.20f)),
        size = Size(pillarW, pillarH * 0.55f)
    )

    // Lower Gold Ring & Base
    drawRect(
        color = GoldAccent,
        topLeft = Offset(x - 2f, topY + 11f + (pillarH * 0.75f)),
        size = Size(pillarW + 4f, 8f)
    )
}

/**
 * Cyan Arrow Badge with Arabic Prayer Label (e.g., [ ▶ الفجر ]) styled with Amiri Arabic Font
 */
@Composable
fun ArabicPrayerBadge(
    arabicName: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = CyanBlueDim.copy(alpha = 0.35f),
                shape = RoundedCornerShape(6.dp)
            )
            .border(
                width = 0.8.dp,
                color = CyanBlue.copy(alpha = 0.75f),
                shape = RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "▶ ",
                color = CyanBlue,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = arabicName,
                color = CyanBlue,
                fontFamily = com.robiul.mosquetime.ui.theme.AmiriFontFamily,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
