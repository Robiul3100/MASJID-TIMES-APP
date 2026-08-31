package com.robiul.mosquetime.ui

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.robiul.mosquetime.R
import com.robiul.mosquetime.ui.components.*
import com.robiul.mosquetime.ui.screens.home.HomeViewModel
import com.robiul.mosquetime.ui.theme.*

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
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val scrollState = rememberScrollState()
    var activeDialogInfo by remember { mutableStateOf<String?>(null) }

    val liveTimeString by viewModel.liveTimeString.collectAsState()
    val dateInfo by viewModel.dateInfo.collectAsState()
    val nextPrayerCountdown by viewModel.nextPrayerCountdown.collectAsState()
    val nextPrayerName by viewModel.nextPrayerName.collectAsState()
    val prayerSchedule by viewModel.prayerSchedule.collectAsState()

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
                // 1. Central Mosque Hero Arch Dome with Digital Clock & Indicators
                PrayerHeroSection(
                    timeString = liveTimeString,
                    dayOfMonth = dateInfo.day,
                    monthOfYear = dateInfo.month,
                    yearStr = dateInfo.year,
                    activeDayIndex = dateInfo.activeDayIndex,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 2. Prayer Timetable Schedule Card
                PrayerScheduleCard(
                    scheduleItems = prayerSchedule,
                    nextPrayerCountdown = nextPrayerCountdown,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

                // 3. Two Horizontal Bottom Information Cards (Date & Next Prayer)
                BottomInfoCards(
                    bengaliDate = "১ সেপ্টেম্বর, ২০২৬",
                    englishDate = "1 September, 2026",
                    nextPrayerName = nextPrayerName,
                    nextPrayerCountdown = nextPrayerCountdown,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(6.dp))

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
                            title = stringResource(R.string.tasbih_feature),
                            subtitle = stringResource(R.string.tasbih_subtitle),
                            icon = Icons.Default.TouchApp,
                            accentColor = NeonGreenGlow,
                            onClick = onNavigateToDigitalTasbih,
                            modifier = Modifier.weight(1f)
                        )

                        // Zakat & Fitrah Card
                        QuickFeatureCard(
                            title = stringResource(R.string.zakat_feature),
                            subtitle = stringResource(R.string.zakat_subtitle),
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
                            title = stringResource(R.string.ramadan_feature),
                            subtitle = stringResource(R.string.ramadan_subtitle),
                            icon = Icons.Default.NightlightRound,
                            accentColor = CyanBlue,
                            onClick = onNavigateToRamadan,
                            modifier = Modifier.weight(1f)
                        )

                        // Janaza & Emergency Card
                        QuickFeatureCard(
                            title = stringResource(R.string.emergency_feature),
                            subtitle = stringResource(R.string.emergency_subtitle),
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
                                    Text(stringResource(R.string.ask_imam_title), color = TextWhite, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                                    Text(stringResource(R.string.ask_imam_subtitle), color = TextMuted, fontSize = 11.sp)
                                }
                            }
                            Text(stringResource(R.string.ask_imam_action), color = NeonGreenGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
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
