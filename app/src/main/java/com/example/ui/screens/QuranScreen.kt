package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FormatSize
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.QuranBookmark
import com.example.data.model.QuranSurah
import com.example.data.repository.MosqueRepository
import com.example.data.repository.UserPreferencesRepository
import com.example.service.QuranAudioPlayer
import com.example.ui.components.AppEmptyStateView
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
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.RedDigital
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

@Composable
fun QuranScreen(
    onBackClick: () -> Unit,
    onSurahClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    val quranBookmarks by UserPreferencesRepository.quranBookmarks.collectAsState()

    val surahs = MosqueRepository.quranSurahs

    val filteredSurahs = remember(searchQuery) {
        if (searchQuery.isBlank()) surahs
        else surahs.filter {
            it.nameBengali.contains(searchQuery, ignoreCase = true) ||
            it.nameArabic.contains(searchQuery, ignoreCase = true) ||
            it.nameEnglish.contains(searchQuery, ignoreCase = true) ||
            it.meaningBengali.contains(searchQuery, ignoreCase = true) ||
            it.number.toString() == searchQuery.trim()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonHeader(
            title = "পবিত্র আল-কুরআনুল কারীম",
            subtitle = "অনুবাদ, উচ্চারণ ও তাফসীর সহায়িকা",
            onBackClick = onBackClick
        )

        // Search Surah Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("সূরা অনুসন্ধান করুন (নাম বা নম্বর)...", color = TextMuted, fontSize = 13.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(18.dp))
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = DarkGreenBorder,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface
            ),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
        )

        // Last Read Bookmark Banner
        if (quranBookmarks.isNotEmpty() && searchQuery.isBlank()) {
            val lastBookmark = quranBookmarks.first()
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(DarkGreen.copy(alpha = 0.7f), DarkSurface)
                        )
                    )
                    .border(1.dp, PrimaryGreen.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                    .clickable { onSurahClick(lastBookmark.surahNumber) }
                    .padding(12.dp)
            ) {
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
                                .background(DarkBackground),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Bookmark, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "সর্বশেষ পঠিত সূরা",
                                color = GoldAccent,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "সূরা ${lastBookmark.surahName} (আয়াত: ${lastBookmark.verseNumber})",
                                color = TextWhite,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PrimaryGreen)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text("পড়ুন", color = DarkBackground, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredSurahs, key = { it.number }) { surah ->
                    SurahListItem(
                        surah = surah,
                        onClick = { onSurahClick(surah.number) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
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
            .clip(RoundedCornerShape(12.dp))
            .background(DarkSurface)
            .border(1.dp, DarkGreenBorder.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(12.dp)
            .testTag("surah_item_${surah.number}")
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Surah Number Medallion
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(DarkGreen.copy(alpha = 0.4f))
                    .border(1.dp, PrimaryGreen.copy(alpha = 0.7f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${surah.number}",
                    color = NeonGreenGlow,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Bengali Name and Meaning
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "সূরা ${surah.nameBengali}",
                        color = TextWhite,
                        fontSize = 14.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkBackground)
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = surah.revelationType,
                            color = CyanBlue,
                            fontSize = 9.5.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "${surah.meaningBengali} • আয়াত: ${surah.totalVerses}",
                    color = TextMuted,
                    fontSize = 11.5.sp
                )
            }

            // Arabic Name
            Text(
                text = surah.nameArabic,
                color = GoldAccent,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun QuranSurahDetailScreen(
    surahNumber: Int,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val surah = MosqueRepository.quranSurahs.find { it.number == surahNumber } ?: MosqueRepository.quranSurahs.first()
    var fontScale by remember { mutableFloatStateOf(1.0f) }
    val quranBookmarks by UserPreferencesRepository.quranBookmarks.collectAsState()
    val audioState by QuranAudioPlayer.audioState.collectAsState()

    DisposableEffect(Unit) {
        onDispose {
            // keep playing or stop if user backs out
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonHeader(
            title = "সূরা ${surah.nameBengali}",
            subtitle = "${surah.nameArabic} • ${surah.revelationType} • আয়াত: ${surah.totalVerses}",
            onBackClick = {
                QuranAudioPlayer.stop()
                onBackClick()
            },
            actionIcon = Icons.Default.FormatSize,
            actionDescription = "Font size",
            onActionClick = {
                fontScale = if (fontScale >= 1.3f) 0.9f else fontScale + 0.15f
            }
        )

        // Embedded Quran Audio Player Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    Brush.verticalGradient(
                        listOf(DarkSurfaceElevated, DarkSurface)
                    )
                )
                .border(1.dp, PrimaryGreen.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
                .padding(12.dp)
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
                        Text("ক্বারী নির্বাচন:", color = TextMuted, fontSize = 11.sp)
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
                                    fontSize = 10.5.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
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

                // Controls: Replay 10s, Play/Pause, Forward 10s, Speed selector
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
                                Text("${spd}x", color = if (isSpd) CyanBlue else TextMuted, fontSize = 9.5.sp)
                            }
                        }
                    }

                    // Main Controls
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { QuranAudioPlayer.seekRelative(-10000) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Replay10, contentDescription = "Replay 10s", tint = TextWhite, modifier = Modifier.size(18.dp))
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        // Play/Pause Big Button
                        Box(
                            modifier = Modifier
                                .size(40.dp)
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
                                    modifier = Modifier.size(20.dp),
                                    color = DarkBackground,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(
                                    imageVector = if (audioState.isPlaying && audioState.currentSurahNumber == surah.number) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = "Play/Pause",
                                    tint = DarkBackground,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = { QuranAudioPlayer.seekRelative(10000) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.Forward10, contentDescription = "Forward 10s", tint = TextWhite, modifier = Modifier.size(18.dp))
                        }
                    }

                    Text(
                        text = if (audioState.isPlaying) "তেলাওয়াত চলছে" else "তেলাওয়াত শুনুন",
                        color = if (audioState.isPlaying) NeonGreenGlow else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (audioState.errorMessage != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = audioState.errorMessage ?: "",
                        color = RedDigital,
                        fontSize = 10.5.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Bismillah Header (Except for Surah 9)
            if (surah.number != 9) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(DarkSurfaceElevated, DarkSurface)
                                )
                            )
                            .border(1.dp, GoldAccent.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ",
                                color = GoldAccent,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "পরম করুণাময় অসীম দয়ালু আল্লাহর নামে শুরু করছি",
                                color = TextMuted,
                                fontSize = 11.5.sp
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
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .border(1.dp, DarkGreenBorder.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        .padding(14.dp)
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
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(DarkBackground)
                                    .border(1.dp, PrimaryGreen.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "আয়াত ${verse.verseNumber}",
                                    color = NeonGreenGlow,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = {
                                        UserPreferencesRepository.saveQuranBookmark(
                                            QuranBookmark(surah.number, surah.nameBengali, verse.verseNumber)
                                        )
                                        Toast.makeText(context, "আয়াত ${verse.verseNumber} সংরক্ষণ করা হয়েছে", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                        contentDescription = "Bookmark Verse",
                                        tint = if (isBookmarked) GoldAccent else TextMuted,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        val clip = ClipData.newPlainText("Quran Verse", "${verse.arabicText}\n\n${verse.bengaliTranslation}\n[সূরা ${surah.nameBengali}: ${verse.verseNumber}]")
                                        clipboard.setPrimaryClip(clip)
                                        Toast.makeText(context, "আয়াত কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier.size(30.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy Verse",
                                        tint = CyanBlue,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), thickness = 0.5.dp, color = DarkSurfaceBorder)

                        // Arabic Text
                        Text(
                            text = verse.arabicText,
                            color = CyanBlue,
                            fontSize = (22 * fontScale).sp,
                            lineHeight = (36 * fontScale).sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Pronunciation (if available)
                        if (verse.bengaliPronunciation.isNotBlank()) {
                            Text(
                                text = "উচ্চারণ: ${verse.bengaliPronunciation}",
                                color = TextMuted,
                                fontSize = (12.5 * fontScale).sp,
                                lineHeight = (18 * fontScale).sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                        }

                        // Bengali Translation
                        Text(
                            text = "অর্থ: ${verse.bengaliTranslation}",
                            color = TextWhite,
                            fontSize = (13.5 * fontScale).sp,
                            lineHeight = (21 * fontScale).sp,
                            fontWeight = FontWeight.Medium
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
