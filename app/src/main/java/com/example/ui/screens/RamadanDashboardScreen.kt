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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.RamadanDay
import com.example.data.model.RamadanDua
import com.example.data.repository.MosqueRepository
import com.example.data.repository.UserPreferencesRepository
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
import kotlinx.coroutines.delay
import java.util.Calendar
import java.util.Locale

@Composable
fun RamadanDashboardScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val appSettings by UserPreferencesRepository.settings.collectAsState()
    val selectedDistrictId = appSettings.selectedDistrictId
    val ramadanSchedule = remember(selectedDistrictId) {
        MosqueRepository.generateRamadanSchedule(selectedDistrictId)
    }

    var selectedTab by remember { mutableIntStateOf(0) }
    val completedFastsMap = remember { mutableStateMapOf<Int, Boolean>() }

    // Real-time Countdown Timer State
    var timeRemainingString by remember { mutableStateOf("00:00:00") }
    var countdownLabel by remember { mutableStateOf("ইফতারের বাকি") }

    LaunchedEffect(Unit) {
        while (true) {
            val now = Calendar.getInstance()
            val hour = now.get(Calendar.HOUR_OF_DAY)
            val minute = now.get(Calendar.MINUTE)
            val second = now.get(Calendar.SECOND)

            val currentSecondsOfDay = hour * 3600 + minute * 60 + second
            // Approximate today's Sehri (04:35 AM = 16500s) and Iftar (06:22 PM = 66120s)
            val sehriSeconds = 4 * 3600 + 35 * 60
            val iftarSeconds = 18 * 3600 + 22 * 60

            val targetSeconds: Int
            if (currentSecondsOfDay < sehriSeconds) {
                targetSeconds = sehriSeconds
                countdownLabel = "সেহরির সময় বাকি"
            } else if (currentSecondsOfDay < iftarSeconds) {
                targetSeconds = iftarSeconds
                countdownLabel = "ইফতারের সময় বাকি"
            } else {
                targetSeconds = 24 * 3600 + sehriSeconds
                countdownLabel = "আগামীকালের সেহরির বাকি"
            }

            val diff = (targetSeconds - currentSecondsOfDay).coerceAtLeast(0)
            val h = diff / 3600
            val m = (diff % 3600) / 60
            val s = diff % 60
            timeRemainingString = String.format(Locale.US, "%02d:%02d:%02d", h, m, s)

            delay(1000)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonHeader(
            title = "পবিত্র মাহে রমজান ড্যাশবোর্ড",
            subtitle = "সেহরি-ইফতার কাউন্টডাউন, ক্যালেন্ডার ও আমল",
            onBackClick = onBackClick
        )

        // Live Countdown & Today Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(DarkGreen.copy(alpha = 0.85f), DarkSurfaceElevated)
                    )
                )
                .border(1.5.dp, GoldAccent.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                .padding(14.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.NightlightRound, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "রমজান ১ম দিবস",
                            color = GoldAccent,
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(DarkBackground)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = countdownLabel,
                            color = CyanBlue,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Big Digital Countdown
                Text(
                    text = timeRemainingString,
                    color = NeonGreenGlow,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(thickness = 0.5.dp, color = DarkSurfaceBorder)
                Spacer(modifier = Modifier.height(10.dp))

                // Sehri & Iftar Times Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timer, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("সেহরি শেষ", color = TextMuted, fontSize = 11.sp)
                        }
                        Text("০৪:৩৫ AM", color = CyanBlue, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(modifier = Modifier.width(1.dp).height(28.dp).background(DarkSurfaceBorder))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.WbSunny, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ফজর আজান", color = TextMuted, fontSize = 11.sp)
                        }
                        Text("০৪:৪০ AM", color = GoldAccent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }

                    Box(modifier = Modifier.width(1.dp).height(28.dp).background(DarkSurfaceBorder))

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.NightlightRound, contentDescription = null, tint = NeonGreenGlow, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ইফতার সময়", color = TextMuted, fontSize = 11.sp)
                        }
                        Text("০৬:২২ PM", color = NeonGreenGlow, fontSize = 14.sp, fontWeight = FontWeight.Bold)
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
            edgePadding = 14.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = PrimaryGreen,
                    height = 2.5.dp
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
                            fontSize = 12.5.sp
                        )
                    }
                )
            }
        }

        when (selectedTab) {
            0 -> RamadanCalendarTab(
                days = ramadanSchedule,
                completedFastsMap = completedFastsMap,
                onToggleFast = { dayNum ->
                    val cur = completedFastsMap[dayNum] ?: false
                    completedFastsMap[dayNum] = !cur
                }
            )
            1 -> RamadanDuasTab(duas = MosqueRepository.ramadanDuas)
            2 -> TarabiAndAmalTab()
        }
    }
}

