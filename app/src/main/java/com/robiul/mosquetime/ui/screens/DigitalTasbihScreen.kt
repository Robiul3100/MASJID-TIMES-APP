package com.robiul.mosquetime.ui.screens

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.robiul.mosquetime.data.local.entity.TasbihRecordEntity
import com.robiul.mosquetime.data.model.DhikrItem
import com.robiul.mosquetime.data.model.TasbihRecord
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun DigitalTasbihScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DigitalTasbihViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val savedRecordsEntities by viewModel.savedRecords.collectAsState()

    val defaultDhikrs = remember { MosqueRepository.dhikrList.toMutableStateList() }
    var selectedDhikr by remember { mutableStateOf(defaultDhikrs.first()) }
    var currentCount by remember { mutableIntStateOf(0) }
    var targetCount by remember { mutableIntStateOf(selectedDhikr.defaultTarget) } // 0 means Free/Unlimited
    var isHapticEnabled by remember { mutableStateOf(true) }
    var isSoundEnabled by remember { mutableStateOf(true) }
    var isFullScreenMode by remember { mutableStateOf(false) }

    var showResetDialog by remember { mutableStateOf(false) }
    var showTargetReachedDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var showCustomDhikrDialog by remember { mutableStateOf(false) }

    val vibrator = remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    val toneGenerator = remember {
        try {
            ToneGenerator(AudioManager.STREAM_NOTIFICATION, 60)
        } catch (e: Exception) {
            null
        }
    }

    fun triggerFeedback(isTarget: Boolean = false) {
        if (isHapticEnabled) {
            if (isTarget) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 80, 60, 140)
                    val amplitudes = intArrayOf(0, 255, 0, 255)
                    vibrator?.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(250)
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createOneShot(28, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(25)
                }
            }
        }
        if (isSoundEnabled) {
            if (isTarget) {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 180)
            } else {
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 35)
            }
        }
    }

    fun handleCountIncrement() {
        val next = currentCount + 1
        currentCount = next

        if (targetCount > 0 && next == targetCount) {
            triggerFeedback(isTarget = true)
            showTargetReachedDialog = true
            // Save to Room database
            coroutineScope.launch {
                val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                viewModel.saveRecord(
                    TasbihRecordEntity.fromDomainModel(
                        TasbihRecord(
                            id = UUID.randomUUID().toString(),
                            dhikrId = selectedDhikr.id,
                            dhikrNameBn = selectedDhikr.transliterationBn,
                            count = next,
                            target = targetCount,
                            dateString = dateStr
                        )
                    )
                )
            }
        } else {
            triggerFeedback(isTarget = false)
        }
    }

    val progress = if (targetCount > 0) {
        (currentCount.toFloat() / targetCount.toFloat()).coerceIn(0f, 1f)
    } else {
        1f
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 150),
        label = "tasbih_progress"
    )

    if (isFullScreenMode) {
        // Full Screen Blind Tap Mode
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    handleCountIncrement()
                }
                .padding(24.dp)
        ) {
            // Exit Full Screen Button
            IconButton(
                onClick = { isFullScreenMode = false },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .clip(CircleShape)
                    .background(DarkSurfaceElevated)
            ) {
                Icon(Icons.Default.FullscreenExit, contentDescription = "Exit Fullscreen", tint = GoldAccent)
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedDhikr.arabicText,
                    fontFamily = AmiriFontFamily,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = selectedDhikr.transliterationBn,
                    fontSize = 16.sp,
                    color = TextWhite,
                    fontFamily = SolaimanLipiFontFamily
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "$currentCount",
                    fontSize = 84.sp,
                    fontWeight = FontWeight.Black,
                    color = NeonGreenGlow,
                    fontFamily = FontFamily.Monospace
                )
                if (targetCount > 0) {
                    Text(
                        text = "টার্গেট: $targetCount",
                        fontSize = 16.sp,
                        color = CyanBlue,
                        fontFamily = SolaimanLipiFontFamily
                    )
                }
                Spacer(modifier = Modifier.height(32.dp))
                Text(
                    text = "স্ক্রিনের যেকোনো স্থানে স্পর্শ করুন 👆",
                    fontSize = 12.5.sp,
                    color = TextMuted,
                    fontFamily = SolaimanLipiFontFamily
                )
            }
        }
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            containerColor = DarkBackground,
            topBar = {
                CommonHeader(
                    title = "স্মার্ট ডিজিটাল তসবিহ",
                    subtitle = "হ্যাপটিক ফিডব্যাক ও যিকির ট্র্যাকার",
                    onBackClick = onBackClick,
                    actionIcon = Icons.Default.History,
                    actionDescription = "তসবিহ ইতিহাস",
                    onActionClick = { showHistoryDialog = true }
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
                // Dhikr Selector Header & Add Custom Dhikr
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "যিকির নির্বাচন করুন",
                        color = GoldAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SolaimanLipiFontFamily
                    )

                    TextButton(
                        onClick = { showCustomDhikrDialog = true },
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("কাস্টম যিকির", color = PrimaryGreen, fontSize = 12.sp, fontFamily = SolaimanLipiFontFamily)
                    }
                }

                // Dhikr Carousel
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(defaultDhikrs, key = { it.id }) { item ->
                        val isSelected = (item.id == selectedDhikr.id)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) DarkGreen else DarkSurfaceElevated)
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) PrimaryGreen else DarkGreenBorder.copy(alpha = 0.5f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable {
                                    if (selectedDhikr.id != item.id) {
                                        selectedDhikr = item
                                        currentCount = 0
                                        targetCount = item.defaultTarget
                                    }
                                }
                                .padding(horizontal = 14.dp, vertical = 10.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = item.transliterationBn,
                                    color = if (isSelected) NeonGreenGlow else TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Dhikr Meaning & Arabic Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, DarkGreenBorder, RoundedCornerShape(16.dp))
                        .padding(16.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = selectedDhikr.arabicText,
                            fontFamily = AmiriFontFamily,
                            color = GoldAccent,
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "অর্থ: ${selectedDhikr.meaningBn}",
                            color = TextWhite,
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = SolaimanLipiFontFamily
                        )
                        if (selectedDhikr.rewardBn.isNotBlank()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "💡 ফজিলত: ${selectedDhikr.rewardBn}",
                                color = CyanBlue,
                                fontSize = 11.5.sp,
                                textAlign = TextAlign.Center,
                                fontFamily = SolaimanLipiFontFamily
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Target Selector & Control Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Target chips
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(33, 100, 1000, 0).forEach { tgt ->
                            val isTgtSelected = (targetCount == tgt)
                            val label = if (tgt == 0) "মুক্ত" else "$tgt"
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isTgtSelected) CyanBlue.copy(alpha = 0.2f) else DarkSurfaceElevated)
                                    .border(
                                        width = 1.dp,
                                        color = if (isTgtSelected) CyanBlue else DarkGreenBorder.copy(alpha = 0.4f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        targetCount = tgt
                                    }
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (isTgtSelected) CyanBlue else TextMuted,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        }
                    }

                    // Toggles: Fullscreen, Haptic, Sound, Reset
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { isFullScreenMode = true },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(Icons.Default.Fullscreen, contentDescription = "Fullscreen", tint = GoldAccent, modifier = Modifier.size(20.dp))
                        }

                        IconButton(
                            onClick = { isHapticEnabled = !isHapticEnabled },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Vibration,
                                contentDescription = "Haptic Toggle",
                                tint = if (isHapticEnabled) PrimaryGreen else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { isSoundEnabled = !isSoundEnabled },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = if (isSoundEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeMute,
                                contentDescription = "Sound Toggle",
                                tint = if (isSoundEnabled) CyanBlue else TextMuted,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        IconButton(
                            onClick = { showResetDialog = true },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Reset",
                                tint = RedDigital,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Main Interactive Tasbih Dial
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(DarkSurfaceElevated, DarkSurface, DarkBackground)
                            )
                        )
                        .border(2.dp, DarkGreenBorder, CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) {
                            handleCountIncrement()
                        },
                    contentAlignment = Alignment.Center
                ) {
                    // Circular Progress Indicator Ring
                    CircularProgressIndicator(
                        progress = { animatedProgress },
                        modifier = Modifier.size(228.dp),
                        color = if (targetCount > 0 && currentCount >= targetCount) NeonGreenGlow else PrimaryGreen,
                        strokeWidth = 7.dp,
                        trackColor = DarkSurfaceBorder.copy(alpha = 0.5f),
                        strokeCap = StrokeCap.Round,
                    )

                    // Central Counter Display
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (targetCount > 0) "টার্গেট: $targetCount" else "মুক্ত গণনা",
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            fontFamily = SolaimanLipiFontFamily
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "$currentCount",
                            color = GreenDigital,
                            fontSize = 54.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(PrimaryGreen.copy(alpha = 0.15f))
                                .border(1.dp, PrimaryGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 14.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "ট্যাপ করুন 👆",
                                color = PrimaryGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = SolaimanLipiFontFamily
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Sub-counter statistics
                val totalSavedCount = remember(savedRecordsEntities) {
                    savedRecordsEntities.sumOf { it.count }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, DarkGreenBorder.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("বর্তমান সেশন", color = TextMuted, fontSize = 11.5.sp, fontFamily = SolaimanLipiFontFamily)
                        Text("$currentCount", color = NeonGreenGlow, fontSize = 17.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(DarkSurfaceBorder))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("সংরক্ষিত রেকর্ড", color = TextMuted, fontSize = 11.5.sp, fontFamily = SolaimanLipiFontFamily)
                        Text("${savedRecordsEntities.size} বার", color = GoldAccent, fontSize = 17.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                    }
                    Box(modifier = Modifier.width(1.dp).height(32.dp).background(DarkSurfaceBorder))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("সর্বমোট যিকির", color = TextMuted, fontSize = 11.5.sp, fontFamily = SolaimanLipiFontFamily)
                        Text("$totalSavedCount", color = CyanBlue, fontSize = 17.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }

    // Custom Dhikr Builder Dialog
    if (showCustomDhikrDialog) {
        var customNameBn by remember { mutableStateOf("") }
        var customArabic by remember { mutableStateOf("") }
        var customMeaning by remember { mutableStateOf("") }
        var customTargetStr by remember { mutableStateOf("100") }

        Dialog(onDismissRequest = { showCustomDhikrDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, PrimaryGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Text(
                        text = "নতুন কাস্টম যিকির যুক্ত করুন",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        fontFamily = SolaimanLipiFontFamily
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = customNameBn,
                        onValueChange = { customNameBn = it },
                        label = { Text("যিকিরের নাম (যেমন: সাইয়্যিদুল ইস্তিগফার)", fontSize = 12.sp, color = TextMuted, fontFamily = SolaimanLipiFontFamily) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGreen, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customArabic,
                        onValueChange = { customArabic = it },
                        label = { Text("আরবি টেক্সট (ঐচ্ছিক)", fontSize = 12.sp, color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGreen, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customMeaning,
                        onValueChange = { customMeaning = it },
                        label = { Text("অর্থ ও ফজিলত (ঐচ্ছিক)", fontSize = 12.sp, color = TextMuted, fontFamily = SolaimanLipiFontFamily) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGreen, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = customTargetStr,
                        onValueChange = { customTargetStr = it },
                        label = { Text("টার্গেট সংখ্যা (যেমন: 33, 100, 500)", fontSize = 12.sp, color = TextMuted, fontFamily = SolaimanLipiFontFamily) },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimaryGreen, focusedTextColor = TextWhite, unfocusedTextColor = TextWhite)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = { showCustomDhikrDialog = false }) {
                            Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (customNameBn.isNotBlank()) {
                                    val tgt = customTargetStr.toIntOrNull() ?: 100
                                    val newDhikr = DhikrItem(
                                        id = "custom_${System.currentTimeMillis()}",
                                        arabicText = if (customArabic.isNotBlank()) customArabic else customNameBn,
                                        transliterationBn = customNameBn,
                                        meaningBn = if (customMeaning.isNotBlank()) customMeaning else "কাস্টম যিকির",
                                        rewardBn = "",
                                        defaultTarget = tgt
                                    )
                                    defaultDhikrs.add(0, newDhikr)
                                    selectedDhikr = newDhikr
                                    targetCount = tgt
                                    currentCount = 0
                                    showCustomDhikrDialog = false
                                    Toast.makeText(context, "যিকির সফলভাবে যুক্ত হয়েছে", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                        ) {
                            Text("সংরক্ষণ", color = DarkBackground, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                        }
                    }
                }
            }
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("কাউন্টার রিসেট", color = TextWhite, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily) },
            text = { Text("আপনি কি বর্তমান গণনা শূন্য (০) করতে চান?", color = TextMuted, fontFamily = SolaimanLipiFontFamily) },
            containerColor = DarkSurfaceElevated,
            confirmButton = {
                TextButton(
                    onClick = {
                        currentCount = 0
                        showResetDialog = false
                    }
                ) {
                    Text("হ্যাঁ, রিসেট করুন", color = RedDigital, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
                }
            }
        )
    }

    // Target Completed Celebration Dialog
    if (showTargetReachedDialog) {
        AlertDialog(
            onDismissRequest = { showTargetReachedDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreenGlow, modifier = Modifier.size(26.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("মাশাআল্লাহ! টার্গেট সম্পন্ন", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = SolaimanLipiFontFamily)
                }
            },
            text = {
                Column {
                    Text(
                        text = "আপনি সফলভাবে $targetCount বার '${selectedDhikr.transliterationBn}' পাঠ সম্পন্ন করেছেন। মহান আল্লাহ আপনার এই যিকির কবুল করুন।",
                        color = TextWhite,
                        fontSize = 13.5.sp,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "রেকর্ডটি স্থানীয় অফলাইন ডাটাবেসে স্বয়ংক্রিয়ভাবে সংরক্ষণ করা হয়েছে।",
                        color = CyanBlue,
                        fontSize = 12.sp,
                        fontFamily = SolaimanLipiFontFamily
                    )
                }
            },
            containerColor = DarkSurfaceElevated,
            confirmButton = {
                TextButton(
                    onClick = {
                        currentCount = 0
                        showTargetReachedDialog = false
                    }
                ) {
                    Text("নতুন করে শুরু করুন", color = PrimaryGreen, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTargetReachedDialog = false }) {
                    Text("চালিয়ে যান", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
                }
            }
        )
    }

    // History Log Dialog View
    if (showHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("তসবিহ পাঠের ইতিহাস", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = SolaimanLipiFontFamily)
                    if (savedRecordsEntities.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                viewModel.clearAllRecords()
                                Toast.makeText(context, "ইতিহাস মুছে ফেলা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.DeleteOutline, contentDescription = "Clear", tint = RedDigital, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            },
            text = {
                if (savedRecordsEntities.isEmpty()) {
                    Text(
                        "এখনো কোনো টার্গেট সম্পন্ন হওয়ার রেকর্ড সংরক্ষিত হয়নি। যিকির সম্পন্ন হলে স্বয়ংক্রিয়ভাবে এখানে যুক্ত হবে।",
                        color = TextMuted,
                        fontSize = 13.sp,
                        fontFamily = SolaimanLipiFontFamily
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        savedRecordsEntities.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(DarkSurface)
                                    .border(0.6.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(item.dhikrNameBn, color = TextWhite, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                                        Text(item.dateString, color = TextMuted, fontSize = 11.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(PrimaryGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text("${item.count} বার", color = NeonGreenGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                                    }
                                }
                            }
                        }
                    }
                }
            },
            containerColor = DarkSurfaceElevated,
            confirmButton = {
                TextButton(onClick = { showHistoryDialog = false }) {
                    Text("বন্ধ করুন", color = PrimaryGreen, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                }
            }
        )
    }
}
