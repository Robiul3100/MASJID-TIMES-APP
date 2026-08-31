package com.robiul.mosquetime.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.robiul.mosquetime.data.model.District
import com.robiul.mosquetime.data.model.RamadanDay
import com.robiul.mosquetime.data.model.RamadanDua
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.data.repository.UserPreferencesRepository
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Locale

enum class AshraFilter(val titleBn: String, val rangeDesc: String) {
    ALL("পূর্ণাঙ্গ ৩০ দিন", "১-৩০ রমজান"),
    RAHMAT("১ম দশক (রহমত)", "১-১০ রমজান"),
    MAGHFIRAT("২য় দশক (মাগফিরাত)", "১১-২০ রমজান"),
    NAJAT("৩য় দশক (নাজাত)", "২১-৩০ রমজান")
}

@Composable
fun RamadanDashboardScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val appSettings by UserPreferencesRepository.settings.collectAsState()
    val completedFasts by UserPreferencesRepository.completedFasts.collectAsState()

    val selectedDistrictId = appSettings.selectedDistrictId
    val currentDistrict = MosqueRepository.districts.find { it.id == selectedDistrictId } ?: MosqueRepository.districts.first()

    val ramadanSchedule = remember(selectedDistrictId) {
        MosqueRepository.generateRamadanSchedule(selectedDistrictId)
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    var selectedAshra by remember { mutableStateOf(AshraFilter.ALL) }
    var showDistrictSearchDialog by remember { mutableStateOf(false) }

    // Real-time Countdown Timer State
    var timeRemainingString by remember { mutableStateOf("00:00:00") }
    var countdownLabel by remember { mutableStateOf("ইফতারের বাকি") }
    var todaySehriStr by remember { mutableStateOf("০৪:৩৫ AM") }
    var todayIftarStr by remember { mutableStateOf("০৬:২২ PM") }

    LaunchedEffect(selectedDistrictId) {
        while (true) {
            val now = Calendar.getInstance()
            val hour = now.get(Calendar.HOUR_OF_DAY)
            val minute = now.get(Calendar.MINUTE)
            val second = now.get(Calendar.SECOND)

            val currentSecondsOfDay = hour * 3600 + minute * 60 + second

            // Offset adjustment based on selected district
            val offsetMins = currentDistrict.fajrOffsetMinutes
            val baseSehriMins = 4 * 60 + 35 + offsetMins
            val baseIftarMins = 18 * 60 + 22 + offsetMins

            val sH = baseSehriMins / 60
            val sM = baseSehriMins % 60
            todaySehriStr = String.format(Locale.US, "%02d:%02d AM", sH, sM)

            val iftH12 = (baseIftarMins / 60) - 12
            val iftM = baseIftarMins % 60
            todayIftarStr = String.format(Locale.US, "%02d:%02d PM", iftH12, iftM)

            val sehriSeconds = baseSehriMins * 60
            val iftarSeconds = baseIftarMins * 60

            val targetSeconds: Int
            if (currentSecondsOfDay < sehriSeconds) {
                targetSeconds = sehriSeconds
                countdownLabel = "সেহরির শেষ সময় বাকি"
            } else if (currentSecondsOfDay < iftarSeconds) {
                targetSeconds = iftarSeconds
                countdownLabel = "ইফতারের সময় বাকি"
            } else {
                targetSeconds = 24 * 3600 + sehriSeconds
                countdownLabel = "আগামীকালের সেহরি বাকি"
            }

            val diff = (targetSeconds - currentSecondsOfDay).coerceAtLeast(0)
            val h = diff / 3600
            val m = (diff % 3600) / 60
            val s = diff % 60
            timeRemainingString = String.format(Locale.US, "%02d:%02d:%02d", h, m, s)

            delay(1000)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            CommonHeader(
                title = "পবিত্র মাহে রমজান ড্যাশবোর্ড",
                subtitle = "সেহরি-ইফতার কাউন্টডাউন, ক্যালেন্ডার ও আমল",
                onBackClick = onBackClick,
                actionIcon = Icons.Default.LocationOn,
                actionDescription = "জেলা পরিবর্তন",
                onActionClick = { showDistrictSearchDialog = true }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Live Countdown & Today Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(DarkGreen.copy(alpha = 0.85f), DarkSurfaceElevated)
                        )
                    )
                    .border(1.5.dp, GoldAccent.copy(alpha = 0.7f), RoundedCornerShape(18.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkBackground.copy(alpha = 0.6f))
                                .clickable { showDistrictSearchDialog = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${currentDistrict.nameBn} জেলা",
                                color = GoldAccent,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = SolaimanLipiFontFamily
                            )
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkBackground)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = countdownLabel,
                                color = CyanBlue,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = SolaimanLipiFontFamily
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Big Digital Countdown
                    Text(
                        text = timeRemainingString,
                        color = NeonGreenGlow,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(thickness = 0.5.dp, color = DarkGreenBorder.copy(alpha = 0.4f))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Sehri & Iftar Times Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Timer, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("সেহরি শেষ", color = TextMuted, fontSize = 11.5.sp, fontFamily = SolaimanLipiFontFamily)
                            }
                            Text(todaySehriStr, color = CyanBlue, fontSize = 14.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }

                        Box(modifier = Modifier.width(1.dp).height(30.dp).background(DarkSurfaceBorder))

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.NightlightRound, contentDescription = null, tint = NeonGreenGlow, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("ইফতার সময়", color = TextMuted, fontSize = 11.5.sp, fontFamily = SolaimanLipiFontFamily)
                            }
                            Text(todayIftarStr, color = NeonGreenGlow, fontSize = 14.5.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
                        }
                    }
                }
            }

            // Sub Navigation Tabs
            val tabs = listOf("৩০ দিনের ক্যালেন্ডার", "রোজার দোয়া ও নিয়ত", "তারাবীহ ও আমল")
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = DarkSurface,
                contentColor = PrimaryGreen,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PrimaryGreen,
                        height = 3.dp
                    )
                },
                divider = { HorizontalDivider(thickness = 0.5.dp, color = DarkSurfaceBorder) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                color = if (selectedTab == index) TextWhite else TextMuted,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp,
                                fontFamily = SolaimanLipiFontFamily
                            )
                        }
                    )
                }
            }

            when (selectedTab) {
                0 -> RamadanCalendarTab(
                    days = ramadanSchedule,
                    completedFasts = completedFasts,
                    selectedAshra = selectedAshra,
                    onSelectAshra = { selectedAshra = it },
                    onToggleFast = { dayNum ->
                        UserPreferencesRepository.toggleFastCompleted(dayNum)
                    }
                )
                1 -> RamadanDuasTab(duas = MosqueRepository.ramadanDuas, context = context)
                2 -> TarabiAndAmalTab()
            }
        }
    }

    // Searchable District Dialog
    if (showDistrictSearchDialog) {
        SearchableDistrictDialog(
            currentDistrictId = selectedDistrictId,
            onDismiss = { showDistrictSearchDialog = false },
            onSelectDistrict = { dist ->
                UserPreferencesRepository.updateSettings(appSettings.copy(selectedDistrictId = dist.id))
                showDistrictSearchDialog = false
                Toast.makeText(context, "${dist.nameBn} জেলা অনুযায়ী সময়সূচি হালনাগাদ করা হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
    }
}

@Composable
private fun RamadanCalendarTab(
    days: List<RamadanDay>,
    completedFasts: Set<Int>,
    selectedAshra: AshraFilter,
    onSelectAshra: (AshraFilter) -> Unit,
    onToggleFast: (Int) -> Unit
) {
    val totalCompleted = completedFasts.size

    val filteredDays = remember(days, selectedAshra) {
        when (selectedAshra) {
            AshraFilter.ALL -> days
            AshraFilter.RAHMAT -> days.filter { it.ramadanDayNumber in 1..10 }
            AshraFilter.MAGHFIRAT -> days.filter { it.ramadanDayNumber in 11..20 }
            AshraFilter.NAJAT -> days.filter { it.ramadanDayNumber in 21..30 }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        // Fast Tracker Summary Card
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, PrimaryGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .padding(horizontal = 14.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreenGlow, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("রোজা ট্র্যাকার সম্পন্ন:", color = TextWhite, fontSize = 13.sp, fontFamily = SolaimanLipiFontFamily)
            }
            Text("$totalCompleted / ৩০ দিন", color = GoldAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Ashra Filter Chips
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(AshraFilter.values()) { ashra ->
                val isSelected = selectedAshra == ashra
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectAshra(ashra) },
                    label = { Text(ashra.titleBn, fontSize = 11.sp, fontFamily = SolaimanLipiFontFamily, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PrimaryGreen,
                        selectedLabelColor = DarkBackground,
                        containerColor = DarkSurfaceElevated,
                        labelColor = TextWhite
                    ),
                    border = FilterChipDefaults.filterChipBorder(enabled = true, selected = isSelected, borderColor = DarkGreenBorder, selectedBorderColor = PrimaryGreen),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(DarkGreen.copy(alpha = 0.5f))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("রমজান", color = GoldAccent, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f), fontFamily = SolaimanLipiFontFamily)
            Text("তারিখ ও বার", color = TextWhite, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f), fontFamily = SolaimanLipiFontFamily)
            Text("সেহরি শেষ", color = CyanBlue, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f), fontFamily = SolaimanLipiFontFamily)
            Text("ইফতার", color = NeonGreenGlow, fontSize = 11.5.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f), fontFamily = SolaimanLipiFontFamily)
            Text("রাখা হয়েছে", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.9f), textAlign = TextAlign.Center, fontFamily = SolaimanLipiFontFamily)
        }

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            items(filteredDays, key = { it.ramadanDayNumber }) { day ->
                val isCompleted = completedFasts.contains(day.ramadanDayNumber)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (day.isToday) DarkGreen.copy(alpha = 0.4f) else DarkSurfaceElevated)
                        .border(
                            width = if (day.isToday) 1.dp else 0.5.dp,
                            color = if (day.isToday) PrimaryGreen else DarkGreenBorder.copy(alpha = 0.4f),
                            shape = RoundedCornerShape(10.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${day.ramadanDayNumber} রমজান",
                        color = if (day.isToday) NeonGreenGlow else TextWhite,
                        fontSize = 12.5.sp,
                        fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.weight(1.2f),
                        fontFamily = SolaimanLipiFontFamily
                    )
                    Text(
                        text = "${day.dateEnglish} (${day.dayName})",
                        color = TextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1.3f),
                        fontFamily = SolaimanLipiFontFamily
                    )
                    Text(
                        text = day.sehriEndTime,
                        color = CyanBlue,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1.1f),
                        fontFamily = FontFamily.Monospace
                    )
                    Text(
                        text = day.iftarTime,
                        color = GoldAccent,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.1f),
                        fontFamily = FontFamily.Monospace
                    )
                    Box(
                        modifier = Modifier.weight(0.9f),
                        contentAlignment = Alignment.Center
                    ) {
                        Checkbox(
                            checked = isCompleted,
                            onCheckedChange = { onToggleFast(day.ramadanDayNumber) },
                            colors = CheckboxDefaults.colors(
                                checkedColor = PrimaryGreen,
                                uncheckedColor = DarkSurfaceBorder,
                                checkmarkColor = DarkBackground
                            ),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun RamadanDuasTab(duas: List<RamadanDua>, context: Context) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        items(duas) { dua ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, DarkGreenBorder.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = dua.titleBn,
                            color = GoldAccent,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SolaimanLipiFontFamily
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkGreen.copy(alpha = 0.4f))
                                .border(0.6.dp, PrimaryGreen.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(dua.occasionBn, color = NeonGreenGlow, fontSize = 10.5.sp, fontFamily = SolaimanLipiFontFamily)
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp, color = DarkGreenBorder.copy(alpha = 0.3f))

                    Text(
                        text = dua.arabicText,
                        color = CyanBlue,
                        fontFamily = AmiriFontFamily,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 36.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "উচ্চারণ: ${dua.transliterationBn}",
                        color = PrimaryGreen.copy(alpha = 0.9f),
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp,
                        fontFamily = SolaimanLipiFontFamily
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "অর্থ: ${dua.meaningBn}",
                        color = TextWhite,
                        fontSize = 13.5.sp,
                        lineHeight = 21.sp,
                        fontFamily = SolaimanLipiFontFamily
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Copy & Share Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText(
                                    "Ramadan Dua",
                                    "${dua.titleBn}\n\n${dua.arabicText}\n\nউচ্চারণ: ${dua.transliterationBn}\nঅর্থ: ${dua.meaningBn}"
                                )
                                clipboard.setPrimaryClip(clip)
                                Toast.makeText(context, "দোয়া কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyanBlue, modifier = Modifier.size(18.dp))
                        }

                        IconButton(
                            onClick = {
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(
                                        Intent.EXTRA_TEXT,
                                        "${dua.titleBn}\n\n${dua.arabicText}\n\nউচ্চারণ: ${dua.transliterationBn}\nঅর্থ: ${dua.meaningBn}\n[মাহে রমজান দোয়া]"
                                    )
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "দোয়া শেয়ার করুন")
                                context.startActivity(shareIntent)
                            },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TarabiAndAmalTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Tarabi Rules Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, DarkGreenBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "তারাবীহ নামাজের ফজিলত ও নিয়ম",
                    color = GoldAccent,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SolaimanLipiFontFamily
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "তারাবীহ নামাজ সুন্নাতে মুয়াক্কাদা। পুরুষদের জন্য মসজিদে খতমে তারাবীহ বা সূরা তারাবীহ জামাতে আদায় করা সুন্নাত। রাসূলুল্লাহ (সা.) ইরশাদ করেছেন: 'যে ব্যক্তি ঈমানের সাথে ও সওয়াবের আশায় রমজানের রাতে নামাজে দাঁড়ায়, তার পূর্ববর্তী সমস্ত গুনাহ মাফ করে দেওয়া হয়।' (সহিহ বুখারী: ২০০৯)",
                    color = TextWhite,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontFamily = SolaimanLipiFontFamily
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "• মোট রাকাত: ২০ রাকাত (প্রতি ২ রাকাত পর পর সালাম)\n• প্রতি ৪ রাকাত পর পর কিছুটা সময় বসে দোয়া ও তাসবীহ পাঠ করা মুস্তাহাব।",
                    color = CyanBlue,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp,
                    fontFamily = SolaimanLipiFontFamily
                )
            }
        }

        // Itikaf rules Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, PrimaryGreen.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "রমজানের শেষ দশকে ইতিকাফ ও শবে কদর",
                    color = NeonGreenGlow,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SolaimanLipiFontFamily
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "রমজানের ২০ তারিখের সূর্যাস্তের পূর্বে মসজিদে প্রবেশ করে ঈদের চাঁদ দেখার আগ পর্যন্ত মসজিদে অবস্থান করা সুন্নাতে মুয়াক্কাদা আলাল কিফায়াহ। শেষ দশকের বেজোড় রাতগুলোতে (২১, ২৩, ২৫, ২৭ ও ২৯তম রাতে) হাজার মাসের চেয়ে শ্রেষ্ঠ মহিমান্বিত লাইলাতুল কদর তালাশ করার জোর নির্দেশ রয়েছে।",
                    color = TextWhite,
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                    fontFamily = SolaimanLipiFontFamily
                )
            }
        }

        // Essential Mas'ala Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(DarkSurfaceElevated)
                .border(1.dp, DarkGreenBorder, RoundedCornerShape(16.dp))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "জরুরি রমজানের মাসআলা:",
                    color = GoldAccent,
                    fontSize = 14.5.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SolaimanLipiFontFamily
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "১. ভুলবশত কিছু খেয়ে বা পান করে ফেললে রোজা নষ্ট হয় না; মনে পড়ার সাথে সাথে খাওয়া বন্ধ করতে হবে।\n২. মেসওয়াক বা টুথব্রাশ ব্যবহার করা যায়, তবে পেটে পেস্ট বা পানি প্রবেশ করলে রোজা ভেঙে যাবে।\n৩. সুবহে সাদিকের পূর্ব পর্যন্ত সেহরি খাওয়ার সময় থাকে এবং সূর্যাস্তের সাথে সাথেই দ্রুত ইফতার করা সুন্নাত।",
                    color = TextMuted,
                    fontSize = 12.5.sp,
                    lineHeight = 19.sp,
                    fontFamily = SolaimanLipiFontFamily
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun SearchableDistrictDialog(
    currentDistrictId: String,
    onDismiss: () -> Unit,
    onSelectDistrict: (District) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    val allDistricts = MosqueRepository.districts

    val filteredDistricts = remember(searchQuery) {
        if (searchQuery.isBlank()) allDistricts
        else allDistricts.filter {
            it.nameBn.contains(searchQuery, ignoreCase = true) ||
            it.nameEn.contains(searchQuery, ignoreCase = true)
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.75f)
                .clip(RoundedCornerShape(20.dp))
                .background(DarkSurface)
                .border(1.dp, PrimaryGreen.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                .padding(16.dp)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "সেহরি-ইফতারের জেলা নির্বাচন",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = PrimaryGreen,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("জেলার নাম লিখুন...", fontSize = 12.sp, color = TextMuted, fontFamily = SolaimanLipiFontFamily) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp)) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkGreenBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    items(filteredDistricts, key = { it.id }) { dist ->
                        val isSelected = dist.id == currentDistrictId
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) DarkGreen else DarkSurfaceElevated)
                                .border(
                                    1.dp,
                                    if (isSelected) PrimaryGreen else Color.Transparent,
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onSelectDistrict(dist) }
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = dist.nameBn,
                                    color = if (isSelected) PrimaryGreen else TextWhite,
                                    fontSize = 14.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                                Text(
                                    text = dist.nameEn,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }

                            Text(
                                text = if (dist.fajrOffsetMinutes == 0) "০ মিনিট"
                                else "${if (dist.fajrOffsetMinutes > 0) "+" else ""}${dist.fajrOffsetMinutes} মি.",
                                color = if (isSelected) GoldAccent else TextMuted,
                                fontSize = 11.5.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }
    }
}
