package com.example.ui.screens

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CompassCalibration
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.CommonHeader
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.DarkGreenBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.RedDigital
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun QiblaCompassScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Bangladesh Makkah Kaaba bearing angle is approx 277°
    val qiblaAngle = 277f
    var azimuth by remember { mutableFloatStateOf(0f) }

    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService(android.content.Context.SENSOR_SERVICE) as? SensorManager
        val rotationSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val accelSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        var lastAccelerometer = FloatArray(3)
        var lastMagnetometer = FloatArray(3)
        var isAccelSet = false
        var isMagSet = false

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                if (event == null) return
                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    val rotationMatrix = FloatArray(9)
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    val orientation = FloatArray(3)
                    SensorManager.getOrientation(rotationMatrix, orientation)
                    val deg = (Math.toDegrees(orientation[0].toDouble()).toFloat() + 360) % 360
                    azimuth = deg
                } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                    System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)
                    isAccelSet = true
                } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                    System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.size)
                    isMagSet = true
                }

                if (rotationSensor == null && isAccelSet && isMagSet) {
                    val r = FloatArray(9)
                    val i = FloatArray(9)
                    if (SensorManager.getRotationMatrix(r, i, lastAccelerometer, lastMagnetometer)) {
                        val orientation = FloatArray(3)
                        SensorManager.getOrientation(r, orientation)
                        val deg = (Math.toDegrees(orientation[0].toDouble()).toFloat() + 360) % 360
                        azimuth = deg
                    }
                }
            }
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        if (rotationSensor != null) {
            sensorManager.registerListener(listener, rotationSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            accelSensor?.let { sensorManager?.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
            magSensor?.let { sensorManager?.registerListener(listener, it, SensorManager.SENSOR_DELAY_UI) }
        }

        onDispose {
            sensorManager?.unregisterListener(listener)
        }
    }

    val animatedAzimuth by animateFloatAsState(
        targetValue = azimuth,
        animationSpec = tween(durationMillis = 200),
        label = "azimuth"
    )

    val relativeQiblaAngle = (qiblaAngle - animatedAzimuth + 360) % 360
    val isFacingQibla = relativeQiblaAngle in 355.0..360.0 || relativeQiblaAngle in 0.0..5.0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonHeader(
            title = "কিবলা কম্পাস",
            subtitle = "পবিত্র কাবা শরীফের সঠিক দিকনির্ণয়",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Qibla Status Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isFacingQibla) DarkGreen.copy(alpha = 0.7f)
                        else DarkSurface
                    )
                    .border(
                        1.dp,
                        if (isFacingQibla) NeonGreenGlow else DarkGreenBorder,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = if (isFacingQibla) "✓ সঠিক কিবলার অভিমুখে আছেন" else "কিবলা কোন: ২৭৭° (পশ্চিম-উত্তর)",
                            color = if (isFacingQibla) NeonGreenGlow else GoldAccent,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "ঢাকা, বাংলাদেশ থেকে দূরত্ব: প্রায় ৫,১৩০ কি.মি.",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }

                    Text(
                        text = "${qiblaAngle.toInt()}°",
                        color = NeonGreenGlow,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Visual Compass Dial
            Box(
                modifier = Modifier
                    .size(280.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(DarkSurfaceElevated, DarkSurface, DarkBackground)
                        )
                    )
                    .border(
                        3.dp,
                        if (isFacingQibla) NeonGreenGlow else PrimaryGreen.copy(alpha = 0.7f),
                        CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                // Rotating dial canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(-animatedAzimuth)
                ) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2 - 20

                    // Draw outer ticks
                    for (i in 0 until 360 step 15) {
                        val rad = Math.toRadians(i.toDouble())
                        val tickLen = if (i % 90 == 0) 14f else if (i % 45 == 0) 10f else 6f
                        val startX = (center.x + (radius - tickLen) * sin(rad)).toFloat()
                        val startY = (center.y - (radius - tickLen) * cos(rad)).toFloat()
                        val endX = (center.x + radius * sin(rad)).toFloat()
                        val endY = (center.y - radius * cos(rad)).toFloat()

                        drawLine(
                            color = if (i == 0) RedDigital else if (i % 90 == 0) PrimaryGreen else DarkSurfaceBorder,
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = if (i % 90 == 0) 3f else 1.5f
                        )
                    }

                    // Draw Qibla pointer line (277 degrees)
                    val qiblaRad = Math.toRadians(277.0)
                    val qiblaX = (center.x + (radius - 24) * sin(qiblaRad)).toFloat()
                    val qiblaY = (center.y - (radius - 24) * cos(qiblaRad)).toFloat()

                    drawLine(
                        color = GoldAccent,
                        start = center,
                        end = Offset(qiblaX, qiblaY),
                        strokeWidth = 4f
                    )
                    drawCircle(
                        color = GoldAccent,
                        radius = 8f,
                        center = Offset(qiblaX, qiblaY)
                    )
                }

                // Center Needle pointing to Qibla
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(DarkGreen)
                            .border(1.dp, GoldAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🕋", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("কাবা শরীফ", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Calibration & Sensor Info Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.CompassCalibration,
                        contentDescription = "Calibration",
                        tint = CyanBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "কম্পাস নির্ভুল করার নিয়মাবলী:",
                            color = CyanBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "১. মোবাইল ফোনটি সমতল স্থানে (Flat) রাখুন।\n২. চুম্বক, ফ্যান বা ধাতব বস্তু থেকে ফোন দূরে রাখুন।\n৩. কম্পাস সঠিকভাবে কাজ না করলে ফোনটি শূন্যে ইংরেজি ৮ (Figure-8) আকারে ঘোরান।",
                            color = TextWhite.copy(alpha = 0.85f),
                            fontSize = 11.5.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}
