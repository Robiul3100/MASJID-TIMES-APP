package com.robiul.mosquetime.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
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
import com.robiul.mosquetime.data.firebase.MosqueAdminRepository
import com.robiul.mosquetime.data.model.District
import com.robiul.mosquetime.data.model.PrayerTimeItem
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.data.repository.UserPreferencesRepository
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.components.PrayerIcon
import com.robiul.mosquetime.ui.components.PrayerType
import com.robiul.mosquetime.ui.theme.*
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DailyPrayerScreen(
    onBackClick: () -> Unit,
    onNavigateToMonthly: () -> Unit = {},
    onNavigateToDistrictSettings: () -> Unit = {},
    modifier: Modifier = Modifier,
    adminRepo: MosqueAdminRepository = remember { MosqueAdminRepository.getInstance() }
) {
    val context = LocalContext.current
    val settings by UserPreferencesRepository.settings.collectAsState()
    val currentDistrict = MosqueRepository.getDistrictById(settings.selectedDistrictId)
    val prayerOverrides by adminRepo.prayerOverrides.collectAsState()
    val isCloudSynced by adminRepo.isCloudSynced.collectAsState()

    var showDistrictSearchDialog by remember { mutableStateOf(false) }

    // Real-time ticking state
    var currentTimeMs by remember { mutableLongStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTimeMs = System.currentTimeMillis()
            delay(1000L)
        }
    }

    // Calculate prayer list incorporating live overrides
    val todayPrayers = remember(settings.selectedDistrictId, prayerOverrides, currentTimeMs) {
        val baseList = MosqueRepository.calculateTodayPrayers(settings.selectedDistrictId)
        val cal = Calendar.getInstance()
        val isFriday = cal.get(Calendar.DAY_OF_WEEK) == Calendar.FRIDAY
        val currentMinutesOfDay = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)

        baseList.map { prayer ->
            val (azanStr, iqamahStr) = when (prayer.type) {
                PrayerType.FAJR -> (if (prayerOverrides.isCustomScheduleActive) prayerOverrides.fajrAzan else prayer.azanTime) to (if (prayerOverrides.isCustomScheduleActive) prayerOverrides.fajrIqamah else prayer.iqamahTime)
                PrayerType.DHUHR, PrayerType.JUMAH -> {
                    if (isFriday) {
                        (if (prayerOverrides.isCustomScheduleActive) prayerOverrides.jumahAzan1 else prayer.azanTime) to (if (prayerOverrides.isCustomScheduleActive) prayerOverrides.jumahJamath else prayer.iqamahTime)
                    } else {
                        (if (prayerOverrides.isCustomScheduleActive) prayerOverrides.dhuhrAzan else prayer.azanTime) to (if (prayerOverrides.isCustomScheduleActive) prayerOverrides.dhuhrIqamah else prayer.iqamahTime)
                    }
                }
                PrayerType.ASR -> (if (prayerOverrides.isCustomScheduleActive) prayerOverrides.asrAzan else prayer.azanTime) to (if (prayerOverrides.isCustomScheduleActive) prayerOverrides.asrIqamah else prayer.iqamahTime)
                PrayerType.MAGHRIB -> (if (prayerOverrides.isCustomScheduleActive) prayerOverrides.maghribAzan else prayer.azanTime) to (if (prayerOverrides.isCustomScheduleActive) prayerOverrides.maghribIqamah else prayer.iqamahTime)
                PrayerType.ISHA -> (if (prayerOverrides.isCustomScheduleActive) prayerOverrides.ishaAzan else prayer.azanTime) to (if (prayerOverrides.isCustomScheduleActive) prayerOverrides.ishaIqamah else prayer.iqamahTime)
                else -> prayer.azanTime to prayer.iqamahTime
            }

            // Determine if passed or active
            val azanMinutes = parseTimeToMinutesOfDay(azanStr, prayer.type)
            val isPassed = currentMinutesOfDay > azanMinutes + 90

            prayer.copy(
                azanTime = azanStr,
                iqamahTime = iqamahStr,
                bengaliName = if (prayer.type == PrayerType.DHUHR && isFriday) "জুমু'আ" else prayer.bengaliName,
                isJumah = prayer.type == PrayerType.DHUHR && isFriday,
                isPassed = isPassed
            )
        }
    }

    // Dynamic next prayer & countdown
    val (activeWaqt, nextWaqt, countdownFormatted) = remember(todayPrayers, currentTimeMs) {
        calculateNextPrayerAndCountdown(todayPrayers)
    }

    val extraPrayers = remember(settings.selectedDistrictId, prayerOverrides) {
        val baseExtras = MosqueRepository.getExtraPrayerTimes(settings.selectedDistrictId)
        if (prayerOverrides.isCustomScheduleActive) {
            baseExtras.map { extra ->
                when {
                    extra.name.contains("তাহাজ্জুদ") -> extra.copy(time = prayerOverrides.tahajjudTime)
                    extra.name.contains("সেহরি") -> extra.copy(time = prayerOverrides.sehriEnd)
                    extra.name.contains("ইফতার") -> extra.copy(time = prayerOverrides.iftarTime)
                    extra.name.contains("ইশরাক") -> extra.copy(time = prayerOverrides.ishraqTime)
                    extra.name.contains("চাশত") -> extra.copy(time = prayerOverrides.chashtTime)
                    else -> extra
                }
            }
        } else baseExtras
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            CommonHeader(
                title = "দৈনিক নামাজের সময়সূচি",
                subtitle = "আজকের ওয়াক্ত ও জামাতের পূর্ণাঙ্গ তালিকা",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 36.dp)
        ) {
            // 1. District Selector & Live Sync Status Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, DarkGreenBorder, RoundedCornerShape(16.dp))
                        .clickable { showDistrictSearchDialog = true }
                        .padding(14.dp)
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
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "জেলা: ${currentDistrict.nameBn}",
                                    color = TextWhite,
                                    fontSize = 14.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                                Text(
                                    text = if (currentDistrict.fajrOffsetMinutes == 0) "মূল সময় (ঢাকা মানদণ্ড)"
                                    else "ঢাকা থেকে ${if (currentDistrict.fajrOffsetMinutes > 0) "+" else ""}${currentDistrict.fajrOffsetMinutes} মি. পার্থক্য",
                                    color = TextMuted,
                                    fontSize = 11.sp,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryGreen.copy(alpha = 0.15f))
                                .border(1.dp, PrimaryGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "জেলা পরিবর্তন",
                                color = PrimaryGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = SolaimanLipiFontFamily
                            )
                        }
                    }
                }
            }

            // 2. Next Prayer Countdown Hero Card
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(DarkGreen.copy(alpha = 0.7f), DarkSurfaceElevated)
                            )
                        )
                        .border(
                            1.dp,
                            Brush.linearGradient(listOf(NeonGreenGlow.copy(alpha = 0.6f), DarkGreenBorder)),
                            RoundedCornerShape(20.dp)
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
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(DarkSurface),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Timer,
                                        contentDescription = null,
                                        tint = CyanBlue,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "পরবর্তী ওয়াক্ত: ${nextWaqt?.bengaliName ?: "ফজর"}",
                                        color = CyanBlue,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                    Text(
                                        text = "আজান: ${nextWaqt?.azanTime ?: "--:--"} | জামাত: ${nextWaqt?.iqamahTime ?: "--:--"}",
                                        color = TextMuted,
                                        fontSize = 11.5.sp,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                }
                            }

                            // Dynamic Pulsing Monospace Countdown
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = countdownFormatted,
                                    color = NeonGreenGlow,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "অবশিষ্ট সময়",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        }

                        if (prayerOverrides.isCustomScheduleActive) {
                            Spacer(modifier = Modifier.height(12.dp))
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurface.copy(alpha = 0.6f))
                                    .padding(horizontal = 10.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(PrimaryGreen)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "মসজিদের কেন্দ্রীয় পরিষদ কর্তৃক লাইভ সময়সূচি সিঙ্ক করা হয়েছে",
                                    color = PrimaryGreen,
                                    fontSize = 10.5.sp,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        }
                    }
                }
            }

            // 3. Main 5 Waqt Timetable Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "পাঁচ ওয়াক্ত নামাজের আজান ও জামাত",
                        color = PrimaryGreen,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    TextButton(onClick = onNavigateToMonthly) {
                        Text(
                            text = "মাসিক চার্ট →",
                            color = GoldAccent,
                            fontSize = 12.sp,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                }
            }

            // 4. 5 Waqt Cards
            items(todayPrayers) { prayer ->
                val isNext = prayer.type == nextWaqt?.type
                var isAlarmOn by remember { mutableStateOf(true) }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            if (isNext) DarkGreen.copy(alpha = 0.5f)
                            else DarkSurface
                        )
                        .border(
                            1.dp,
                            if (isNext) PrimaryGreen else DarkGreenBorder.copy(alpha = 0.6f),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Waqt Icon & Name
                        Row(
                            modifier = Modifier.weight(1.3f),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (isNext) PrimaryGreen.copy(alpha = 0.2f) else DarkBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                PrayerIcon(
                                    type = prayer.type,
                                    isJumah = prayer.isJumah,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = prayer.bengaliName,
                                        color = if (prayer.isJumah) GoldAccent else TextWhite,
                                        fontSize = 14.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                    if (isNext) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(NeonGreenGlow.copy(alpha = 0.2f))
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "আসন্ন",
                                                color = NeonGreenGlow,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                fontFamily = SolaimanLipiFontFamily
                                            )
                                        }
                                    }
                                }
                                Text(
                                    text = prayer.arabicName,
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // Azan & Jamath Times
                        Row(
                            modifier = Modifier.weight(1.3f),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "আজান",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                                Text(
                                    text = prayer.azanTime,
                                    color = TextWhite,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "জামাত",
                                    color = TextMuted,
                                    fontSize = 10.sp,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                                Text(
                                    text = prayer.iqamahTime,
                                    color = if (prayer.isJumah) GoldAccent else PrimaryGreen,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        // Notification Alarm Toggle
                        IconButton(
                            onClick = {
                                isAlarmOn = !isAlarmOn
                                Toast.makeText(
                                    context,
                                    "${prayer.bengaliName} আজান রিমাইন্ডার ${if (isAlarmOn) "চালু" else "বন্ধ"} করা হয়েছে",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            modifier = Modifier.size(34.dp)
                        ) {
                            Icon(
                                imageVector = if (isAlarmOn) Icons.Outlined.NotificationsActive else Icons.Outlined.NotificationsOff,
                                contentDescription = "Alarm Toggle",
                                tint = if (isAlarmOn) GoldAccent else TextMuted.copy(alpha = 0.5f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            // 5. Nafil & Extra Times Card
            item {
                Text(
                    text = "নফল ইবাদত ও গুরুত্বপূর্ণ সময়সূচি",
                    color = GoldAccent,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = SolaimanLipiFontFamily,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }

            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, GoldAccent.copy(alpha = 0.35f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        extraPrayers.forEachIndexed { index, extra ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = extra.name,
                                        color = TextWhite,
                                        fontSize = 13.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                    Text(
                                        text = extra.description,
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                }
                                Text(
                                    text = extra.time,
                                    color = GoldAccent,
                                    fontSize = 13.5.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            if (index < extraPrayers.size - 1) {
                                HorizontalDivider(thickness = 0.5.dp, color = DarkGreenBorder.copy(alpha = 0.3f))
                            }
                        }
                    }
                }
            }

            // 6. Makrooh (Forbidden) Times Caution Banner
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurfaceElevated)
                        .border(1.dp, RedDigital.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(14.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(RedDigital.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Makrooh",
                                tint = RedDigital,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "নামাজের নিষিদ্ধ (মাকরুহ) সময়সমূহ:",
                                color = RedDigital,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = SolaimanLipiFontFamily
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "১. সূর্যোদয়ের সময় (সূর্য ওঠা থেকে প্রায় ১৫ মিনিট)\n২. ঠিক দ্বিপ্রহরের সময় (সূর্য মধ্যাকাশে অবস্থানকালে)\n৩. সূর্যাস্তের পূর্ব মুহূর্ত (সূর্য হলুদ হওয়া থেকে ডোবা পর্যন্ত)",
                                color = TextWhite.copy(alpha = 0.85f),
                                fontSize = 11.5.sp,
                                lineHeight = 17.sp,
                                fontFamily = SolaimanLipiFontFamily
                            )
                        }
                    }
                }
            }
        }
    }

    // 7. Searchable District Modal Dialog
    if (showDistrictSearchDialog) {
        SearchableDistrictDialog(
            currentDistrictId = settings.selectedDistrictId,
            onDismiss = { showDistrictSearchDialog = false },
            onSelectDistrict = { dist ->
                UserPreferencesRepository.updateSettings(settings.copy(selectedDistrictId = dist.id))
                showDistrictSearchDialog = false
                Toast.makeText(context, "${dist.nameBn} জেলা নির্বাচিত হয়েছে", Toast.LENGTH_SHORT).show()
            }
        )
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
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "বাংলাদেশ জেলা নির্বাচন (৬৪টি জেলা)",
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

                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("জেলার নাম লিখুন (যেমন: ঢাকা, সিলেট...)", fontSize = 12.sp, color = TextMuted, fontFamily = SolaimanLipiFontFamily) },
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

                // District List
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

/**
 * Calculates next prayer and dynamic countdown
 */
private fun calculateNextPrayerAndCountdown(prayers: List<PrayerTimeItem>): Triple<PrayerTimeItem?, PrayerTimeItem?, String> {
    if (prayers.isEmpty()) return Triple(null, null, "00:00:00")

    val cal = Calendar.getInstance()
    val nowMinutes = cal.get(Calendar.HOUR_OF_DAY) * 60 + cal.get(Calendar.MINUTE)
    val nowSeconds = cal.get(Calendar.SECOND)

    var activeWaqt: PrayerTimeItem? = null
    var nextWaqt: PrayerTimeItem? = null
    var targetMinutes = 0

    for (i in prayers.indices) {
        val prayer = prayers[i]
        val pMinutes = parseTimeToMinutesOfDay(prayer.azanTime, prayer.type)
        if (nowMinutes < pMinutes) {
            nextWaqt = prayer
            targetMinutes = pMinutes
            activeWaqt = if (i > 0) prayers[i - 1] else prayers.last()
            break
        }
    }

    if (nextWaqt == null) {
        // Next prayer is tomorrow's Fajr
        nextWaqt = prayers.first()
        activeWaqt = prayers.last()
        val fajrMinutes = parseTimeToMinutesOfDay(nextWaqt.azanTime, nextWaqt.type)
        targetMinutes = fajrMinutes + 24 * 60
    }

    val totalDiffSeconds = (targetMinutes * 60) - (nowMinutes * 60 + nowSeconds)
    val safeDiff = if (totalDiffSeconds < 0) totalDiffSeconds + 24 * 3600 else totalDiffSeconds

    val hours = safeDiff / 3600
    val minutes = (safeDiff % 3600) / 60
    val seconds = safeDiff % 60

    val countdownStr = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    return Triple(activeWaqt, nextWaqt, countdownStr)
}

/**
 * Parses time string into minutes of the day (0..1439)
 */
private fun parseTimeToMinutesOfDay(timeStr: String, type: PrayerType): Int {
    val clean = timeStr.trim().replace("AM", "", ignoreCase = true).replace("PM", "", ignoreCase = true).trim()
    val parts = clean.split(":")
    if (parts.size < 2) return 0
    var hour = parts[0].trim().toIntOrNull() ?: 0
    val minute = parts[1].trim().toIntOrNull() ?: 0

    // Adjust 12h to 24h based on Waqt
    when (type) {
        PrayerType.DHUHR, PrayerType.JUMAH -> if (hour in 1..11) hour += 12
        PrayerType.ASR, PrayerType.MAGHRIB, PrayerType.ISHA, PrayerType.SUNSET_IFTAR -> if (hour in 1..11) hour += 12
        PrayerType.FAJR, PrayerType.SUNRISE_SEHRI -> if (hour == 12) hour = 0
        else -> Unit
    }

    return hour * 60 + minute
}

