package com.robiul.mosquetime.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.*
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
import com.robiul.mosquetime.data.model.QuranBookmark
import com.robiul.mosquetime.data.model.QuranSurah
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.data.repository.UserPreferencesRepository
import com.robiul.mosquetime.service.QuranAudioPlayer
import com.robiul.mosquetime.ui.components.AppEmptyStateView
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.theme.*

enum class SurahCategoryFilter(val titleBn: String) {
    ALL("সকল সূরা"),
    MAKKI("মাক্কী"),
    MADANI("মাদানী"),
    BOOKMARKED("বুকমার্ক")
}

@Composable
fun QuranScreen(
    onBackClick: () -> Unit,
    onSurahClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(SurahCategoryFilter.ALL) }
    val quranBookmarks by UserPreferencesRepository.quranBookmarks.collectAsState()

    val surahs = MosqueRepository.quranSurahs

    val filteredSurahs = remember(searchQuery, selectedFilter, quranBookmarks) {
        surahs.filter { surah ->
            val matchesCategory = when (selectedFilter) {
                SurahCategoryFilter.ALL -> true
                SurahCategoryFilter.MAKKI -> surah.revelationType.contains("মাক্কী", ignoreCase = true) || surah.revelationType.contains("Makki", ignoreCase = true)
                SurahCategoryFilter.MADANI -> surah.revelationType.contains("মাদানী", ignoreCase = true) || surah.revelationType.contains("Madani", ignoreCase = true)
                SurahCategoryFilter.BOOKMARKED -> quranBookmarks.any { it.surahNumber == surah.number }
            }

            val matchesSearch = if (searchQuery.isBlank()) true else {
                surah.nameBengali.contains(searchQuery, ignoreCase = true) ||
                surah.nameArabic.contains(searchQuery, ignoreCase = true) ||
                surah.nameEnglish.contains(searchQuery, ignoreCase = true) ||
                surah.meaningBengali.contains(searchQuery, ignoreCase = true) ||
                surah.number.toString() == searchQuery.trim()
            }

            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            CommonHeader(
                title = "পবিত্র আল-কুরআনুল কারীম",
                subtitle = "অনুবাদ, উচ্চারণ ও অডিও তিলাওয়াত",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Surah Field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("সূরা খুঁজুন (নাম বা নম্বর লিখুন)...", color = TextMuted, fontSize = 13.sp, fontFamily = SolaimanLipiFontFamily) },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = DarkGreenBorder,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
            )

            // Category Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(SurahCategoryFilter.values()) { filter ->
                    val isSelected = selectedFilter == filter
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                text = filter.titleBn,
                                fontSize = 11.5.sp,
                                fontFamily = SolaimanLipiFontFamily,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen,
                            selectedLabelColor = DarkBackground,
                            containerColor = DarkSurfaceElevated,
                            labelColor = TextWhite
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = DarkGreenBorder,
                            selectedBorderColor = PrimaryGreen
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )
                }
            }

            // Last Read Bookmark Banner
            if (quranBookmarks.isNotEmpty() && searchQuery.isBlank()) {
                val lastBookmark = quranBookmarks.first()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(DarkGreen.copy(alpha = 0.7f), DarkSurfaceElevated)
                            )
                        )
                        .border(1.dp, PrimaryGreen.copy(alpha = 0.8f), RoundedCornerShape(14.dp))
                        .clickable { onSurahClick(lastBookmark.surahNumber) }
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
                                    .background(DarkBackground),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Bookmark, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "সর্বশেষ পঠিত সূরা",
                                    color = GoldAccent,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                                Text(
                                    text = "সূরা ${lastBookmark.surahName} (আয়াত: ${lastBookmark.verseNumber})",
                                    color = TextWhite,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(PrimaryGreen)
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text("পড়ুন", color = DarkBackground, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                        }
                    }
                }
            }

            // Surah Directory List
            if (filteredSurahs.isEmpty()) {
                AppEmptyStateView(
                    title = "কোনো সূরা পাওয়া যায়নি",
                    subtitle = "সঠিক সূরার নাম বা নম্বর দিয়ে খুঁজুন।",
                    icon = Icons.AutoMirrored.Filled.MenuBook
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 6.dp, bottom = 32.dp)
                ) {
                    items(filteredSurahs, key = { it.number }) { surah ->
                        SurahListItem(
                            surah = surah,
                            onClick = { onSurahClick(surah.number) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SurahListItem(
    surah: QuranSurah,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurfaceElevated)
            .border(1.dp, DarkGreenBorder.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Surah Number Medallion
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(DarkGreen.copy(alpha = 0.5f))
                    .border(1.dp, PrimaryGreen.copy(alpha = 0.7f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${surah.number}",
                    color = NeonGreenGlow,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            // Bengali Name and Meaning
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "সূরা ${surah.nameBengali}",
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = SolaimanLipiFontFamily
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkBackground)
                            .border(0.6.dp, CyanBlue.copy(alpha = 0.4f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = surah.revelationType,
                            color = CyanBlue,
                            fontSize = 10.sp,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${surah.meaningBengali} • আয়াত: ${surah.totalVerses}",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontFamily = SolaimanLipiFontFamily
                )
            }

            // Arabic Name
            Text(
                text = surah.nameArabic,
                color = GoldAccent,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuranSurahDetailScreen(
    surahNumber: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val surah = MosqueRepository.quranSurahs.find { it.number == surahNumber } ?: MosqueRepository.quranSurahs.first()
    var fontScale by remember { mutableFloatStateOf(1.0f) }
    var showPronunciation by remember { mutableStateOf(true) }
    var showTranslation by remember { mutableStateOf(true) }
    var showSettingsSheet by remember { mutableStateOf(false) }

    val quranBookmarks by UserPreferencesRepository.quranBookmarks.collectAsState()
    val audioState by QuranAudioPlayer.audioState.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            // keep audio playing or gracefully manage
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "সূরা ${surah.nameBengali}",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextWhite,
                            fontFamily = SolaimanLipiFontFamily
                        )
                        Text(
                            text = "${surah.nameArabic} • ${surah.revelationType} • আয়াত: ${surah.totalVerses}",
                            fontSize = 11.sp,
                            color = PrimaryGreen,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {
                        QuranAudioPlayer.stop()
                        onBackClick()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "ফিরে যান",
                            tint = TextWhite
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showSettingsSheet = true }) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "পড়ার সেটিংস",
                            tint = GoldAccent
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = TextWhite
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Embedded Quran Audio Player Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.verticalGradient(
                            listOf(DarkSurfaceElevated, DarkSurface)
                        )
                    )
                    .border(1.dp, PrimaryGreen.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .padding(14.dp)
            ) {
                Column {
                    // Qari Selector Chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Headphones, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ক্বারী:", color = TextMuted, fontSize = 11.5.sp, fontFamily = SolaimanLipiFontFamily)
                        }

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            items(MosqueRepository.qariList) { qari ->
                                val isSelected = (qari.id == audioState.selectedQariId)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isSelected) DarkGreen else DarkBackground)
                                        .border(1.dp, if (isSelected) PrimaryGreen else DarkSurfaceBorder, RoundedCornerShape(6.dp))
                                        .clickable {
                                            QuranAudioPlayer.playSurah(context, surah.number, qari.id)
                                        }
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = if (qari.id == "mishary") "আলাফাসী" else if (qari.id == "sudais") "সুদাইস" else "মুয়াইক্বলী",
                                        color = if (isSelected) NeonGreenGlow else TextWhite,
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Progress Slider & Timestamps
                    val totalSecs = audioState.durationMs / 1000
                    val currentSecs = audioState.currentPositionMs / 1000
                    val progressFloat = if (audioState.durationMs > 0) {
                        (audioState.currentPositionMs.toFloat() / audioState.durationMs.toFloat()).coerceIn(0f, 1f)
                    } else 0f

                    fun fmtTime(sec: Int): String {
                        val m = sec / 60
                        val s = sec % 60
                        return String.format("%02d:%02d", m, s)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(fmtTime(currentSecs), color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                        Slider(
                            value = progressFloat,
                            onValueChange = { newProg ->
                                val seekTarget = (newProg * audioState.durationMs).toInt()
                                QuranAudioPlayer.seekTo(seekTarget)
                            },
                            colors = SliderDefaults.colors(
                                thumbColor = PrimaryGreen,
                                activeTrackColor = PrimaryGreen,
                                inactiveTrackColor = DarkSurfaceBorder
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .height(20.dp)
                                .padding(horizontal = 8.dp)
                        )
                        Text(if (totalSecs > 0) fmtTime(totalSecs) else "--:--", color = TextMuted, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }

                    // Audio Controls Bar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Speed Chip
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(1.0f, 1.25f, 1.5f).forEach { spd ->
                                val isSpd = (audioState.playbackSpeed == spd)
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (isSpd) CyanBlue.copy(alpha = 0.2f) else DarkBackground)
                                        .clickable { QuranAudioPlayer.setSpeed(spd) }
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text("${spd}x", color = if (isSpd) CyanBlue else TextMuted, fontSize = 10.sp)
                                }
                            }
                        }

                        // Main Play Controls
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = { QuranAudioPlayer.seekRelative(-10000) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Replay10, contentDescription = "Replay 10s", tint = TextWhite, modifier = Modifier.size(20.dp))
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            // Play/Pause Button
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryGreen)
                                    .clickable {
                                        if (audioState.currentSurahNumber != surah.number && !audioState.isPlaying) {
                                            QuranAudioPlayer.playSurah(context, surah.number)
                                        } else {
                                            QuranAudioPlayer.togglePlayPause()
                                        }
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                if (audioState.isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        color = DarkBackground,
                                        strokeWidth = 2.5.dp
                                    )
                                } else {
                                    Icon(
                                        imageVector = if (audioState.isPlaying && audioState.currentSurahNumber == surah.number) Icons.Default.Pause else Icons.Default.PlayArrow,
                                        contentDescription = "Play/Pause",
                                        tint = DarkBackground,
                                        modifier = Modifier.size(26.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            IconButton(
                                onClick = { QuranAudioPlayer.seekRelative(10000) },
                                modifier = Modifier.size(34.dp)
                            ) {
                                Icon(Icons.Default.Forward10, contentDescription = "Forward 10s", tint = TextWhite, modifier = Modifier.size(20.dp))
                            }
                        }

                        Text(
                            text = if (audioState.isPlaying) "তেলাওয়াত চলছে" else "তেলাওয়াত শুনুন",
                            color = if (audioState.isPlaying) NeonGreenGlow else TextMuted,
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }

                    if (audioState.errorMessage != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = audioState.errorMessage ?: "",
                            color = RedDigital,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth(),
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
            ) {
                // Bismillah Header (Except for Surah At-Tawbah #9)
                if (surah.number != 9) {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(DarkSurfaceElevated, DarkSurface)
                                    )
                                )
                                .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .padding(18.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                    color = GoldAccent,
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "পরম করুণাময় অসীম দয়ালু আল্লাহর নামে শুরু করছি",
                                    color = TextMuted,
                                    fontSize = 12.sp,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        }
                    }
                }

                // Verses List
                items(surah.verses, key = { it.verseNumber }) { verse ->
                    val isBookmarked = quranBookmarks.any { it.surahNumber == surah.number && it.verseNumber == verse.verseNumber }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurfaceElevated)
                            .border(1.dp, DarkGreenBorder.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                            .padding(16.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            // Verse Header with number and actions
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(DarkGreen.copy(alpha = 0.35f))
                                        .border(1.dp, PrimaryGreen.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = "আয়াত ${verse.verseNumber}",
                                        color = NeonGreenGlow,
                                        fontSize = 11.5.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = {
                                            UserPreferencesRepository.saveQuranBookmark(
                                                QuranBookmark(surah.number, surah.nameBengali, verse.verseNumber)
                                            )
                                            Toast.makeText(context, "সূরা ${surah.nameBengali} আয়াত ${verse.verseNumber} সংরক্ষণ করা হয়েছে", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = "Bookmark Verse",
                                            tint = if (isBookmarked) GoldAccent else TextMuted,
                                            modifier = Modifier.size(19.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val clip = ClipData.newPlainText("Quran Verse", "${verse.arabicText}\n\n${verse.bengaliTranslation}\n[সূরা ${surah.nameBengali}: ${verse.verseNumber}]")
                                            clipboard.setPrimaryClip(clip)
                                            Toast.makeText(context, "আয়াত কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy Verse",
                                            tint = CyanBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    IconButton(
                                        onClick = {
                                            val sendIntent = Intent().apply {
                                                action = Intent.ACTION_SEND
                                                putExtra(Intent.EXTRA_TEXT, "${verse.arabicText}\n\n${verse.bengaliTranslation}\n\n[পবিত্র কুরআন, সূরা ${surah.nameBengali}: আয়াত ${verse.verseNumber}]")
                                                type = "text/plain"
                                            }
                                            val shareIntent = Intent.createChooser(sendIntent, "আয়াত শেয়ার করুন")
                                            context.startActivity(shareIntent)
                                        },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Share Verse",
                                            tint = PrimaryGreen,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp, color = DarkGreenBorder.copy(alpha = 0.3f))

                            // Arabic Text with Amiri Quran Font
                            Text(
                                text = verse.arabicText,
                                fontFamily = AmiriFontFamily,
                                color = CyanBlue,
                                fontSize = (25 * fontScale).sp,
                                lineHeight = (42 * fontScale).sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.End,
                                modifier = Modifier.fillMaxWidth()
                            )

                            // Pronunciation (if enabled)
                            if (showPronunciation && verse.bengaliPronunciation.isNotBlank()) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "উচ্চারণ: ${verse.bengaliPronunciation}",
                                    color = TextMuted,
                                    fontSize = (13 * fontScale).sp,
                                    lineHeight = (19 * fontScale).sp,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }

                            // Bengali Translation (if enabled)
                            if (showTranslation) {
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "অর্থ: ${verse.bengaliTranslation}",
                                    color = TextWhite,
                                    fontSize = (14 * fontScale).sp,
                                    lineHeight = (22 * fontScale).sp,
                                    fontWeight = FontWeight.Medium,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Reading Settings Bottom Sheet Dialog
    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            containerColor = DarkSurfaceElevated,
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            ) {
                Text(
                    text = "📖 কুরআন পড়ার কাস্টমাইজেশন",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = GoldAccent,
                    fontFamily = SolaimanLipiFontFamily
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Font Size Slider
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("আরবি ফন্ট সাইজ:", fontSize = 13.sp, color = TextWhite, fontFamily = SolaimanLipiFontFamily)
                    Text("${(fontScale * 100).toInt()}%", fontSize = 12.sp, color = PrimaryGreen, fontWeight = FontWeight.Bold)
                }

                Slider(
                    value = fontScale,
                    onValueChange = { fontScale = it },
                    valueRange = 0.85f..1.5f,
                    colors = SliderDefaults.colors(
                        thumbColor = PrimaryGreen,
                        activeTrackColor = PrimaryGreen,
                        inactiveTrackColor = DarkSurfaceBorder
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Pronunciation Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("বাংলা উচ্চারণ দেখান", fontSize = 13.5.sp, color = TextWhite, fontFamily = SolaimanLipiFontFamily)
                    Switch(
                        checked = showPronunciation,
                        onCheckedChange = { showPronunciation = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PrimaryGreen, checkedTrackColor = DarkGreen)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Translation Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("বাংলা অনুবাদ ও অর্থ দেখান", fontSize = 13.5.sp, color = TextWhite, fontFamily = SolaimanLipiFontFamily)
                    Switch(
                        checked = showTranslation,
                        onCheckedChange = { showTranslation = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = PrimaryGreen, checkedTrackColor = DarkGreen)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = { showSettingsSheet = false },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("সম্পন্ন", color = DarkBackground, fontWeight = FontWeight.Bold, fontFamily = SolaimanLipiFontFamily)
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
