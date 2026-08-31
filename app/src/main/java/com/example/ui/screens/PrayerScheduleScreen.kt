package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alarm.PrayerAlarmScheduler
import com.example.model.PrayerTimeItem
import com.example.ui.theme.*
import com.example.util.HapticUtils

@Composable
fun PrayerScheduleScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current

    var prayers by remember {
        mutableStateOf(
            listOf(
                PrayerTimeItem("fajr", "Fajr", "ফজর", "০৫:০২", "০৫:১৫", isAlarmEnabled = true, isNext = false),
                PrayerTimeItem("dhuhr", "Dhuhr", "যোহর", "১২:১৫", "০১:১৫", isAlarmEnabled = true, isNext = true),
                PrayerTimeItem("asr", "Asr", "আসর", "০৪:৪৫", "০৫:০০", isAlarmEnabled = true, isNext = false),
                PrayerTimeItem("maghrib", "Maghrib", "মাগরিব", "০৬:২৪", "০৬:২৮", isAlarmEnabled = true, isNext = false),
                PrayerTimeItem("isha", "Isha", "ইশা", "০৭:৪৫", "০৮:১৫", isAlarmEnabled = true, isNext = false)
            )
        )
    }

    var selectedDistrict by remember { mutableStateOf("ঢাকা (Dhaka)") }
    var showTestAlarmSnack by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 24.dp)
    ) {
        // Next Prayer Hero Card
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.4f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(EmeraldDeep.copy(alpha = 0.7f), DarkSurface)
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryGreen)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "পরবর্তী নামাজ: যোহর",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryGreen
                                )
                            }

                            Text(
                                text = "আজান বাকি: ১ ঘণ্টা ১২ মি.",
                                fontSize = 11.sp,
                                color = GoldAccent,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "আজান",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = "১২:১৫ PM",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = TextWhite
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "জায়ামাত / একামত",
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                                Text(
                                    text = "০১:১৫ PM",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = GoldAccent
                                )
                            }
                        }
                    }
                }
            }
        }

        // Test Alarm Trigger Button (AlarmManager verification)
        item {
            Button(
                onClick = {
                    HapticUtils.performLongPressHaptic(view)
                    PrayerAlarmScheduler.scheduleTestAlarm(context)
                    showTestAlarmSnack = true
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = DarkSurfaceElevated,
                    contentColor = PrimaryGreen
                ),
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkGreenBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Outlined.AlarmOn,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "৫ সেকেন্ডের মধ্যে এলার্ট ও অডিও টেস্ট করুন (AlarmManager)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        if (showTestAlarmSnack) {
            item {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = EmeraldDeep,
                    border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "✓ টেস্ট এলার্ম শিডিউল হয়েছে! ৫ সেকেন্ডে অডিও সহ নোটিফিকেশন আসবে।",
                        fontSize = 11.sp,
                        color = PrimaryGreen,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }

        // Additional Sun/Night Times
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SunTimeCard(
                    title = "সূর্যোদয় (Sunrise)",
                    time = "০৫:৪৫ AM",
                    icon = Icons.Outlined.WbSunny,
                    modifier = Modifier.weight(1f)
                )
                SunTimeCard(
                    title = "তাহাজ্জুদ ও সাহরী",
                    time = "০৪:৩০ AM",
                    icon = Icons.Outlined.NightsStay,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // 5 Daily Waqt Cards with Alarm Switches
        items(prayers) { prayer ->
            PrayerWaqtCard(
                item = prayer,
                onToggleAlarm = { isChecked ->
                    HapticUtils.performLongPressHaptic(view)
                    prayers = prayers.map {
                        if (it.id == prayer.id) it.copy(isAlarmEnabled = isChecked) else it
                    }
                    val updated = prayers.find { it.id == prayer.id }
                    if (updated != null) {
                        if (isChecked) {
                            PrayerAlarmScheduler.schedulePrayerAlarm(context, updated)
                        } else {
                            PrayerAlarmScheduler.cancelPrayerAlarm(context, updated.id)
                        }
                    }
                }
            )
        }
    }
}

@Composable
private fun PrayerWaqtCard(
    item: PrayerTimeItem,
    onToggleAlarm: (Boolean) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = if (item.isNext) DarkSurfaceElevated else DarkSurface,
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.isNext) PrimaryGreen.copy(alpha = 0.6f) else DarkSurfaceBorder
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(if (item.isNext) EmeraldDeep else DarkSurfaceBorder)
                        .border(1.dp, if (item.isNext) PrimaryGreen else Color.Transparent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccessTime,
                        contentDescription = null,
                        tint = if (item.isNext) PrimaryGreen else TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = item.nameBn,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isNext) PrimaryGreen else TextWhite
                    )
                    Text(
                        text = "আজান: ${item.adhanTime} • জামাত: ${item.iqamahTime}",
                        fontSize = 12.sp,
                        color = if (item.isNext) GoldAccent else TextMuted
                    )
                }
            }

            // Alarm Switch with Tactile Feedback
            Switch(
                checked = item.isAlarmEnabled,
                onCheckedChange = onToggleAlarm,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = PrimaryGreen,
                    checkedTrackColor = EmeraldDeep,
                    uncheckedThumbColor = TextMuted,
                    uncheckedTrackColor = DarkSurfaceBorder
                )
            )
        }
    }
}

@Composable
private fun SunTimeCard(
    title: String,
    time: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = DarkSurface,
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkSurfaceBorder),
        modifier = modifier
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldAccent,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 10.sp,
                    color = TextMuted
                )
                Text(
                    text = time,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite
                )
            }
        }
    }
}
