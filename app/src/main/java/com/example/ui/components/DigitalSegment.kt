package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GreenDigital
import com.example.ui.theme.GreenDigitalDim
import com.example.ui.theme.RedDigital
import com.example.ui.theme.RedDigitalDim

/**
 * 7-Segment LED Digit Display.
 * Segments:
 *    -- a --
 *   |       |
 *   f       b
 *   |       |
 *    -- g --
 *   |       |
 *   e       c
 *   |       |
 *    -- d --
 */
@Composable
fun SevenSegmentDigit(
    char: Char,
    modifier: Modifier = Modifier,
    activeColor: Color = RedDigital,
    inactiveColor: Color = RedDigitalDim,
    slantAngle: Float = 0.05f
) {
    val activeSegments = getActiveSegments(char)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val thickness = w * 0.18f
        val gap = thickness * 0.22f

        val halfH = h / 2f

        // Segment A (top horizontal)
        drawHorizontalSegment(
            x = thickness * 0.8f,
            y = 0f,
            length = w - (thickness * 1.6f),
            thickness = thickness,
            gap = gap,
            isActive = 'a' in activeSegments,
            activeColor = activeColor,
            inactiveColor = inactiveColor
        )

        // Segment B (top right vertical)
        drawVerticalSegment(
            x = w - thickness,
            y = thickness * 0.8f,
            length = halfH - (thickness * 0.9f),
            thickness = thickness,
            gap = gap,
            isActive = 'b' in activeSegments,
            activeColor = activeColor,
            inactiveColor = inactiveColor
        )

        // Segment C (bottom right vertical)
        drawVerticalSegment(
            x = w - thickness,
            y = halfH + (thickness * 0.1f),
            length = halfH - (thickness * 0.9f),
            thickness = thickness,
            gap = gap,
            isActive = 'c' in activeSegments,
            activeColor = activeColor,
            inactiveColor = inactiveColor
        )

        // Segment D (bottom horizontal)
        drawHorizontalSegment(
            x = thickness * 0.8f,
            y = h - thickness,
            length = w - (thickness * 1.6f),
            thickness = thickness,
            gap = gap,
            isActive = 'd' in activeSegments,
            activeColor = activeColor,
            inactiveColor = inactiveColor
        )

        // Segment E (bottom left vertical)
        drawVerticalSegment(
            x = 0f,
            y = halfH + (thickness * 0.1f),
            length = halfH - (thickness * 0.9f),
            thickness = thickness,
            gap = gap,
            isActive = 'e' in activeSegments,
            activeColor = activeColor,
            inactiveColor = inactiveColor
        )

        // Segment F (top left vertical)
        drawVerticalSegment(
            x = 0f,
            y = thickness * 0.8f,
            length = halfH - (thickness * 0.9f),
            thickness = thickness,
            gap = gap,
            isActive = 'f' in activeSegments,
            activeColor = activeColor,
            inactiveColor = inactiveColor
        )

        // Segment G (middle horizontal)
        drawHorizontalSegment(
            x = thickness * 0.8f,
            y = halfH - (thickness / 2f),
            length = w - (thickness * 1.6f),
            thickness = thickness,
            gap = gap,
            isActive = 'g' in activeSegments,
            activeColor = activeColor,
            inactiveColor = inactiveColor
        )
    }
}

private fun DrawScope.drawHorizontalSegment(
    x: Float,
    y: Float,
    length: Float,
    thickness: Float,
    gap: Float,
    isActive: Boolean,
    activeColor: Color,
    inactiveColor: Color
) {
    val color = if (isActive) activeColor else inactiveColor
    val halfT = thickness / 2f

    val path = Path().apply {
        moveTo(x + halfT + gap, y)
        lineTo(x + length - halfT - gap, y)
        lineTo(x + length - gap, y + halfT)
        lineTo(x + length - halfT - gap, y + thickness)
        lineTo(x + halfT + gap, y + thickness)
        lineTo(x + gap, y + halfT)
        close()
    }
    drawPath(path, color)
}

private fun DrawScope.drawVerticalSegment(
    x: Float,
    y: Float,
    length: Float,
    thickness: Float,
    gap: Float,
    isActive: Boolean,
    activeColor: Color,
    inactiveColor: Color
) {
    val color = if (isActive) activeColor else inactiveColor
    val halfT = thickness / 2f

    val path = Path().apply {
        moveTo(x, y + halfT + gap)
        lineTo(x + halfT, y + gap)
        lineTo(x + thickness, y + halfT + gap)
        lineTo(x + thickness, y + length - halfT - gap)
        lineTo(x + halfT, y + length - gap)
        lineTo(x, y + length - halfT - gap)
        close()
    }
    drawPath(path, color)
}

