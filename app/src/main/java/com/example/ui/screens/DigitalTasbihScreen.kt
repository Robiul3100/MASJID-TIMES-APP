package com.example.ui.screens

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.AppDatabase
import com.example.data.local.entity.TasbihRecordEntity
import com.example.data.model.DhikrItem
import com.example.data.model.TasbihRecord
import com.example.data.repository.MosqueRepository
import com.example.ui.components.CommonHeader
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.DarkGreenBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceBorder
import com.example.ui.theme.DarkSurfaceElevated
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.GreenDigital
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.RedDigital
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@Composable
fun DigitalTasbihScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val db = remember { AppDatabase.getInstance(context) }
    val savedRecordsEntities by db.tasbihDao().getAllRecordsFlow().collectAsState(initial = emptyList())

    val dhikrs = MosqueRepository.dhikrList
    var selectedDhikr by remember { mutableStateOf(dhikrs.first()) }
    var currentCount by remember { mutableIntStateOf(0) }
    var targetCount by remember { mutableIntStateOf(selectedDhikr.defaultTarget) } // 0 means Free/Unlimited
    var isHapticEnabled by remember { mutableStateOf(true) }
    var isSoundEnabled by remember { mutableStateOf(true) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showTargetReachedDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }

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
            coroutineScope.launch(Dispatchers.IO) {
                val dateStr = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date())
                db.tasbihDao().insertRecord(
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonHeader(
            title = "স্মার্ট ডিজিটাল তসবিহ",
            subtitle = "হ্যাপটিক ফিডব্যাক ও যিকির ট্র্যাকার",
            onBackClick = onBackClick,
            actionIcon = Icons.Default.History,
            actionDescription = "তসবিহ ইতিহাস",
            onActionClick = { showHistoryDialog = true }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Dhikr Selector Carousel
            Text(
                text = "যিকির নির্বাচন করুন",
                color = GoldAccent,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(dhikrs, key = { it.id }) { item ->
                    val isSelected = (item.id == selectedDhikr.id)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) DarkGreen else DarkSurface)
                            .border(
                                width = if (isSelected) 1.5.dp else 1.dp,
                                color = if (isSelected) PrimaryGreen else DarkSurfaceBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                if (selectedDhikr.id != item.id) {
                                    selectedDhikr = item
                                    currentCount = 0
                                    targetCount = item.defaultTarget
                                }
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = item.transliterationBn,
                                color = if (isSelected) NeonGreenGlow else TextWhite,
                                fontSize = 12.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dhikr Meaning & Arabic Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkGreenBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = selectedDhikr.arabicText,
                        color = GoldAccent,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "অর্থ: ${selectedDhikr.meaningBn}",
                        color = TextWhite,
                        fontSize = 12.5.sp,
                        textAlign = TextAlign.Center
                    )
                    if (selectedDhikr.rewardBn.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ফজিলত: ${selectedDhikr.rewardBn}",
                            color = CyanBlue,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
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
                                .background(if (isTgtSelected) CyanBlue.copy(alpha = 0.2f) else DarkSurface)
                                .border(
                                    width = 1.dp,
                                    color = if (isTgtSelected) CyanBlue else DarkSurfaceBorder,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    targetCount = tgt
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isTgtSelected) CyanBlue else TextMuted,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Toggles: Haptic & Sound
                Row(verticalAlignment = Alignment.CenterVertically) {
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
                            imageVector = if (isSoundEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
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

            // Main Interactive Tasbih Bead / Counter Dial
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
                    }
                    .testTag("tasbih_main_tap_button"),
                contentAlignment = Alignment.Center
            ) {
                // Circular Progress Indicator Ring
                CircularProgressIndicator(
                    progress = { animatedProgress },
                    modifier = Modifier.size(228.dp),
                    color = if (targetCount > 0 && currentCount >= targetCount) NeonGreenGlow else PrimaryGreen,
                    strokeWidth = 6.dp,
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
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "$currentCount",
                        color = GreenDigital,
                        fontSize = 52.sp,
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
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Sub-counter statistics
            val totalSavedCount = remember(savedRecordsEntities) {
                savedRecordsEntities.sumOf { it.count }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("বর্তমান সেশন", color = TextMuted, fontSize = 11.sp)
                    Text("$currentCount", color = NeonGreenGlow, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(DarkSurfaceBorder))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("সংরক্ষিত রেকর্ড", color = TextMuted, fontSize = 11.sp)
                    Text("${savedRecordsEntities.size} বার", color = GoldAccent, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
                Box(modifier = Modifier.width(1.dp).height(30.dp).background(DarkSurfaceBorder))
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("সর্বমোট যিকির", color = TextMuted, fontSize = 11.sp)
                    Text("$totalSavedCount", color = CyanBlue, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Reset Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("কাউন্টার রিসেট", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = { Text("আপনি কি বর্তমান গণনা শূন্য (০) করতে চান?", color = TextMuted) },
            containerColor = DarkSurfaceElevated,
            confirmButton = {
                TextButton(
                    onClick = {
                        currentCount = 0
                        showResetDialog = false
                    }
                ) {
                    Text("হ্যাঁ, রিসেট করুন", color = RedDigital, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("বাতিল", color = TextMuted)
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
                    Text("মাশাআল্লাহ! টার্গেট সম্পন্ন", color = TextWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            },
            text = {
                Column {
                    Text(
                        text = "আপনি সফলভাবে $targetCount বার '${selectedDhikr.transliterationBn}' পাঠ সম্পন্ন করেছেন। মহান আল্লাহ আপনার এই যিকির কবুল করুন।",
                        color = TextWhite,
                        fontSize = 13.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "রেকর্ডটি স্থানীয় অফলাইন ডাটাবেসে সংরক্ষণ করা হয়েছে।",
                        color = CyanBlue,
                        fontSize = 11.5.sp
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
                    Text("নতুন করে শুরু করুন", color = PrimaryGreen, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTargetReachedDialog = false }) {
                    Text("চালিয়ে যান", color = TextMuted)
                }
            }
        )
    }

    // History Log Bottom/Dialog View
    if (showHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showHistoryDialog = false },
            title = {
                Text("তসবিহ পাঠের ইতিহাস ও রেকর্ড", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            },
            text = {
                if (savedRecordsEntities.isEmpty()) {
                    Text(
                        "এখনো কোনো টার্গেট সম্পন্ন হওয়ার রেকর্ড সংরক্ষিত হয়নি। যিকির সম্পন্ন হলে স্বয়ংক্রিয়ভাবে এখানে যুক্ত হবে।",
                        color = TextMuted,
                        fontSize = 12.5.sp
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp)
                            .verticalScroll(rememberScrollState()),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        savedRecordsEntities.forEach { item ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurface)
                                    .border(0.5.dp, DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(item.dhikrNameBn, color = TextWhite, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                        Text(item.dateString, color = TextMuted, fontSize = 10.5.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(PrimaryGreen.copy(alpha = 0.2f))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("${item.count} বার", color = NeonGreenGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
                    Text("বন্ধ করুন", color = PrimaryGreen, fontWeight = FontWeight.Bold)
                }
            }
        )
    }
}
