package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Timer
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.MosqueRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.ui.components.CommonHeader
import com.example.ui.components.PrayerIcon
import com.example.ui.components.PrayerType
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
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.RedDigital
import com.example.ui.theme.TextDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun DailyPrayerScreen(
    onBackClick: () -> Unit,
    onNavigateToMonthly: () -> Unit = {},
    onNavigateToDistrictSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val settings by UserPreferencesRepository.settings.collectAsState()
    val currentDistrict = MosqueRepository.getDistrictById(settings.selectedDistrictId)

    var isDistrictDropdownOpen by remember { mutableStateOf(false) }
    var countdownString by remember { mutableStateOf("02:14:35") }
    var nextPrayerName by remember { mutableStateOf("মাগরিব") }

    val todayPrayers = remember(settings.selectedDistrictId) {
        MosqueRepository.calculateTodayPrayers(settings.selectedDistrictId)
    }

    val extraPrayers = remember(settings.selectedDistrictId) {
        MosqueRepository.getExtraPrayerTimes(settings.selectedDistrictId)
    }

    // Real-time ticking for countdown
    LaunchedEffect(Unit) {
        while (true) {
            val cal = Calendar.getInstance()
            val secs = 59 - cal.get(Calendar.SECOND)
            val mins = (44 - cal.get(Calendar.MINUTE) + 60) % 60
            val hrs = (18 - cal.get(Calendar.HOUR_OF_DAY) + 24) % 24
            countdownString = String.format(Locale.US, "%02d:%02d:%02d", hrs % 3, mins, secs)
            delay(1000L)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonHeader(
            title = "দৈনিক নামাজের সময়সূচি",
            subtitle = "আজকের ওয়াক্ত ও জামাতের পূর্ণাঙ্গ তালিকা",
            onBackClick = onBackClick
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            // District Selector Header Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkGreenBorder.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                    .clickable { isDistrictDropdownOpen = true }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = PrimaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "জেলা: ${currentDistrict.nameBn}",
                                color = TextWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (currentDistrict.fajrOffsetMinutes == 0) "মূল সময় (ঢাকা মানদণ্ড)"
                                else "ঢাকাত চেয়ে ${if (currentDistrict.fajrOffsetMinutes > 0) "+" else ""}${currentDistrict.fajrOffsetMinutes} মিনিট পার্থক্য",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkBackground)
                            .border(1.dp, CyanBlue.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "জেলা পরিবর্তন",
                            color = CyanBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                DropdownMenu(
                    expanded = isDistrictDropdownOpen,
                    onDismissRequest = { isDistrictDropdownOpen = false },
                    modifier = Modifier.background(DarkSurfaceElevated)
                ) {
                    MosqueRepository.districts.forEach { dist ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = dist.nameBn,
                                    color = if (dist.id == currentDistrict.id) PrimaryGreen else TextWhite
                                )
                            },
                            onClick = {
                                UserPreferencesRepository.updateSettings(settings.copy(selectedDistrictId = dist.id))
                                isDistrictDropdownOpen = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Countdown to Next Prayer Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(DarkGreen.copy(alpha = 0.6f), DarkSurface)
                        )
                    )
                    .border(1.dp, PrimaryGreen.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Timer,
                                contentDescription = null,
                                tint = CyanBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "পরবর্তী নামাজ: $nextPrayerName",
                                color = CyanBlue,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "আজানের পর দ্রুত মসজিদে উপস্থিত হোন",
                            color = TextMuted,
                            fontSize = 11.5.sp
                        )
                    }

                    Text(
                        text = countdownString,
                        color = NeonGreenGlow,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Prayer Schedule Header
            Text(
                text = "পাঁচ ওয়াক্ত নামাজের সময়সূচি ও জামাত",
                color = PrimaryGreen,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )

            // Prayer Time Table Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkGreenBorder.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(vertical = 6.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Table Header
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ওয়াক্ত",
                            color = CyanBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1.5f)
                        )
                        Text(
                            text = "আজান",
                            color = CyanBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "জামাত",
                            color = CyanBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "অবস্থা",
                            color = CyanBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    HorizontalDivider(thickness = 0.8.dp, color = DarkSurfaceBorder)

                    todayPrayers.forEachIndexed { index, prayer ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(if (prayer.isActive) DarkGreen.copy(alpha = 0.4f) else Color.Transparent)
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Prayer Icon + Name
                            Row(
                                modifier = Modifier.weight(1.5f),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                PrayerIcon(
                                    type = prayer.type,
                                    isJumah = prayer.isJumah,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = prayer.bengaliName,
                                        color = if (prayer.isJumah) RedDigital else TextWhite,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = prayer.arabicName,
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            // Azan Time
                            Text(
                                text = prayer.azanTime,
                                color = if (prayer.isActive) NeonGreenGlow else TextWhite,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(1f)
                            )

                            // Iqamah Time
                            Text(
                                text = prayer.iqamahTime,
                                color = if (prayer.isJumah) RedDigital else PrimaryGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.weight(1f)
                            )

                            // Status Badge
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(
                                        if (prayer.isActive) NeonGreenGlow.copy(alpha = 0.2f)
                                        else if (prayer.isPassed) DarkBackground
                                        else DarkSurfaceBorder
                                    )
                                    .padding(horizontal = 6.dp, vertical = 3.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (prayer.isActive) "বর্তমান" else if (prayer.isPassed) "সমাপ্ত" else "আসন্ন",
                                    color = if (prayer.isActive) NeonGreenGlow else if (prayer.isPassed) TextMuted else CyanBlue,
                                    fontSize = 10.5.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        if (index < todayPrayers.size - 1) {
                            HorizontalDivider(thickness = 0.5.dp, color = DarkSurfaceBorder.copy(alpha = 0.5f))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Extra / Nafil Prayer Timetable Card
            Text(
                text = "অন্যান্য বিশেষ ওয়াক্ত ও নফল ইবাদত",
                color = GoldAccent,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(1.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    extraPrayers.forEach { extra ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = extra.name,
                                    color = TextWhite,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = extra.description,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = extra.time,
                                color = GoldAccent,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                        HorizontalDivider(thickness = 0.4.dp, color = DarkSurfaceBorder.copy(alpha = 0.4f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Makrooh Times Alert Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(1.dp, RedDigital.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Makrooh",
                        tint = RedDigital,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "নামাজের নিষিদ্ধ (মাকরুহ) সময়সমূহ:",
                            color = RedDigital,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "১. সূর্যোদয়ের সময় (প্রায় ১৫ মিনিট)\n২. ঠিক দ্বিপ্রহরের সময় (সূর্য মাথার উপরে অবস্থানকালে)\n৩. সূর্যাস্তের পূর্ব মুহূর্ত (সূর্য হলুদ বর্ণ ধারণ থেকে ডোবা পর্যন্ত)",
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
