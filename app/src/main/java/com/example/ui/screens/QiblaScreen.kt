package com.example.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sensor.QiblaCompassManager
import com.example.sensor.QiblaState
import com.example.ui.theme.*
import com.example.util.HapticUtils
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

@Composable
fun QiblaScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current
    val compassManager = remember { QiblaCompassManager(context) }
    val qiblaState by compassManager.qiblaState.collectAsState()

    DisposableEffect(Unit) {
        compassManager.startListening()
        onDispose {
            compassManager.stopListening()
        }
    }

    val animatedNeedleRotation by animateFloatAsState(
        targetValue = qiblaState.relativeAngle,
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
        label = "QiblaNeedleRotation"
    )

    val animatedDialRotation by animateFloatAsState(
        targetValue = -qiblaState.currentAzimuth,
        animationSpec = tween(durationMillis = 150, easing = FastOutSlowInEasing),
        label = "CompassDialRotation"
    )

    val dialBorderColor by animateColorAsState(
        targetValue = if (qiblaState.isAligned) PrimaryGreen else DarkSurfaceBorder,
        animationSpec = tween(durationMillis = 250),
        label = "DialBorderColor"
    )

    val glowColor by animateColorAsState(
        targetValue = if (qiblaState.isAligned) PrimaryGreen.copy(alpha = 0.4f) else Color.Transparent,
        animationSpec = tween(durationMillis = 250),
        label = "GlowColor"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Status & Instruction Header
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, if (qiblaState.isAligned) PrimaryGreen.copy(alpha = 0.5f) else DarkSurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (qiblaState.isAligned) PrimaryGreen.copy(alpha = 0.2f) else EmeraldDeep)
                        .border(1.5.dp, if (qiblaState.isAligned) PrimaryGreen else DarkGreenBorder, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (qiblaState.isAligned) Icons.Outlined.CheckCircle else Icons.Outlined.Explore,
                        contentDescription = null,
                        tint = if (qiblaState.isAligned) PrimaryGreen else GoldAccent,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (qiblaState.isAligned) "কিবলা লক হয়েছে (সঠিক দিক)" else "কিবলার দিক নির্ধারণ করুন",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (qiblaState.isAligned) PrimaryGreen else TextWhite
                    )
                    Text(
                        text = if (qiblaState.isAligned)
                            "আপনি পবিত্র কাবার মুখোমুখি আছেন"
                        else
                            getTurnInstruction(qiblaState.relativeAngle),
                        fontSize = 12.sp,
                        color = if (qiblaState.isAligned) GoldAccent else TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Main Circular Compass Dial with Magnetometer & Accelerometer Data
        Box(
            modifier = Modifier
                .size(280.dp)
                .drawBehind {
                    if (qiblaState.isAligned) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(PrimaryGreen.copy(alpha = 0.35f), Color.Transparent),
                                radius = size.width * 0.7f
                            )
                        )
                    }
                }
                .clip(CircleShape)
                .background(DarkSurface)
                .border(3.dp, dialBorderColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            // Rotating Compass Outer Dial
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(animatedDialRotation)
            ) {
                val center = Offset(size.width / 2f, size.height / 2f)
                val radius = size.width / 2f

                // Draw 72 degree tick marks (every 5 degrees)
                for (i in 0 until 72) {
                    val angleDeg = i * 5.0
                    val angleRad = Math.toRadians(angleDeg)
                    val isMajor = i % 6 == 0 // Every 30 deg
                    val isCardinal = i % 18 == 0 // N, E, S, W

                    val tickLength = if (isCardinal) 16.dp.toPx() else if (isMajor) 10.dp.toPx() else 5.dp.toPx()
                    val tickColor = if (isCardinal) GoldAccent else if (isMajor) PrimaryGreen.copy(alpha = 0.6f) else TextMuted.copy(alpha = 0.3f)
                    val strokeWidth = if (isCardinal) 2.5f else if (isMajor) 1.8f else 1.0f

                    val startX = center.x + ((radius - 12.dp.toPx()) * sin(angleRad)).toFloat()
                    val startY = center.y - ((radius - 12.dp.toPx()) * cos(angleRad)).toFloat()

                    val endX = center.x + ((radius - 12.dp.toPx() - tickLength) * sin(angleRad)).toFloat()
                    val endY = center.y - ((radius - 12.dp.toPx() - tickLength) * cos(angleRad)).toFloat()

                    drawLine(
                        color = tickColor,
                        start = Offset(startX, startY),
                        end = Offset(endX, endY),
                        strokeWidth = strokeWidth
                    )
                }

                // Inner circle
                drawCircle(
                    color = DarkSurfaceBorder,
                    radius = radius * 0.62f,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Cardinal Indicators
            Box(modifier = Modifier.fillMaxSize().rotate(animatedDialRotation)) {
                Text(
                    text = "N",
                    color = RedDigital,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.align(Alignment.TopCenter).padding(top = 8.dp)
                )
                Text(
                    text = "E",
                    color = TextWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterEnd).padding(end = 10.dp)
                )
                Text(
                    text = "S",
                    color = TextWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 8.dp)
                )
                Text(
                    text = "W",
                    color = TextWhite,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterStart).padding(start = 10.dp)
                )
            }

            // Rotating Qibla Kaaba Pointer Needle
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .rotate(animatedNeedleRotation),
                contentAlignment = Alignment.Center
            ) {
                // Directional Needle pointing towards Kaaba
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val cX = size.width / 2f
                    val cY = size.height / 2f

                    val arrowPath = Path().apply {
                        moveTo(cX, cY - (size.height * 0.38f))
                        lineTo(cX - 10.dp.toPx(), cY - (size.height * 0.15f))
                        lineTo(cX + 10.dp.toPx(), cY - (size.height * 0.15f))
                        close()
                    }

                    drawPath(
                        path = arrowPath,
                        brush = Brush.verticalGradient(
                            listOf(if (qiblaState.isAligned) PrimaryGreen else GoldAccent, GoldWarm)
                        )
                    )

                    // Connecting pointer beam
                    drawLine(
                        color = if (qiblaState.isAligned) PrimaryGreen else GoldAccent,
                        start = Offset(cX, cY - (size.height * 0.15f)),
                        end = Offset(cX, cY),
                        strokeWidth = 3.dp.toPx()
                    )
                }

                // Kaaba Icon at the tip of the needle
                Box(
                    modifier = Modifier
                        .offset(y = (-90).dp)
                        .size(34.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (qiblaState.isAligned) PrimaryGreen else Color.Black)
                        .border(1.5.dp, GoldAccent, RoundedCornerShape(6.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .background(GoldAccent)
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "🕋",
                            fontSize = 14.sp
                        )
                    }
                }

                // Center Pivot Hub
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(GoldAccent)
                        .border(2.dp, DarkBackground, CircleShape)
                )
            }

            // Fixed Top Direction Indicator
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-6).dp)
                    .size(12.dp)
                    .clip(CircleShape)
                    .background(PrimaryGreen)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Degrees & Azimuth Real-time Metrics Card
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            MetricCard(
                title = "কিবলার কোণ",
                value = "${qiblaState.qiblaBearing.roundToInt()}°",
                subtitle = "মক্কা মুকাররমা",
                icon = Icons.Outlined.NearMe,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "ডিভাইস দিক",
                value = "${qiblaState.currentAzimuth.roundToInt()}°",
                subtitle = getCompassDirectionName(qiblaState.currentAzimuth),
                icon = Icons.Outlined.CompassCalibration,
                modifier = Modifier.weight(1f)
            )

            MetricCard(
                title = "কাবার দূরত্ব",
                value = "${qiblaState.distanceKm.roundToInt()} কিমি",
                subtitle = "বাংলাদেশ থেকে",
                icon = Icons.Outlined.Straighten,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Sensor & Calibration Information
        Surface(
            shape = RoundedCornerShape(14.dp),
            color = DarkSurface,
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.Sensors,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "হার্ডওয়্যার সেন্সর স্ট্যাটাস",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (qiblaState.isSensorAvailable) PrimaryGreen.copy(alpha = 0.15f) else RedDigital.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (qiblaState.isSensorAvailable) "সক্রিয় (Active)" else "অনুপস্থিত",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (qiblaState.isSensorAvailable) PrimaryGreen else RedDigital
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "ম্যাগনেটোমিটার এবং অ্যাক্সিলোমিটার সেন্সর ব্যবহার করে রিয়েলটাইম কিবলার দিক গণনা করা হচ্ছে। সঠিক ফলাফলের জন্য মোবাইলটিকে সমতল স্থানে রাখুন এবং ইংরেজী '8' এর মত ঘুরিয়ে ক্যালিব্রেট করুন।",
                    fontSize = 11.sp,
                    color = TextMuted,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Test Haptic Button
                Button(
                    onClick = {
                        HapticUtils.performLongPressHaptic(view)
                        HapticUtils.performQiblaLockPulse(context)
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DarkSurfaceElevated,
                        contentColor = PrimaryGreen
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, DarkGreenBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Vibration,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "টেকটাইল ভাইব্রেশন টেস্ট করুন (Long Press Haptic)",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldAccent,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = PrimaryGreen
            )
            Text(
                text = title,
                fontSize = 10.sp,
                color = TextWhite,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = subtitle,
                fontSize = 9.sp,
                color = TextMuted
            )
        }
    }
}

private fun getTurnInstruction(relativeAngle: Float): String {
    val diff = (relativeAngle + 180) % 360 - 180
    return when {
        abs(diff) <= 3.5 -> "সঠিক দিকে আছেন"
        diff > 0 -> "ডান দিকে ${diff.roundToInt()}° ঘোরান"
        else -> "বাম দিকে ${abs(diff).roundToInt()}° ঘোরান"
    }
}

private fun getCompassDirectionName(azimuth: Float): String {
    val deg = (azimuth + 360) % 360
    return when {
        deg >= 337.5 || deg < 22.5 -> "উত্তর (N)"
        deg >= 22.5 && deg < 67.5 -> "উত্তর-পূর্ব (NE)"
        deg >= 67.5 && deg < 112.5 -> "পূর্ব (E)"
        deg >= 112.5 && deg < 157.5 -> "দক্ষিণ-পূর্ব (SE)"
        deg >= 157.5 && deg < 202.5 -> "দক্ষিণ (S)"
        deg >= 202.5 && deg < 247.5 -> "দক্ষিণ-পশ্চিম (SW)"
        deg >= 247.5 && deg < 292.5 -> "পশ্চিম (W)"
        else -> "উত্তর-পশ্চিম (NW)"
    }
}
