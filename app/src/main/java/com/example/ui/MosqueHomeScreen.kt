package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.BottomInfoCards
import com.example.ui.components.MosqueHeader
import com.example.ui.components.PrayerHeroSection
import com.example.ui.components.PrayerScheduleCard
import com.example.ui.components.PrayerScheduleItem
import com.example.ui.components.PrayerType
import com.example.ui.components.ShortcutCardsRow
import com.example.ui.components.ShortcutType
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.QuestionAnswer
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.ui.theme.AppRadius
import com.example.ui.theme.AppSpacing
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
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun MosqueHomeScreen(
    onOpenDrawer: () -> Unit = {},
    onNavigateToNotification: () -> Unit = {},
    onNavigateToDailyPrayer: () -> Unit = {},
    onNavigateToMonthlySchedule: () -> Unit = {},
    onNavigateToIslamicCalendar: () -> Unit = {},
    onNavigateToQibla: () -> Unit = {},
    onNavigateToQuran: () -> Unit = {},
    onNavigateToDua: () -> Unit = {},
    onNavigateToAboutMosque: () -> Unit = {},
    onNavigateToDonation: () -> Unit = {},
    onNavigateToSettings: () -> Unit = {},
    onNavigateToNoticeBoard: () -> Unit = {},
    onNavigateToDigitalTasbih: () -> Unit = {},
    onNavigateToZakat: () -> Unit = {},
    onNavigateToRamadan: () -> Unit = {},
    onNavigateToJanaza: () -> Unit = {},
    onNavigateToAskImam: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var activeDialogInfo by remember { mutableStateOf<String?>(null) }

    // Live Clock & Countdown State
    var liveTimeString by remember { mutableStateOf("18:88:88") }
    var dayOfMonth by remember { mutableStateOf("88") }
    var monthOfYear by remember { mutableStateOf("88") }
    var yearStr by remember { mutableStateOf("88") }
    var activeDayIndex by remember { mutableIntStateOf(0) } // Saturday = 0
    var nextPrayerCountdown by remember { mutableStateOf("02:34:56") }

    // Real-time ticking ticker
    LaunchedEffect(Unit) {
        while (true) {
            val cal = Calendar.getInstance()
            val timeFormat = SimpleDateFormat("HH:mm:ss", Locale.US)
            val currentTime = timeFormat.format(cal.time)
            
            liveTimeString = currentTime
            
            val day = String.format(Locale.US, "%02d", cal.get(Calendar.DAY_OF_MONTH))
            val month = String.format(Locale.US, "%02d", cal.get(Calendar.MONTH) + 1)
            val year = String.format(Locale.US, "%02d", cal.get(Calendar.YEAR) % 100)

            dayOfMonth = day
            monthOfYear = month
            yearStr = year

            val calDay = cal.get(Calendar.DAY_OF_WEEK)
            activeDayIndex = when (calDay) {
                Calendar.SATURDAY -> 0
                Calendar.SUNDAY -> 1
                Calendar.MONDAY -> 2
                Calendar.TUESDAY -> 3
                Calendar.WEDNESDAY -> 4
                Calendar.THURSDAY -> 5
                Calendar.FRIDAY -> 6
                else -> 0
            }

            val totalSecs = (3600 * 2 + 34 * 60 + 56 - (cal.get(Calendar.SECOND))) % 86400
            val hrs = (totalSecs / 3600).coerceAtLeast(0)
            val mins = ((totalSecs % 3600) / 60).coerceAtLeast(0)
            val secs = (totalSecs % 60).coerceAtLeast(0)
            nextPrayerCountdown = String.format(Locale.US, "%02d:%02d:%02d", hrs, mins, secs)

            delay(1000L)
        }
    }

    // Default Prayer Schedule Items matching visual design
    val prayerSchedule = remember {
        listOf(
            PrayerScheduleItem(PrayerType.FAJR, "ফজর", "الفجر", "8:88"),
            PrayerScheduleItem(PrayerType.DHUHR, "যোহর", "الظهر", "8:88"),
            PrayerScheduleItem(PrayerType.ASR, "আসর", "العصر", "8:88"),
            PrayerScheduleItem(PrayerType.MAGHRIB, "মাগরিব", "المغرب", "8:88"),
            PrayerScheduleItem(PrayerType.ISHA, "এশা", "العشاء", "8:88"),
            PrayerScheduleItem(PrayerType.JUMAH, "জুম'আ", "الجمعة", "8:88", isJumah = true, isActive = true),
            PrayerScheduleItem(PrayerType.SUNRISE_SEHRI, "সূর্যোদয়/সেহরি", "الشروق", "8:88"),
            PrayerScheduleItem(PrayerType.SUNSET_IFTAR, "সূর্যাস্ত/ইফতার", "الغروب", "8:88")
        )
    }

    Surface(
        modifier = modifier
            .fillMaxSize()
            .testTag("mosque_home_screen"),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Screen Header
            MosqueHeader(
                onMenuClick = onOpenDrawer,
                onNotificationClick = onNavigateToNotification
            )

            // Scrollable Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Spacer(modifier = Modifier.height(4.dp))

                // 1. Central Mosque Hero Arch Dome with Digital Clock & Indicators
                PrayerHeroSection(
                    timeString = liveTimeString,
                    dayOfMonth = dayOfMonth,
                    monthOfYear = monthOfYear,
                    yearStr = yearStr,
                    activeDayIndex = activeDayIndex,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 2. Prayer Timetable Schedule Card
                PrayerScheduleCard(
                    scheduleItems = prayerSchedule,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 3. Two Horizontal Bottom Information Cards (Date & Next Prayer)
                BottomInfoCards(
                    bengaliDate = "২৫ বৈশাখ, ১৪৩১",
                    englishDate = "8 May, 2025",
                    nextPrayerName = "ফজর",
                    nextPrayerCountdown = nextPrayerCountdown,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 4. Four Feature Shortcut Cards
                ShortcutCardsRow(
                    onShortcutClick = { shortcut ->
                        when (shortcut) {
                            ShortcutType.TODAY_SCHEDULE -> onNavigateToDailyPrayer()
                            ShortcutType.MONTHLY_SCHEDULE -> onNavigateToMonthlySchedule()
                            ShortcutType.ARABIC_CALENDAR -> onNavigateToIslamicCalendar()
                            ShortcutType.QIBLA_DIRECTION -> onNavigateToQibla()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 5. Special Features & Welfare Grid
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Digital Tasbih Card
                        QuickFeatureCard(
                            title = "ডিজিটাল তসবিহ",
                            subtitle = "কাউন্টার ও যিকির ট্র্যাকার",
                            icon = Icons.Default.TouchApp,
                            accentColor = NeonGreenGlow,
                            onClick = onNavigateToDigitalTasbih,
                            modifier = Modifier.weight(1f)
                        )

                        // Zakat & Fitrah Card
                        QuickFeatureCard(
                            title = "জাকাত ও ফিতরা",
                            subtitle = "নিসাব ভিত্তিক ক্যালকুলেটর",
                            icon = Icons.Default.VolunteerActivism,
                            accentColor = GoldAccent,
                            onClick = onNavigateToZakat,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Ramadan Card
                        QuickFeatureCard(
                            title = "রমজান স্পেশাল",
                            subtitle = "সেহরি-ইফতার কাউন্টডাউন",
                            icon = Icons.Default.NightlightRound,
                            accentColor = CyanBlue,
                            onClick = onNavigateToRamadan,
                            modifier = Modifier.weight(1f)
                        )

                        // Janaza & Emergency Card
                        QuickFeatureCard(
                            title = "জানাজা ও রক্তদান",
                            subtitle = "জরুরি ঘোষণা ও নোটিশ",
                            icon = Icons.Default.Warning,
                            accentColor = RedDigital,
                            onClick = onNavigateToJanaza,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Ask Imam Wide Banner
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.horizontalGradient(
                                    listOf(DarkSurfaceElevated, DarkGreen.copy(alpha = 0.5f))
                                )
                            )
                            .border(1.dp, PrimaryGreen.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                            .clickable { onNavigateToAskImam() }
                            .padding(horizontal = 14.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(38.dp)
                                        .clip(CircleShape)
                                        .background(DarkGreen),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.QuestionAnswer, contentDescription = null, tint = NeonGreenGlow, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text("ইমাম সাহেবকে সরাসরি প্রশ্ন করুন", color = TextWhite, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                                    Text("যেকোনো শরয়ী জটিলতা ও ফতোয়া জিজ্ঞাসা", color = TextMuted, fontSize = 11.sp)
                                }
                            }
                            Text("জিজ্ঞাসা ›", color = NeonGreenGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(AppSpacing.lg))
            }
        }
    }

    // Modal Dialog for interactions
    activeDialogInfo?.let { message ->
        AlertDialog(
            onDismissRequest = { activeDialogInfo = null },
            title = { Text(text = "বিজ্ঞপ্তি", color = PrimaryGreen, fontWeight = FontWeight.Bold) },
            text = { Text(text = message, color = TextWhite) },
            confirmButton = {
                Button(
                    onClick = { activeDialogInfo = null },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("ঠিক আছে", color = DarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(AppRadius.md)
        )
    }
}

@Composable
private fun QuickFeatureCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(84.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurfaceElevated)
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.6f),
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(10.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column {
                Text(
                    text = title,
                    color = TextWhite,
                    fontSize = 12.5.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 10.sp,
                    maxLines = 1
                )
            }
        }
    }
}

