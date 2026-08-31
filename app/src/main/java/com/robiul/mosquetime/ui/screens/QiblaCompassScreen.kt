package com.robiul.mosquetime.ui.screens

import android.hardware.SensorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import androidx.compose.ui.window.Dialog
import com.robiul.mosquetime.data.model.District
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.sensor.QiblaCompassManager
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.theme.*
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun QiblaCompassScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val compassManager = remember { QiblaCompassManager(context) }
    val qiblaState by compassManager.qiblaState.collectAsState()

    var selectedDistrict by remember { mutableStateOf(MosqueRepository.districts.first()) }
    var showDistrictDialog by remember { mutableStateOf(false) }
    var showCalibrationDialog by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        compassManager.startListening()
        onDispose {
            compassManager.stopListening()
        }
    }

    // Update coordinates whenever selected district changes
    LaunchedEffect(selectedDistrict) {
        val baseLat = 23.8103
        // Approximate longitude based on fajrOffsetMinutes from Dhaka
        val baseLng = 90.4125 - (selectedDistrict.fajrOffsetMinutes * 0.25)
        compassManager.updateUserLocation(baseLat, baseLng)
    }

    val animatedDialRotation by animateFloatAsState(
        targetValue = -qiblaState.currentAzimuth,
        animationSpec = tween(durationMillis = 160, easing = FastOutSlowInEasing),
        label = "CompassDialRotation"
    )

    val dialBorderColor by animateColorAsState(
        targetValue = if (qiblaState.isAligned) NeonGreenGlow else DarkGreenBorder,
        animationSpec = tween(durationMillis = 200),
        label = "DialBorderColor"
    )

    val relativeAngle = qiblaState.relativeAngle
    val deviation = abs((relativeAngle + 180) % 360 - 180)

    val directionAdvice = when {
        qiblaState.isAligned -> "✓ আপনি সঠিক কিবলামুখী আছেন"
        relativeAngle in 1.0..180.0 -> "ডানে আরও ${deviation.toInt()}° ঘোরান 👉"
        else -> "👈 বামে আরও ${deviation.toInt()}° ঘোরান"
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            CommonHeader(
                title = "কিবলা কম্পাস",
                subtitle = "${selectedDistrict.nameBn} • পবিত্র কাবা শরীফের দিকনির্ণয়",
                onBackClick = onBackClick,
                actionIcon = Icons.Default.LocationOn,
                actionDescription = "জেলা নির্বাচন",
                onActionClick = { showDistrictDialog = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // District Switcher Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable { showDistrictDialog = true }
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "বর্তমান অবস্থান: ${selectedDistrict.nameBn}",
                            color = TextWhite,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SolaimanLipiFontFamily
                        )
                        Text(
                            text = "কাবার দূরত্ব: প্রায় ${qiblaState.distanceKm.toInt()} কি.মি.",
                            color = TextMuted,
                            fontSize = 11.sp,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(PrimaryGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("পরিবর্তন", color = PrimaryGreen, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Qibla Status Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        if (qiblaState.isAligned) DarkGreen.copy(alpha = 0.85f)
                        else DarkSurfaceElevated
                    )
                    .border(
                        1.5.dp,
                        if (qiblaState.isAligned) NeonGreenGlow else DarkGreenBorder,
                        RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = directionAdvice,
                            color = if (qiblaState.isAligned) NeonGreenGlow else GoldAccent,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SolaimanLipiFontFamily
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (qiblaState.isAligned) "মোবাইলে হালকা ভাইব্রেশন দিয়ে নিশ্চিত করা হয়েছে"
                            else "কিবলা কোণ: ${qiblaState.qiblaBearing.toInt()}° (পশ্চিম-উত্তর)",
                            color = if (qiblaState.isAligned) TextWhite else TextMuted,
                            fontSize = 12.sp,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (qiblaState.isAligned) NeonGreenGlow.copy(alpha = 0.2f) else DarkSurface)
                            .border(1.dp, if (qiblaState.isAligned) NeonGreenGlow else DarkSurfaceBorder, RoundedCornerShape(10.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${qiblaState.qiblaBearing.toInt()}°",
                            color = if (qiblaState.isAligned) NeonGreenGlow else GoldAccent,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Visual Compass Dial
            Box(
                modifier = Modifier
                    .size(290.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            listOf(DarkSurfaceElevated, DarkSurface, DarkBackground)
                        )
                    )
                    .border(3.dp, dialBorderColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                // Rotating dial canvas
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(animatedDialRotation)
                ) {
                    val center = Offset(size.width / 2, size.height / 2)
                    val radius = size.width / 2 - 20

                    // Draw outer graduations
                    for (i in 0 until 360 step 10) {
                        val rad = Math.toRadians(i.toDouble())
                        val isMajor = (i % 90 == 0)
                        val isMedium = (i % 30 == 0)
                        val tickLen = if (isMajor) 16f else if (isMedium) 10f else 6f

                        val startX = (center.x + (radius - tickLen) * sin(rad)).toFloat()
                        val startY = (center.y - (radius - tickLen) * cos(rad)).toFloat()
                        val endX = (center.x + radius * sin(rad)).toFloat()
                        val endY = (center.y - radius * cos(rad)).toFloat()

                        drawLine(
                            color = when {
                                i == 0 -> RedDigital
                                isMajor -> PrimaryGreen
                                isMedium -> GoldAccent.copy(alpha = 0.6f)
                                else -> DarkSurfaceBorder
                            },
                            start = Offset(startX, startY),
                            end = Offset(endX, endY),
                            strokeWidth = if (isMajor) 3f else 1.5f
                        )
                    }

                    // Draw Qibla pointer vector from center towards exact bearing
                    val qiblaRad = Math.toRadians(qiblaState.qiblaBearing.toDouble())
                    val qiblaX = (center.x + (radius - 28) * sin(qiblaRad)).toFloat()
                    val qiblaY = (center.y - (radius - 28) * cos(qiblaRad)).toFloat()

                    drawLine(
                        color = GoldAccent,
                        start = center,
                        end = Offset(qiblaX, qiblaY),
                        strokeWidth = 3.5f
                    )
                    drawCircle(
                        color = GoldAccent,
                        radius = 10f,
                        center = Offset(qiblaX, qiblaY)
                    )
                    drawCircle(
                        color = if (qiblaState.isAligned) NeonGreenGlow else DarkBackground,
                        radius = 5f,
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
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(if (qiblaState.isAligned) DarkGreen else DarkSurfaceElevated)
                            .border(1.5.dp, if (qiblaState.isAligned) NeonGreenGlow else GoldAccent, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🕋", fontSize = 28.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (qiblaState.isAligned) "কিবলা লকড" else "পবিত্র কা'বা",
                        color = if (qiblaState.isAligned) NeonGreenGlow else GoldAccent,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SolaimanLipiFontFamily
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Heading & Azimuth Details
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkGreenBorder.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("বর্তমান দিক (হেডিং)", color = TextMuted, fontSize = 11.sp, fontFamily = SolaimanLipiFontFamily)
                    Text("${qiblaState.currentAzimuth.toInt()}°", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                Box(modifier = Modifier.width(1.dp).height(28.dp).background(DarkSurfaceBorder))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("কিবলার কোণ", color = TextMuted, fontSize = 11.sp, fontFamily = SolaimanLipiFontFamily)
                    Text("${qiblaState.qiblaBearing.toInt()}°", color = GoldAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                }
                Box(modifier = Modifier.width(1.dp).height(28.dp).background(DarkSurfaceBorder))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("পার্থক্য", color = TextMuted, fontSize = 11.sp, fontFamily = SolaimanLipiFontFamily)
                    Text(
                        "${deviation.toInt()}°",
                        color = if (qiblaState.isAligned) NeonGreenGlow else RedDigital,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Calibration Banner Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .clickable { showCalibrationDialog = true }
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CompassCalibration,
                        contentDescription = "Calibration",
                        tint = CyanBlue,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "কম্পাস নির্ভুল করার নিয়ম ও টিপস",
                            color = CyanBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SolaimanLipiFontFamily
                        )
                        Text(
                            text = "সঠিক পাঠ পেতে মোবাইল সমতলে রেখে 'Figure-8' নিয়মে ঘুরান",
                            color = TextWhite.copy(alpha = 0.8f),
                            fontSize = 11.5.sp,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                    Icon(Icons.Default.ChevronRight, contentDescription = null, tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }

    // District Selector Dialog
    if (showDistrictDialog) {
        var searchQuery by remember { mutableStateOf("") }
        val filteredDistricts = remember(searchQuery) {
            if (searchQuery.isBlank()) MosqueRepository.districts
            else MosqueRepository.districts.filter {
                it.nameBn.contains(searchQuery, ignoreCase = true) ||
                        it.nameEn.contains(searchQuery, ignoreCase = true)
            }
        }

        Dialog(onDismissRequest = { showDistrictDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(480.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, PrimaryGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "জেলা নির্বাচন করুন (৬৪ জেলা)",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldAccent,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("জেলার নাম দিয়ে খুঁজুন...", fontSize = 12.5.sp, color = TextMuted, fontFamily = SolaimanLipiFontFamily) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryGreen) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        items(filteredDistricts, key = { it.id }) { dist ->
                            val isSelected = dist.id == selectedDistrict.id
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isSelected) DarkGreen else DarkSurface)
                                    .border(
                                        width = if (isSelected) 1.5.dp else 0.5.dp,
                                        color = if (isSelected) PrimaryGreen else DarkSurfaceBorder,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .clickable {
                                        selectedDistrict = dist
                                        showDistrictDialog = false
                                    }
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = dist.nameBn,
                                            color = if (isSelected) NeonGreenGlow else TextWhite,
                                            fontSize = 13.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            fontFamily = SolaimanLipiFontFamily
                                        )
                                        Text(
                                            text = dist.nameEn,
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }

                                    if (isSelected) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreenGlow, modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { showDistrictDialog = false },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                    ) {
                        Text("বন্ধ করুন", color = DarkBackground, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                    }
                }
            }
        }
    }

    // Calibration & Guidance Modal Dialog
    if (showCalibrationDialog) {
        AlertDialog(
            onDismissRequest = { showCalibrationDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CompassCalibration, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("কম্পাস নির্ভুল করার নিয়ম", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = SolaimanLipiFontFamily)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "১. মোবাইল ফোনটি পুরোপুরি সমতল স্থানে (Flat Surface) রাখুন।",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    Text(
                        text = "২. যেকোনো চুম্বক, স্পিকার, মোটর বা ধাতব বস্তু থেকে মোবাইল দূরে রাখুন।",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    Text(
                        text = "৩. কম্পাস আটকে গেলে বা ভুল দেখালে ফোনটি হাতে নিয়ে শূন্যে ইংরেজি ৮ (Figure-8) আকারে ৩-৪ বার ঘোরান।",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    Text(
                        text = "💡 বাংলাদেশ থেকে কিবলা সাধারণত পশ্চিম-উত্তর কোণে (প্রায় ২৭৫° থেকে ২৮০° এর মধ্যে) অবস্থিত।",
                        color = GoldAccent,
                        fontSize = 12.5.sp,
                        fontFamily = SolaimanLipiFontFamily
                    )
                }
            },
            containerColor = DarkSurfaceElevated,
            confirmButton = {
                TextButton(onClick = { showCalibrationDialog = false }) {
                    Text("বুঝেছি", color = PrimaryGreen, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                }
            }
        )
    }
}
