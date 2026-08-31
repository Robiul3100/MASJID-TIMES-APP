package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bloodtype
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NotificationImportant
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.EmergencyAlert
import com.example.data.model.JanazaNotice
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
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.RedDigital
import com.example.ui.theme.TextMuted
import androidx.compose.runtime.collectAsState
import com.example.ui.theme.TextWhite

@Composable
fun JanazaAlertsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val janazaNotices by MosqueRepository.janazaNoticesFlow.collectAsState()
    val emergencyAlerts by MosqueRepository.emergencyAlertsFlow.collectAsState()
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    var showGuideDialog by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonHeader(
            title = "জানাজা ও জরুরি ঘোষণা",
            subtitle = "মরহুমের তথ্য, জানাজার সময় ও মানবিক বিজ্ঞপ্তি",
            onBackClick = onBackClick,
            actionIcon = Icons.Default.MenuBook,
            actionDescription = "জানাজার নিয়ম",
            onActionClick = { showGuideDialog = true }
        )

        // Main Tabs
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = DarkSurface,
            contentColor = PrimaryGreen,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                    color = if (selectedTabIndex == 0) GoldAccent else RedDigital,
                    height = 2.5.dp
                )
            },
            divider = { HorizontalDivider(thickness = 0.5.dp, color = DarkSurfaceBorder) }
        ) {
            Tab(
                selected = selectedTabIndex == 0,
                onClick = { selectedTabIndex = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = if (selectedTabIndex == 0) GoldAccent else TextMuted, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "জানাজার নোটিশ",
                            color = if (selectedTabIndex == 0) TextWhite else TextMuted,
                            fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            )

            Tab(
                selected = selectedTabIndex == 1,
                onClick = { selectedTabIndex = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NotificationImportant, contentDescription = null, tint = if (selectedTabIndex == 1) RedDigital else TextMuted, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            "জরুরি মানবিক ঘোষণা",
                            color = if (selectedTabIndex == 1) TextWhite else TextMuted,
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            )
        }

        if (selectedTabIndex == 0) {
            JanazaListTab(
                notices = janazaNotices,
                onOpenGuide = { showGuideDialog = true }
            )
        } else {
            EmergencyAlertsTab(alerts = emergencyAlerts)
        }
    }

    if (showGuideDialog) {
        JanazaPrayerGuideDialog(onDismiss = { showGuideDialog = false })
    }
}