@Composable
private fun RamadanCalendarTab(
    days: List<RamadanDay>,
    completedFastsMap: Map<Int, Boolean>,
    onToggleFast: (Int) -> Unit
) {
    val totalCompleted = completedFastsMap.values.count { it }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 6.dp)
    ) {
        // Fast Tracker Summary Badge
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(DarkSurface)
                .border(1.dp, DarkGreenBorder, RoundedCornerShape(10.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = NeonGreenGlow, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("রোজা ট্র্যাকার সম্পন্ন:", color = TextWhite, fontSize = 12.sp)
            }
            Text("$totalCompleted / ৩০ দিন", color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Table Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(DarkGreen.copy(alpha = 0.5f))
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("রমজান", color = GoldAccent, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f))
            Text("তারিখ ও বার", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f))
            Text("সেহরি শেষ", color = CyanBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f))
            Text("ইফতার", color = NeonGreenGlow, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.1f))
            Text("ট্র্যাক", color = TextWhite, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            items(days, key = { it.ramadanDayNumber }) { day ->
                val isCompleted = completedFastsMap[day.ramadanDayNumber] ?: false

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (day.isToday) DarkGreen.copy(alpha = 0.4f) else DarkSurface)
                        .border(
                            width = if (day.isToday) 1.dp else 0.5.dp,
                            color = if (day.isToday) PrimaryGreen else DarkSurfaceBorder,
                            shape = RoundedCornerShape(8.dp)
                        )
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${day.ramadanDayNumber} রমজান",
                        color = if (day.isToday) NeonGreenGlow else TextWhite,
                        fontSize = 12.sp,
                        fontWeight = if (day.isToday) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.weight(1.2f)
                    )
                    Text(
                        text = "${day.dateEnglish} (${day.dayName})",
                        color = TextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.weight(1.3f)
                    )
                    Text(
                        text = day.sehriEndTime,
                        color = CyanBlue,
                        fontSize = 11.5.sp,
                        modifier = Modifier.weight(1.1f)
                    )
                    Text(
                        text = day.iftarTime,
                        color = GoldAccent,
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1.1f)
                    )
                    Box(
                        modifier = Modifier.weight(0.7f),
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

            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun RamadanDuasTab(duas: List<RamadanDua>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(duas) { dua ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(DarkSurface)
                    .border(1.dp, DarkGreenBorder.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
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
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(DarkBackground)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(dua.occasionBn, color = CyanBlue, fontSize = 10.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = dua.arabicText,
                        color = CyanBlue,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 28.sp,
                        textAlign = TextAlign.End,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "উচ্চারণ: ${dua.transliterationBn}",
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 17.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "অর্থ: ${dua.meaningBn}",
                        color = TextWhite,
                        fontSize = 12.5.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun TarabiAndAmalTab() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Tarabi Rules Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .border(1.dp, DarkGreenBorder, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "তারাবীহ নামাজের ফজিলত ও নিয়ম",
                    color = GoldAccent,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "তারাবীহ নামাজ সুন্নাতে মুয়াক্কাদা। পুরুষদের জন্য মসজিদে খতমে তারাবীহ বা সুরা তারাবীহ জামাতে আদায় করা সুন্নাত। রাসূলুল্লাহ (সা.) ইরশাদ করেছেন: 'যে ব্যক্তি ঈমানের সাথে ও সওয়াবের আশায় রমজানের রাতে নামাজে দাঁড়ায়, তার পূর্ববর্তী সমস্ত গুনাহ মাফ করে দেওয়া হয়।' (বুখারী: ২০০৯)",
                    color = TextWhite,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "• মোট রাকাত: ২০ রাকাত (প্রতি ২ রাকাত পর পর সালাম)\n• প্রতি ৪ রাকাত পর পর কিছুটা সময় বসে দোয়া ও তাসবীহ পাঠ করা মুস্তাহাব।",
                    color = CyanBlue,
                    fontSize = 12.sp
                )
            }
        }

        // Itikaf rules Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "রমজানের শেষ দশকে ইতিকাফ",
                    color = NeonGreenGlow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "রমজানের ২০ তারিখের সূর্যাস্তের পূর্বে মসজিদে প্রবেশ করে ঈদের চাঁদ দেখার আগ পর্যন্ত মসজিদে অবস্থান করা সুন্নাতে মুয়াক্কাদা আলাল কিফায়াহ। বায়তুল আমান জামে মসজিদে প্রতি বছর মুসল্লিদের জন্য সুশৃঙ্খল ইতিকাফের ব্যবস্থা করা হয়।",
                    color = TextWhite,
                    fontSize = 12.5.sp,
                    lineHeight = 18.sp
                )
            }
        }

        // Essential Mas'ala Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(12.dp))
                .padding(14.dp)
        ) {
            Column {
                Text(
                    text = "জরুরি রমজানের মাসআলা:",
                    color = GoldAccent,
                    fontSize = 13.5.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "১. ভুলবশত কিছু খেয়ে বা পান করে ফেললে রোজা নষ্ট হয় না; মনে পড়ার সাথে সাথে খাওয়া বন্ধ করতে হবে।\n২. মেসওয়াক বা টুথব্রাশ ব্যবহার করা যায়, তবে পেটে পেস্ট বা পানি প্রবেশ করলে রোজা ভেঙে যাবে।\n৩. সুবহে সাদিকের পূর্ব পর্যন্ত সেহরি খাওয়ার সময় থাকে এবং সূর্যাস্তের সাথে সাথেই দ্রুত ইফতার করা সুন্নাত।",
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}
