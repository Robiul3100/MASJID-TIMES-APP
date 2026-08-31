package com.robiul.mosquetime.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.AlarmOff
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.ui.theme.ArabicTextStyle
import com.robiul.mosquetime.ui.theme.CyanBlue
import com.robiul.mosquetime.ui.theme.DarkBackground
import com.robiul.mosquetime.ui.theme.DarkGreen
import com.robiul.mosquetime.ui.theme.DarkGreenBorder
import com.robiul.mosquetime.ui.theme.DarkSurface
import com.robiul.mosquetime.ui.theme.DarkSurfaceBorder
import com.robiul.mosquetime.ui.theme.DarkSurfaceElevated
import com.robiul.mosquetime.ui.theme.EmeraldDeep
import com.robiul.mosquetime.ui.theme.GoldAccent
import com.robiul.mosquetime.ui.theme.GreenDigital
import com.robiul.mosquetime.ui.theme.NeonGreenGlow
import com.robiul.mosquetime.ui.theme.PrimaryGreen
import com.robiul.mosquetime.ui.theme.RedDigital
import com.robiul.mosquetime.ui.theme.SolaimanLipiFontFamily
import com.robiul.mosquetime.ui.theme.TextMuted
import com.robiul.mosquetime.ui.theme.TextSubtle
import com.robiul.mosquetime.ui.theme.TextWhite
import com.robiul.mosquetime.util.HapticUtils

data class PrayerScheduleItem(
    val type: PrayerType,
    val bengaliName: String,
    val arabicName: String,
    val time: String,
    val jamathTime: String = "",
    val endingTime: String = "",
    val isJumah: Boolean = false,
    val isActive: Boolean = false,
    val isPassed: Boolean = false,
    val isNext: Boolean = false,
    val isExtra: Boolean = false,
    val reminderNote: String = ""
)

/**
 * Premium Modern Prayer Timetable Card applying UI/UX Psychology:
 * - Isolation Effect (Von Restorff): Active/Next prayer highlighted with neon glow and pulse.
 * - Progressive Disclosure: Tap any row to smoothly expand Jamath time & alarm settings.
 * - Gestalt Grouping: 5 Fard prayers in main group, Extra/Nafl prayers in distinct warm group.
 * - Micro-interactions: Tactile feedback, glowing pulses, and smooth scale transitions.
 * - Color Psychology: Past prayers dimmed, current active highlighted, upcoming clear and bright.
 */