@Composable
private fun JanazaListTab(
    notices: List<JanazaNotice>,
    onOpenGuide: () -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Quick Guide Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkGreen.copy(alpha = 0.5f))
                    .border(1.dp, PrimaryGreen.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
                    .clickable { onOpenGuide() }
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = NeonGreenGlow, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text("জানাজার নামাজের নিয়ম ও তাকবীর গাইড", color = TextWhite, fontSize = 12.5.sp, fontWeight = FontWeight.Bold)
                            Text("৪ তাকবীর, সানা, দুরুদ ও জানাজার দোয়া দেখতে ট্যাপ করুন", color = TextMuted, fontSize = 10.5.sp)
                        }
                    }
                    Text("দেখুন ›", color = NeonGreenGlow, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        items(notices, key = { it.id }) { notice ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    // Header with Deceased Name & Demise Time
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = notice.deceasedNameBn,
                                color = GoldAccent,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "ইন্তেকাল: ${notice.demiseTimeBn}",
                                color = TextMuted,
                                fontSize = 11.5.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkBackground)
                                .border(0.5.dp, GoldAccent, RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(notice.deceasedAge, color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = DarkSurfaceBorder)

                    // Details
                    DetailRow("ঠিকানা:", notice.residenceBn)
                    DetailRow("জানাজার সময়:", notice.janazaTimeBn, highlightColor = NeonGreenGlow)
                    DetailRow("জানাজার স্থান:", notice.janazaLocationBn)
                    DetailRow("জানাজার ইমাম:", notice.imamNameBn)
                    DetailRow("দাফন স্থান:", notice.graveyardBn)

                    if (notice.specialMessageBn.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = notice.specialMessageBn,
                            color = CyanBlue,
                            fontSize = 11.5.sp,
                            lineHeight = 16.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Actions: Call Family & Share Notice
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Call Family
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurface)
                                .border(1.dp, PrimaryGreen, RoundedCornerShape(8.dp))
                                .clickable {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${notice.contactFamilyPhone}"))
                                    context.startActivity(intent)
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Call, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("পরিবারে যোগাযোগ", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Share
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkGreen)
                                .border(1.dp, PrimaryGreen, RoundedCornerShape(8.dp))
                                .clickable {
                                    val shareText = "إِنَّا لِلَّهِ وَإِنَّا إِلَيْهِ رَاجِعُونَ\n\nজানাজার নোটিশ:\nমরহুম: ${notice.deceasedNameBn}\nজানাজার সময়: ${notice.janazaTimeBn}\nস্থান: ${notice.janazaLocationBn}\nদাফন: ${notice.graveyardBn}\n\n- বায়তুল আমান জামে মসজিদ"
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "জানাজার নোটিশ শেয়ার করুন"))
                                }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("নোটিশ শেয়ার করুন", color = DarkBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun EmergencyAlertsTab(alerts: List<EmergencyAlert>) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(alerts, key = { it.id }) { alert ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, if (alert.urgencyLevel == "HIGH") RedDigital else GoldAccent, RoundedCornerShape(14.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (alert.categoryBn.contains("রক্ত")) Icons.Default.Bloodtype else Icons.Default.Warning,
                                contentDescription = null,
                                tint = if (alert.urgencyLevel == "HIGH") RedDigital else GoldAccent,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = alert.categoryBn,
                                color = if (alert.urgencyLevel == "HIGH") RedDigital else GoldAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(alert.dateBn, color = TextMuted, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = alert.titleBn,
                        color = TextWhite,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = alert.descriptionBn,
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Contact Bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurface)
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text("যোগাযোগ: ${alert.contactPerson}", color = TextWhite, fontSize = 11.5.sp)
                            Text(alert.contactPhone, color = CyanBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        IconButton(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${alert.contactPhone}"))
                                context.startActivity(intent)
                            },
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(PrimaryGreen)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call", tint = DarkBackground, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String, highlightColor: androidx.compose.ui.graphics.Color = TextWhite) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 11.5.sp,
            modifier = Modifier.width(90.dp)
        )
        Text(
            text = value,
            color = highlightColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun JanazaPrayerGuideDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("জানাজার নামাজের নিয়ম ও তাকবীর গাইড", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(360.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "জানাজার নামাজ ফরযে কিফায়াহ। এতে রুকু ও সিজদা নেই, দাঁড়িয়ে ৪টি তাকবীরের সাথে আদায় করতে হয়।",
                    color = TextWhite,
                    fontSize = 12.sp
                )

                HorizontalDivider(thickness = 0.5.dp, color = DarkSurfaceBorder)

                Text("১ম তাকবীর:", color = CyanBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("তাকবীরে তাহরীমার পর সানা (সুবহানাকাল্লাহুম্মা...) পাঠ করা।", color = TextMuted, fontSize = 11.5.sp)

                Text("২য় তাকবীর:", color = CyanBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("হাত না উঠিয়ে দ্বিতীয় তাকবীর বলে দুরুদে ইব্রাহীম পাঠ করা।", color = TextMuted, fontSize = 11.5.sp)

                Text("৩য় তাকবীর:", color = CyanBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("তৃতীয় তাকবীর বলে মৃত ব্যক্তির জন্য মাগফিরাতের দোয়া পাঠ করা:\n\nاللَّهُمَّ اغْفِرْ لِحَيِّنَا وَمَيِّتِنَا وَشَاهِدِنَا وَغَائِبِنَا وَصَغِيرِنَا وَكَبِيرِنَا وَذَكَرِنَا وَأُنْثَانَا...", color = TextWhite, fontSize = 11.5.sp)

                Text("৪র্থ তাকবীর:", color = CyanBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text("চতুর্থ তাকবীর বলে ডানে ও বামে সালাম ফিরিয়ে নামাজ সম্পন্ন করা।", color = TextMuted, fontSize = 11.5.sp)
            }
        },
        containerColor = DarkSurfaceElevated,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("ঠিক আছে", color = PrimaryGreen, fontWeight = FontWeight.Bold)
            }
        }
    )
}
