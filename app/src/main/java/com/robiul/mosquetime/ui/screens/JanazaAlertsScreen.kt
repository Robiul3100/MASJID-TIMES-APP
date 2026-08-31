package com.robiul.mosquetime.ui.screens

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.outlined.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.data.model.EmergencyAlert
import com.robiul.mosquetime.data.model.JanazaNotice
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.theme.*

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

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            CommonHeader(
                title = "জানাজা ও জরুরি ঘোষণা",
                subtitle = "মরহুমের তথ্য, জানাজার সময় ও মানবিক বিজ্ঞপ্তি",
                onBackClick = onBackClick,
                actionIcon = Icons.AutoMirrored.Filled.MenuBook,
                actionDescription = "জানাজার নিয়ম ও দোয়া",
                onActionClick = { showGuideDialog = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Main Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = DarkSurfaceElevated,
                contentColor = PrimaryGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = if (selectedTabIndex == 0) GoldAccent else RedDigital,
                        height = 3.dp
                    )
                },
                divider = { HorizontalDivider(thickness = 0.5.dp, color = DarkGreenBorder.copy(alpha = 0.5f)) }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = if (selectedTabIndex == 0) GoldAccent else TextMuted, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "জানাজার নোটিশ (${janazaNotices.size})",
                                color = if (selectedTabIndex == 0) TextWhite else TextMuted,
                                fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.5.sp,
                                fontFamily = SolaimanLipiFontFamily
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
                                fontSize = 13.5.sp,
                                fontFamily = SolaimanLipiFontFamily
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
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Quick Guide Banner
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkGreen.copy(alpha = 0.6f))
                    .border(1.dp, PrimaryGreen.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                    .clickable { onOpenGuide() }
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = NeonGreenGlow, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("জানাজার নামাজের নিয়ম ও তাকবীর গাইড", color = TextWhite, fontSize = 13.5.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                            Text("৪ তাকবীর, সানা, দুরুদ ও দোয়া দেখতে ট্যাপ করুন", color = TextMuted, fontSize = 11.sp, fontFamily = SolaimanLipiFontFamily)
                        }
                    }
                    Text("দেখুন ›", color = NeonGreenGlow, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                }
            }
        }

        // Janaza Reward Hadith Card
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurfaceElevated)
                    .border(0.8.dp, GoldAccent.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Text("💡", fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "জানাজার সাওয়াব ও মর্যাদা:",
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SolaimanLipiFontFamily
                        )
                        Text(
                            text = "রাসূলুল্লাহ (ﷺ) বলেছেন: \"যে ব্যক্তি কোনো জানাজায় অংশ নিয়ে নামাজ শেষ হওয়া পর্যন্ত থাকে, সে এক 'কিরাত' সাওয়াব লাভ করবে। আর যে ব্যক্তি দাফন সম্পন্ন হওয়া পর্যন্ত থাকে, সে দুই 'কিরাত' সাওয়াব পাবে।\" সাহাবীগণ জিজ্ঞেস করলেন, দুই কিরাত কী? তিনি বললেন: \"দুটি বিশাল পাহাড় (ওহুদ পাহাড়) সমপরিমাণ সাওয়াব।\" (সহিহ বুখারী: ১৩২৫)",
                            color = TextWhite.copy(alpha = 0.85f),
                            fontSize = 11.5.sp,
                            lineHeight = 16.5.sp,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                }
            }
        }

        if (notices.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceElevated)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("🕊️", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("বর্তমানে কোনো জানাজার নোটিশ নেই", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                        Text("নতুন জানাজার সংবাদ পাওয়া মাত্রই সাথে সাথে আপডেট করা হবে।", color = TextMuted, fontSize = 12.sp, fontFamily = SolaimanLipiFontFamily)
                    }
                }
            }
        } else {
            items(notices, key = { it.id }) { notice ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, GoldAccent.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                        .padding(16.dp)
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
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "ইন্তেকাল: ${notice.demiseTimeBn}",
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurface)
                                    .border(0.8.dp, GoldAccent, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(notice.deceasedAge, color = GoldAccent, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp, color = DarkGreenBorder.copy(alpha = 0.4f))

                        // Details
                        DetailRow("ঠিকানা:", notice.residenceBn)
                        DetailRow("জানাজার সময়:", notice.janazaTimeBn, highlightColor = NeonGreenGlow)
                        DetailRow("জানাজার স্থান:", notice.janazaLocationBn)
                        DetailRow("জানাজার ইমাম:", notice.imamNameBn)
                        DetailRow("দাফন স্থান:", notice.graveyardBn)

                        if (notice.specialMessageBn.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "জরুরি বার্তা: ${notice.specialMessageBn}",
                                color = CyanBlue,
                                fontSize = 12.sp,
                                lineHeight = 17.sp,
                                fontFamily = SolaimanLipiFontFamily
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Actions: Call Family, Maps & Share Notice
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Call Family
                            Button(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${notice.contactFamilyPhone}"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("পরিবার", color = PrimaryGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                            }

                            // Google Maps Direction
                            Button(
                                onClick = {
                                    val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=${Uri.encode(notice.janazaLocationBn)}"))
                                    context.startActivity(mapIntent)
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkSurface),
                                border = androidx.compose.foundation.BorderStroke(1.dp, CyanBlue),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ম্যাপস", color = CyanBlue, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                            }

                            // Share Notice
                            Button(
                                onClick = {
                                    val shareText = "إِنَّا لِلَّهِ وَإِنَّا إِلَيْهِ رَاجِعُونَ\n\nজানাজার নোটিশ:\nমরহুম: ${notice.deceasedNameBn}\nজানাজার সময়: ${notice.janazaTimeBn}\nস্থান: ${notice.janazaLocationBn}\nদাফন: ${notice.graveyardBn}\nপরিবারের ফোন: ${notice.contactFamilyPhone}\n\n- বায়তুল আমান জামে মসজিদ"
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, shareText)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "জানাজার নোটিশ শেয়ার করুন"))
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = DarkGreen),
                                border = androidx.compose.foundation.BorderStroke(1.dp, PrimaryGreen),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(vertical = 8.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("শেয়ার", color = DarkBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
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
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        if (alerts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurfaceElevated)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("📢", fontSize = 36.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("বর্তমানে কোনো জরুরি মানবিক ঘোষণা নেই", color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                        Text("রক্তদান বা জরুরি সহায়তার ঘোষণা আসলে এখানে দেখতে পাবেন।", color = TextMuted, fontSize = 12.sp, fontFamily = SolaimanLipiFontFamily)
                    }
                }
            }
        } else {
            items(alerts, key = { it.id }) { alert ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.5.dp, if (alert.urgencyLevel == "HIGH") RedDigital else GoldAccent, RoundedCornerShape(16.dp))
                        .padding(16.dp)
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
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = alert.categoryBn,
                                    color = if (alert.urgencyLevel == "HIGH") RedDigital else GoldAccent,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }

                            Text(alert.dateBn, color = TextMuted, fontSize = 11.5.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = alert.titleBn,
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SolaimanLipiFontFamily
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = alert.descriptionBn,
                            color = TextWhite.copy(alpha = 0.85f),
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp,
                            fontFamily = SolaimanLipiFontFamily
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Contact Bar with 1-tap dial
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurface)
                                .border(0.6.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("যোগাযোগ: ${alert.contactPerson}", color = TextWhite, fontSize = 12.sp, fontFamily = SolaimanLipiFontFamily)
                                Text(alert.contactPhone, color = CyanBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                            }

                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${alert.contactPhone}"))
                                    context.startActivity(intent)
                                },
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryGreen)
                            ) {
                                Icon(Icons.Default.Call, contentDescription = "Call", tint = DarkBackground, modifier = Modifier.size(18.dp))
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
private fun DetailRow(label: String, value: String, highlightColor: androidx.compose.ui.graphics.Color = TextWhite) {
    Row(modifier = Modifier.padding(vertical = 3.dp)) {
        Text(
            text = label,
            color = TextMuted,
            fontSize = 12.sp,
            fontFamily = SolaimanLipiFontFamily,
            modifier = Modifier.width(90.dp)
        )
        Text(
            text = value,
            color = highlightColor,
            fontSize = 12.5.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = SolaimanLipiFontFamily
        )
    }
}

@Composable
private fun JanazaPrayerGuideDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.MenuBook, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("জানাজার নিয়ম ও বিশুদ্ধ দোয়া", color = GoldAccent, fontWeight = FontWeight.Bold, fontSize = 16.sp, fontFamily = SolaimanLipiFontFamily)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "জানাজার নামাজ ফরযে কিফায়াহ। এতে রুকু ও সিজদা নেই, দাঁড়িয়ে ৪টি তাকবীরের সাথে আদায় করতে হয়।",
                    color = TextWhite,
                    fontSize = 12.5.sp,
                    fontFamily = SolaimanLipiFontFamily
                )

                HorizontalDivider(thickness = 0.5.dp, color = DarkGreenBorder.copy(alpha = 0.5f))

                Text("১ম তাকবীর:", color = CyanBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                Text("তাকবীরে তাহরীমার পর হাত বেঁধে সানা (সুবহানাকাল্লাহুম্মা...) পাঠ করা।", color = TextMuted, fontSize = 12.sp, fontFamily = SolaimanLipiFontFamily)

                Text("২য় তাকবীর:", color = CyanBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                Text("হাত না উঠিয়ে তাকবীর বলে দুরুদে ইব্রাহীম (নামাজে পঠিত দুরুদ) পাঠ করা।", color = TextMuted, fontSize = 12.sp, fontFamily = SolaimanLipiFontFamily)

                Text("৩য় তাকবীর (প্রাপ্তবয়স্কদের দোয়া):", color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                Text(
                    text = "اللَّهُمَّ اغْفِرْ لِحَيِّنَا وَمَيِّتِنَا وَشَاهِدِنَا وَغَائِبِنَا وَصَغِيرِنَا وَكَبِيرِنَا وَذَكَرِنَا وَأُنْثَانَا ۖ اللَّهُمَّ مَنْ أَحْيَيْتَهُ مِنَّا فَأَحْيِهِ عَلَى الْإِسْلَامِ وَمَنْ تَوَفَّيْتَهُ مِنَّا فَتَوَفَّهُ عَلَى الْإِيمَانِ",
                    color = TextWhite,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
                Text(
                    text = "উচ্চারণ: 'আল্লাহুম্মাগফির লি-হাইয়্যিনা ওয়া মাইয়্যিতিনা, ওয়া শাহিদিনা ওয়া গা-ইবিনা, ওয়া সাগীরিনা ওয়া কাবীরিনা, ওয়া যাকারিনা ওয়া উনছানা। আল্লাহুম্মা মান আহ্ইয়াইতাহু মিন্না ফা-আহ্য়িহি ‘আলাল ইসলাম, ওয়া মান তাওয়াফ্ফাইতাহু মিন্না ফাতাওয়াফফাহু ‘আলাল ঈমান।'",
                    color = TextMuted,
                    fontSize = 11.5.sp,
                    fontFamily = SolaimanLipiFontFamily
                )

                Text("অপ্রাপ্তবয়স্ক (নাবালেগ শিশু) এর দোয়া:", color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                Text(
                    text = "اللَّهُمَّ اجْعَلْهُ لَنَا فَرَطًا وَاجْعَلْهُ لَنَا أَجْرًا وَذُخْرًا وَاجْعَلْهُ لَنَا شَافِعًا وَمُشَفَّعًا",
                    color = TextWhite,
                    fontSize = 13.sp,
                    lineHeight = 20.sp
                )
                Text(
                    text = "উচ্চারণ: 'আল্লাহুম্মাজ‘আলহু লানা ফারাতান, ওয়াজ‘আলহু লানা আজরান ওয়া যুখরান, ওয়াজ‘আলহু লানা শাফি‘আন ওয়া মুশাফ্ফা‘আ।'",
                    color = TextMuted,
                    fontSize = 11.5.sp,
                    fontFamily = SolaimanLipiFontFamily
                )

                Text("৪র্থ তাকবীর:", color = CyanBlue, fontSize = 13.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                Text("চতুর্থ তাকবীর বলে ডানে ও বামে সালাম ফিরিয়ে নামাজ সম্পন্ন করা।", color = TextMuted, fontSize = 12.sp, fontFamily = SolaimanLipiFontFamily)
            }
        },
        containerColor = DarkSurfaceElevated,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("ঠিক আছে", color = PrimaryGreen, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
            }
        }
    )
}
