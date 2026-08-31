package com.robiul.mosquetime.ui.screens

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.data.model.MonthlyPrayerDay
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.data.repository.UserPreferencesRepository
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.theme.CyanBlue
import com.robiul.mosquetime.ui.theme.DarkBackground
import com.robiul.mosquetime.ui.theme.DarkGreen
import com.robiul.mosquetime.ui.theme.DarkGreenBorder
import com.robiul.mosquetime.ui.theme.DarkSurface
import com.robiul.mosquetime.ui.theme.DarkSurfaceBorder
import com.robiul.mosquetime.ui.theme.DarkSurfaceElevated
import com.robiul.mosquetime.ui.theme.GoldAccent
import com.robiul.mosquetime.ui.theme.NeonGreenGlow
import com.robiul.mosquetime.ui.theme.PrimaryGreen
import com.robiul.mosquetime.ui.theme.PurpleAccent
import com.robiul.mosquetime.ui.theme.RedDigital
import com.robiul.mosquetime.ui.theme.TextMuted
import com.robiul.mosquetime.ui.theme.TextWhite

@Composable
fun MonthlyScheduleScreen(
    onBackClick: () -> Unit,
    onNavigateToDistrictSettings: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val settings by UserPreferencesRepository.settings.collectAsState()
    val currentDistrict = MosqueRepository.getDistrictById(settings.selectedDistrictId)

    var currentYear by remember { mutableIntStateOf(2025) }
    var currentMonth by remember { mutableIntStateOf(5) } // 5 = May

    val monthNamesBn = arrayOf("জানুয়ারি", "ফেব্রুয়ারি", "মার্চ", "এপ্রিল", "মে", "জুন", "জুলাই", "আগস্ট", "সেপ্টেম্বর", "অক্টোবর", "নভেম্বর", "ডিসেম্বর")
    val bengaliMonthNames = arrayOf("পৌষ-মাঘ", "মাঘ-ফাল্গুন", "ফাল্গুন-চৈত্র", "চৈত্র-বৈশাখ", "বৈশাখ-জ্যৈষ্ঠ", "জ্যৈষ্ঠ-আষাঢ়", "আষাঢ়-শ্রাবণ", "শ্রাবণ-ভাদ্র", "ভাদ্র-আশ্বিন", "আশ্বিন-কার্তিক", "কার্তিক-অগ্রহায়ণ", "অগ্রহায়ণ-পৌষ")
    val hijriMonthNames = arrayOf("রজব", "শাবান", "রমজান", "শাওয়াল", "জিলক্বদ", "জিলহজ", "মহররম", "সফর", "রবিউল আউয়াল", "রবিউস সানি", "জমাদিউল আউয়াল", "জমাদিউস সানি")

    val daysList = remember(currentYear, currentMonth, settings.selectedDistrictId) {
        MosqueRepository.generateMonthlySchedule(currentYear, currentMonth, settings.selectedDistrictId)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonHeader(
            title = "মাসিক নামাজের ক্যালেন্ডার",
            subtitle = "${monthNamesBn[currentMonth - 1]} $currentYear • জেলা: ${currentDistrict.nameBn}",
            onBackClick = onBackClick
        )

        // Month Selector Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .border(1.dp, PurpleAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        if (currentMonth > 1) {
                            currentMonth -= 1
                        } else {
                            currentMonth = 12
                            currentYear -= 1
                        }
                    },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DarkBackground)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Previous Month",
                        tint = PurpleAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "${monthNamesBn[currentMonth - 1]} $currentYear",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "বাংলা: ${bengaliMonthNames[currentMonth - 1]} • হিজরি: ${hijriMonthNames[currentMonth - 1]}",
                        color = GoldAccent,
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = {
                        if (currentMonth < 12) {
                            currentMonth += 1
                        } else {
                            currentMonth = 1
                            currentYear += 1
                        }
                    },
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(DarkBackground)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Next Month",
                        tint = PurpleAccent,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Table Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(DarkSurfaceElevated)
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "তারিখ/বার",
                    color = CyanBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1.3f)
                )
                Text(
                    text = "ফজর",
                    color = CyanBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "যোহর",
                    color = CyanBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "আসর",
                    color = CyanBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "মাগরিব",
                    color = CyanBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = "এশা",
                    color = CyanBlue,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Days List
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(daysList, key = { it.dayNumber }) { day ->
                MonthlyDayRow(day = day)
            }

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun MonthlyDayRow(day: MonthlyPrayerDay) {
    val rowBackground = when {
        day.isToday -> DarkGreen.copy(alpha = 0.5f)
        day.isFriday -> RedDigital.copy(alpha = 0.10f)
        day.dayNumber % 2 == 0 -> DarkSurface
        else -> DarkSurface.copy(alpha = 0.6f)
    }

    val borderColor = when {
        day.isToday -> PrimaryGreen
        day.isFriday -> RedDigital.copy(alpha = 0.4f)
        else -> DarkSurfaceBorder
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(rowBackground)
            .border(1.dp, borderColor, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 7.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Date & Day
            Column(modifier = Modifier.weight(1.3f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${day.dayNumber}",
                        color = if (day.isToday) NeonGreenGlow else if (day.isFriday) RedDigital else TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "(${day.dayName})",
                        color = if (day.isFriday) RedDigital else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = if (day.isFriday) FontWeight.Bold else FontWeight.Normal
                    )
                }
                Text(
                    text = "${day.bengaliDate}",
                    color = GoldAccent,
                    fontSize = 9.5.sp
                )
            }

            // Fajr
            Text(
                text = day.fajrAzan,
                color = if (day.isToday) NeonGreenGlow else TextWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            // Dhuhr
            Text(
                text = day.dhuhrAzan,
                color = if (day.isFriday) RedDigital else if (day.isToday) NeonGreenGlow else TextWhite,
                fontSize = 11.sp,
                fontWeight = if (day.isFriday) FontWeight.Bold else FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            // Asr
            Text(
                text = day.asrAzan,
                color = if (day.isToday) NeonGreenGlow else TextWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            // Maghrib
            Text(
                text = day.maghribAzan,
                color = if (day.isToday) NeonGreenGlow else TextWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )

            // Isha
            Text(
                text = day.ishaAzan,
                color = if (day.isToday) NeonGreenGlow else TextWhite,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = FontFamily.Monospace,
                textAlign = TextAlign.Center,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