@Composable
fun SevenSegmentColon(
    modifier: Modifier = Modifier,
    activeColor: Color = RedDigital,
    inactiveColor: Color = RedDigitalDim
) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val dotRadius = w * 0.35f

        drawCircle(
            color = activeColor,
            radius = dotRadius,
            center = Offset(w / 2f, h * 0.32f)
        )
        drawCircle(
            color = activeColor,
            radius = dotRadius,
            center = Offset(w / 2f, h * 0.68f)
        )
    }
}

private fun getActiveSegments(char: Char): Set<Char> {
    return when (char) {
        '0' -> setOf('a', 'b', 'c', 'd', 'e', 'f')
        '1' -> setOf('b', 'c')
        '2' -> setOf('a', 'b', 'd', 'e', 'g')
        '3' -> setOf('a', 'b', 'c', 'd', 'g')
        '4' -> setOf('b', 'c', 'f', 'g')
        '5' -> setOf('a', 'c', 'd', 'f', 'g')
        '6' -> setOf('a', 'c', 'd', 'e', 'f', 'g')
        '7' -> setOf('a', 'b', 'c')
        '8' -> setOf('a', 'b', 'c', 'd', 'e', 'f', 'g')
        '9' -> setOf('a', 'b', 'c', 'd', 'f', 'g')
        '-' -> setOf('g')
        else -> emptySet()
    }
}

/**
 * Large Hero LED Clock: "18:88:88" (or live HH:MM:SS)
 */
@Composable
fun HeroDigitalLedClock(
    timeString: String,
    modifier: Modifier = Modifier,
    digitWidth: Dp = 34.dp,
    digitHeight: Dp = 62.dp,
    secDigitWidth: Dp = 22.dp,
    secDigitHeight: Dp = 40.dp,
    activeColor: Color = RedDigital,
    inactiveColor: Color = RedDigitalDim
) {
    // Expected format: "HH:MM:SS" or "HH:MM"
    val parts = timeString.split(":")
    val hh = parts.getOrNull(0) ?: "18"
    val mm = parts.getOrNull(1) ?: "88"
    val ss = parts.getOrNull(2) ?: "88"

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Hours
        hh.forEach { char ->
            SevenSegmentDigit(
                char = char,
                modifier = Modifier
                    .width(digitWidth)
                    .height(digitHeight)
                    .padding(horizontal = 2.dp),
                activeColor = activeColor,
                inactiveColor = inactiveColor
            )
        }

        // Colon
        SevenSegmentColon(
            modifier = Modifier
                .width(10.dp)
                .height(digitHeight)
                .padding(horizontal = 1.dp),
            activeColor = activeColor,
            inactiveColor = inactiveColor
        )

        // Minutes
        mm.forEach { char ->
            SevenSegmentDigit(
                char = char,
                modifier = Modifier
                    .width(digitWidth)
                    .height(digitHeight)
                    .padding(horizontal = 2.dp),
                activeColor = activeColor,
                inactiveColor = inactiveColor
            )
        }

        Spacer(modifier = Modifier.width(4.dp))

        // Seconds (smaller size aligned to bottom/center)
        Row(
            verticalAlignment = Alignment.Bottom,
            modifier = Modifier.height(digitHeight).padding(bottom = 6.dp)
        ) {
            ss.forEach { char ->
                SevenSegmentDigit(
                    char = char,
                    modifier = Modifier
                        .width(secDigitWidth)
                        .height(secDigitHeight)
                        .padding(horizontal = 1.5.dp),
                    activeColor = activeColor,
                    inactiveColor = inactiveColor
                )
            }
        }
    }
}

/**
 * Small 7-Segment display for Prayer Table (e.g., "8:88" or "04:45")
 */
@Composable
fun TableDigitalTime(
    timeString: String,
    modifier: Modifier = Modifier,
    activeColor: Color = RedDigital,
    inactiveColor: Color = RedDigitalDim,
    digitWidth: Dp = 13.dp,
    digitHeight: Dp = 24.dp
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        timeString.forEach { char ->
            if (char == ':') {
                SevenSegmentColon(
                    modifier = Modifier
                        .width(6.dp)
                        .height(digitHeight)
                        .padding(horizontal = 1.dp),
                    activeColor = activeColor,
                    inactiveColor = inactiveColor
                )
            } else {
                SevenSegmentDigit(
                    char = char,
                    modifier = Modifier
                        .width(digitWidth)
                        .height(digitHeight)
                        .padding(horizontal = 1.dp),
                    activeColor = activeColor,
                    inactiveColor = inactiveColor
                )
            }
        }
    }
}