@Composable
fun PrayerScheduleCard(
    scheduleItems: List<PrayerScheduleItem>,
    modifier: Modifier = Modifier,
    nextPrayerCountdown: String = ""
) {
    val context = LocalContext.current
    val view = LocalView.current
    var expandedPrayerType by remember { mutableStateOf<PrayerType?>(null) }
    val alarmStates = remember { mutableStateMapOf<PrayerType, Boolean>() }

    // Split items into 5 Fard prayers and Extra times (Sunrise/Sunset)
    val fardPrayers = remember(scheduleItems) {
        scheduleItems.filter { !it.isExtra }
    }
    val extraPrayers = remember(scheduleItems) {
        scheduleItems.filter { it.isExtra }
    }

    val activeItem = scheduleItems.firstOrNull { it.isActive }
    val nextItem = scheduleItems.firstOrNull { it.isNext } ?: scheduleItems.firstOrNull { !it.isPassed && !it.isActive }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF141F18),
                        Color(0xFF0F1812)
                    )
                )
            )
            .border(
                width = 1.2.dp,
                brush = Brush.verticalGradient(
                    colors = listOf(
                        NeonGreenGlow.copy(alpha = 0.7f),
                        PrimaryGreen.copy(alpha = 0.4f),
                        DarkGreenBorder.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(vertical = 10.dp, horizontal = 6.dp)
            .testTag("prayer_schedule_card")
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // -------------------------------------------------------------
            // 1. Header: Title + Live Status Indicator
            // -------------------------------------------------------------
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(NeonGreenGlow)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "নামাজের সময়সূচি",
                        color = CyanBlue,
                        fontSize = 15.5.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.3.sp
                    )
                }

                // Active or Next Prayer Quick Badge
                val badgeText = when {
                    activeItem != null -> "চলমান: ${activeItem.bengaliName}"
                    nextItem != null -> "পরবর্তী: ${nextItem.bengaliName}"
                    else -> "দৈনিক ওয়াক্ত"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(EmeraldDeep.copy(alpha = 0.7f))
                        .border(0.8.dp, PrimaryGreen.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                        .padding(horizontal = 8.dp, vertical = 2.5.dp)
                ) {
                    Text(
                        text = badgeText,
                        color = NeonGreenGlow,
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 6.dp),
                thickness = 0.6.dp,
                color = DarkSurfaceBorder
            )

            // -------------------------------------------------------------
            // 2. Primary 5 Fard Prayers (Gestalt Rhythm)
            // -------------------------------------------------------------
            fardPrayers.forEachIndexed { index, item ->
                val isExpanded = expandedPrayerType == item.type
                val isAlarmOn = alarmStates.getOrPut(item.type) { true }

                InteractivePrayerRow(
                    item = item,
                    isExpanded = isExpanded,
                    isAlarmOn = isAlarmOn,
                    onToggleExpand = {
                        HapticUtils.performLongPressHaptic(view)
                        expandedPrayerType = if (isExpanded) null else item.type
                    },
                    onToggleAlarm = { newState ->
                        HapticUtils.performLongPressHaptic(view)
                        alarmStates[item.type] = newState
                        val msg = if (newState) "${item.bengaliName} আজান অ্যালার্ম চালু" else "${item.bengaliName} আজান অ্যালার্ম বন্ধ"
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    }
                )

                if (index < fardPrayers.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        thickness = 0.4.dp,
                        color = DarkSurfaceBorder.copy(alpha = 0.35f)
                    )
                }
            }

            // -------------------------------------------------------------
            // 3. Extra / Nafl Prayers Section (Distinct Warm Accent)
            // -------------------------------------------------------------
            if (extraPrayers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = 0.5.dp,
                        color = GoldAccent.copy(alpha = 0.3f)
                    )
                    Text(
                        text = " নফল ও অতিরিক্ত ওয়াক্ত ",
                        color = GoldAccent,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                    HorizontalDivider(
                        modifier = Modifier.weight(1f),
                        thickness = 0.5.dp,
                        color = GoldAccent.copy(alpha = 0.3f)
                    )
                }

                extraPrayers.forEachIndexed { index, item ->
                    val isExpanded = expandedPrayerType == item.type
                    val isAlarmOn = alarmStates.getOrPut(item.type) { false }

                    InteractivePrayerRow(
                        item = item,
                        isExpanded = isExpanded,
                        isAlarmOn = isAlarmOn,
                        onToggleExpand = {
                            HapticUtils.performLongPressHaptic(view)
                            expandedPrayerType = if (isExpanded) null else item.type
                        },
                        onToggleAlarm = { newState ->
                            HapticUtils.performLongPressHaptic(view)
                            alarmStates[item.type] = newState
                        }
                    )

                    if (index < extraPrayers.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            thickness = 0.4.dp,
                            color = DarkSurfaceBorder.copy(alpha = 0.35f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * High-fidelity interactive prayer row featuring Progressive Disclosure and Attention Cues
 */
@Composable
private fun InteractivePrayerRow(
    item: PrayerScheduleItem,
    isExpanded: Boolean,
    isAlarmOn: Boolean,
    onToggleExpand: () -> Unit,
    onToggleAlarm: (Boolean) -> Unit
) {
    // Pulse animation for active prayer
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Color psychology state
    val contentAlpha = when {
        item.isActive -> 1f
        item.isPassed -> 0.55f
        else -> 0.95f
    }

    val rowBgColor = when {
        item.isActive -> EmeraldDeep.copy(alpha = 0.55f)
        item.isNext -> Color(0xFF1B2E24).copy(alpha = 0.45f)
        isExpanded -> Color(0xFF14241A)
        else -> Color.Transparent
    }

    val rowBorderColor = when {
        item.isActive -> NeonGreenGlow.copy(alpha = pulseAlpha)
        item.isNext -> PrimaryGreen.copy(alpha = 0.4f)
        isExpanded -> DarkGreenBorder
        else -> Color.Transparent
    }

    val nameColor = when {
        item.isJumah -> RedDigital
        item.isActive -> NeonGreenGlow
        item.isPassed -> TextMuted
        item.isExtra -> GoldAccent
        else -> PrimaryGreen
    }

    val timeColor = when {
        item.isJumah -> GreenDigital
        item.isActive -> NeonGreenGlow
        item.isPassed -> RedDigital.copy(alpha = 0.6f)
        else -> RedDigital
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(rowBgColor)
            .border(if (item.isActive || item.isNext || isExpanded) 1.dp else 0.dp, rowBorderColor, RoundedCornerShape(10.dp))
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = ripple(bounded = true, color = PrimaryGreen.copy(alpha = 0.25f)),
                onClick = onToggleExpand
            )
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .testTag("prayer_row_${item.type.name}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // -------------------------------------------------------------
            // Main Glanceable Row (Essential Info)
            // -------------------------------------------------------------
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Left: Icon + Bengali Name + Passed/Active tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1.35f)
                ) {
                    PrayerIcon(
                        type = item.type,
                        isJumah = item.isJumah,
                        modifier = Modifier
                            .size(22.dp)
                            .scale(if (item.isActive) 1.08f else 1f)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = item.bengaliName,
                                color = nameColor,
                                fontSize = 14.sp,
                                fontWeight = if (item.isActive) FontWeight.Bold else FontWeight.SemiBold
                            )

                            if (item.isActive) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .clip(CircleShape)
                                        .background(NeonGreenGlow)
                                )
                            }
                        }

                        if (item.isPassed && !item.isActive) {
                            Text(
                                text = "ওয়াক্ত শেষ ✓",
                                color = TextSubtle,
                                fontSize = 9.sp
                            )
                        } else if (item.isActive) {
                            Text(
                                text = "ওয়াক্ত চলছে",
                                color = NeonGreenGlow,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Center: Arabic Badge with Amiri Arabic Font
                Box(
                    modifier = Modifier.weight(1.0f),
                    contentAlignment = Alignment.Center
                ) {
                    ArabicPrayerBadge(arabicName = item.arabicName)
                }

                // Right: 7-Segment Digital Clock Time + Expand indicator
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.weight(1.15f)
                ) {
                    TableDigitalTime(
                        timeString = item.time,
                        activeColor = timeColor,
                        digitWidth = 12.dp,
                        digitHeight = 22.dp
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    Icon(
                        imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = if (item.isActive) PrimaryGreen else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // -------------------------------------------------------------
            // Expanded Details (Progressive Disclosure)
            // -------------------------------------------------------------
            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically() + fadeIn(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 4.dp)
                        .background(Color(0xFF0C140F), RoundedCornerShape(8.dp))
                        .border(0.8.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Jamath Time
                        Column {
                            Text(
                                text = "মসজিদে জামাত সময়",
                                color = TextMuted,
                                fontSize = 10.5.sp
                            )
                            Text(
                                text = if (item.jamathTime.isNotEmpty()) item.jamathTime else "${item.time} (আজান)",
                                color = GoldAccent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Alarm Toggle Switch
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (isAlarmOn) Icons.Default.Alarm else Icons.Default.AlarmOff,
                                contentDescription = null,
                                tint = if (isAlarmOn) PrimaryGreen else TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAlarmOn) "আজান এলার্ট" else "বন্ধ",
                                color = if (isAlarmOn) PrimaryGreen else TextMuted,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Switch(
                                checked = isAlarmOn,
                                onCheckedChange = onToggleAlarm,
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = PrimaryGreen,
                                    checkedTrackColor = EmeraldDeep,
                                    uncheckedThumbColor = TextMuted,
                                    uncheckedTrackColor = DarkSurfaceElevated
                                ),
                                modifier = Modifier.scale(0.75f)
                            )
                        }
                    }

                    if (item.reminderNote.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "💡 ${item.reminderNote}",
                            color = TextMuted,
                            fontSize = 10.5.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }
        }
    }
}
