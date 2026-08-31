package com.robiul.mosquetime.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.robiul.mosquetime.data.model.DuaCategory
import com.robiul.mosquetime.data.model.DuaItem
import com.robiul.mosquetime.data.model.TasbeehItem
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.ui.components.AppEmptyStateView
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.theme.*
import com.robiul.mosquetime.util.HapticUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DuaDhikrScreen(
    modifier: Modifier = Modifier,
    onBackClick: (() -> Unit)? = null
) {
    val context = LocalContext.current
    val view = LocalView.current

    val allDuas by MosqueRepository.duasFlow.collectAsState()
    val tasbeehList = MosqueRepository.tasbeehItems

    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf<DuaCategory?>(null) }
    var bookmarkedDuaIds by remember { mutableStateOf(setOf<String>()) }
    var showOnlyBookmarked by remember { mutableStateOf(false) }

    // Tasbeeh State
    var selectedTasbeeh by remember { mutableStateOf(tasbeehList.first()) }
    var tasbeehCount by remember { mutableStateOf(0) }
    var tasbeehTarget by remember { mutableStateOf(33) }
    var showZikrSelectorDialog by remember { mutableStateOf(false) }

    val filteredDuas = remember(allDuas, searchQuery, selectedCategory, showOnlyBookmarked, bookmarkedDuaIds) {
        allDuas.filter { dua ->
            val matchesCategory = if (showOnlyBookmarked) {
                bookmarkedDuaIds.contains(dua.id)
            } else if (selectedCategory == null || selectedCategory == DuaCategory.ALL) {
                true
            } else {
                dua.category == selectedCategory
            }

            val matchesSearch = if (searchQuery.isBlank()) true else {
                dua.titleBn.contains(searchQuery, ignoreCase = true) ||
                dua.arabicText.contains(searchQuery, ignoreCase = true) ||
                dua.pronunciationBn.contains(searchQuery, ignoreCase = true) ||
                dua.meaningBn.contains(searchQuery, ignoreCase = true) ||
                dua.benefit.contains(searchQuery, ignoreCase = true)
            }

            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            CommonHeader(
                title = "প্রয়োজনীয় দোয়া ও জিকির",
                subtitle = "কুরআন ও সুন্নাহর সহিহ দোয়ার ভাণ্ডার",
                onBackClick = { onBackClick?.invoke() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 1. Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("দোয়া বা জিকির খুঁজুন...", color = TextMuted, fontSize = 13.sp, fontFamily = SolaimanLipiFontFamily) },
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

            // 2. Category Filter Chips Row
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    val isAllSelected = !showOnlyBookmarked && (selectedCategory == null || selectedCategory == DuaCategory.ALL)
                    FilterChip(
                        selected = isAllSelected,
                        onClick = {
                            showOnlyBookmarked = false
                            selectedCategory = DuaCategory.ALL
                        },
                        label = { Text("সকল দোয়া", fontSize = 11.5.sp, fontFamily = SolaimanLipiFontFamily, fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PrimaryGreen,
                            selectedLabelColor = DarkBackground,
                            containerColor = DarkSurfaceElevated,
                            labelColor = TextWhite
                        ),
                        border = FilterChipDefaults.filterChipBorder(enabled = true, selected = isAllSelected, borderColor = DarkGreenBorder, selectedBorderColor = PrimaryGreen),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                item {
                    FilterChip(
                        selected = showOnlyBookmarked,
                        onClick = {
                            showOnlyBookmarked = !showOnlyBookmarked
                            selectedCategory = null
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = if (showOnlyBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = null,
                                tint = if (showOnlyBookmarked) DarkBackground else GoldAccent,
                                modifier = Modifier.size(14.dp)
                            )
                        },
                        label = { Text("বুকমার্ককৃত", fontSize = 11.5.sp, fontFamily = SolaimanLipiFontFamily, fontWeight = if (showOnlyBookmarked) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = GoldAccent,
                            selectedLabelColor = DarkBackground,
                            containerColor = DarkSurfaceElevated,
                            labelColor = TextWhite
                        ),
                        border = FilterChipDefaults.filterChipBorder(enabled = true, selected = showOnlyBookmarked, borderColor = DarkGreenBorder, selectedBorderColor = GoldAccent),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                items(DuaCategory.values().filter { it != DuaCategory.ALL }) { cat ->
                    val isSelected = !showOnlyBookmarked && selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            showOnlyBookmarked = false
                            selectedCategory = cat
                        },
                        label = { Text(cat.titleBn, fontSize = 11.5.sp, fontFamily = SolaimanLipiFontFamily, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
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

            // 3. Main Content List
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(top = 8.dp, bottom = 40.dp)
            ) {
                // Interactive Digital Tasbeeh Section
                if (searchQuery.isBlank() && !showOnlyBookmarked && (selectedCategory == null || selectedCategory == DuaCategory.ALL)) {
                    item {
                        Card(
                            shape = RoundedCornerShape(20.dp),
                            colors = CardDefaults.cardColors(containerColor = DarkSurfaceElevated),
                            border = BorderStroke(1.dp, PrimaryGreen.copy(alpha = 0.4f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.TouchApp, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "ডিজিটাল তসবিহ ও জিকির",
                                            fontSize = 14.5.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = PrimaryGreen,
                                            fontFamily = SolaimanLipiFontFamily
                                        )
                                    }

                                    // Change Zikr Button
                                    TextButton(
                                        onClick = { showZikrSelectorDialog = true },
                                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text("জিকির পরিবর্তন", color = CyanBlue, fontSize = 12.sp, fontFamily = SolaimanLipiFontFamily)
                                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(16.dp))
                                    }
                                }

                                // Selected Zikr Arabic & Bengali
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(DarkBackground.copy(alpha = 0.6f))
                                        .border(0.6.dp, DarkGreenBorder, RoundedCornerShape(10.dp))
                                        .padding(vertical = 8.dp, horizontal = 12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = selectedTasbeeh.arabicText,
                                            fontFamily = AmiriFontFamily,
                                            fontSize = 20.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = GoldAccent
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = selectedTasbeeh.nameBn,
                                            fontSize = 13.sp,
                                            color = TextWhite,
                                            fontFamily = SolaimanLipiFontFamily
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Large Tactile Circular Tap Button
                                val progressRatio = if (tasbeehTarget > 0) (tasbeehCount % tasbeehTarget).toFloat() / tasbeehTarget else 0f

                                Box(
                                    modifier = Modifier
                                        .size(150.dp)
                                        .drawBehind {
                                            drawCircle(
                                                brush = Brush.radialGradient(
                                                    colors = listOf(PrimaryGreen.copy(alpha = 0.3f), Color.Transparent),
                                                    radius = size.width * 0.75f
                                                )
                                            )
                                        }
                                        .clip(CircleShape)
                                        .background(
                                            Brush.linearGradient(
                                                listOf(Color(0xFF132A1F), DarkBackground)
                                            )
                                        )
                                        .border(2.5.dp, PrimaryGreen, CircleShape)
                                        .clickable {
                                            HapticUtils.performLongPressHaptic(view)
                                            HapticUtils.performTactilePulse(context, 35)
                                            tasbeehCount++
                                            if (tasbeehCount % tasbeehTarget == 0) {
                                                HapticUtils.performQiblaLockPulse(context)
                                                Toast.makeText(context, "${selectedTasbeeh.nameBn} $tasbeehTarget বার পূর্ণ হয়েছে!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "$tasbeehCount",
                                            fontSize = 40.sp,
                                            fontWeight = FontWeight.Black,
                                            color = TextWhite,
                                            fontFamily = FontFamily.Monospace
                                        )
                                        Text(
                                            text = "টার্গেট: $tasbeehTarget",
                                            fontSize = 11.5.sp,
                                            color = GoldAccent,
                                            fontFamily = SolaimanLipiFontFamily
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                // Tasbeeh Controls (Reset, Target Toggles)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    OutlinedButton(
                                        onClick = {
                                            HapticUtils.performLongPressHaptic(view)
                                            tasbeehCount = 0
                                        },
                                        shape = RoundedCornerShape(10.dp),
                                        border = BorderStroke(1.dp, DarkGreenBorder)
                                    ) {
                                        Icon(Icons.Outlined.Refresh, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text("রিসেট", fontSize = 11.5.sp, color = TextMuted, fontFamily = SolaimanLipiFontFamily)
                                    }

                                    // Target Selector 33 / 99 / 100
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        listOf(33, 99, 100).forEach { target ->
                                            val isTgt = (tasbeehTarget == target)
                                            Box(
                                                modifier = Modifier
                                                    .clip(RoundedCornerShape(8.dp))
                                                    .background(if (isTgt) PrimaryGreen else DarkBackground)
                                                    .border(1.dp, if (isTgt) PrimaryGreen else DarkGreenBorder, RoundedCornerShape(8.dp))
                                                .clickable {
                                                    HapticUtils.performLongPressHaptic(view)
                                                    tasbeehTarget = target
                                                }
                                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                            ) {
                                                Text(
                                                    text = "$target",
                                                    fontSize = 11.5.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isTgt) DarkBackground else TextWhite,
                                                    fontFamily = FontFamily.Monospace
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Section Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (showOnlyBookmarked) "সংরক্ষিত দোয়াসমূহ (${filteredDuas.size})"
                                   else "${selectedCategory?.titleBn ?: "সকল দোয়া ও মোনাজাত"} (${filteredDuas.size})",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                }

                if (filteredDuas.isEmpty()) {
                    item {
                        AppEmptyStateView(
                            title = "কোনো দোয়া পাওয়া যায়নি",
                            subtitle = "ভিন্ন শব্দ বা ক্যাটাগরি দিয়ে চেষ্টা করুন।",
                            icon = Icons.AutoMirrored.Filled.MenuBook
                        )
                    }
                } else {
                    // Dua Cards
                    items(filteredDuas, key = { it.id }) { dua ->
                        val isBookmarked = bookmarkedDuaIds.contains(dua.id)

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = DarkSurfaceElevated,
                            border = BorderStroke(1.dp, DarkGreenBorder.copy(alpha = 0.6f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                // Dua Title & Category Tag
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = dua.titleBn,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = GoldAccent,
                                        fontFamily = SolaimanLipiFontFamily,
                                        modifier = Modifier.weight(1f)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(DarkGreen.copy(alpha = 0.4f))
                                            .border(0.6.dp, PrimaryGreen.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                                            .padding(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(dua.category.titleBn, fontSize = 10.sp, color = NeonGreenGlow, fontFamily = SolaimanLipiFontFamily)
                                    }
                                }

                                HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), thickness = 0.5.dp, color = DarkGreenBorder.copy(alpha = 0.3f))

                                // Arabic Calligraphy with Amiri Font
                                Text(
                                    text = dua.arabicText,
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 22.sp,
                                    lineHeight = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = CyanBlue,
                                    textAlign = TextAlign.End,
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(10.dp))

                                // Bengali Pronunciation
                                if (dua.pronunciationBn.isNotBlank()) {
                                    Text(
                                        text = "উচ্চারণ: ${dua.pronunciationBn}",
                                        fontSize = 12.5.sp,
                                        lineHeight = 19.sp,
                                        color = PrimaryGreen.copy(alpha = 0.9f),
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                }

                                // Bengali Meaning
                                Text(
                                    text = "অর্থ: ${dua.meaningBn}",
                                    fontSize = 13.5.sp,
                                    lineHeight = 21.sp,
                                    color = TextWhite,
                                    fontFamily = SolaimanLipiFontFamily
                                )

                                // Benefit / Fazilat Note (if available)
                                if (dua.benefit.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DarkBackground.copy(alpha = 0.5f))
                                            .padding(8.dp)
                                    ) {
                                        Text(
                                            text = "💡 ফজিলত: ${dua.benefit}",
                                            fontSize = 11.5.sp,
                                            color = GoldAccent.copy(alpha = 0.9f),
                                            fontFamily = SolaimanLipiFontFamily
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Reference & Actions Footer (Copy, Share, Bookmark)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "সূত্র: ${dua.reference}",
                                        fontSize = 11.sp,
                                        color = TextMuted,
                                        fontFamily = SolaimanLipiFontFamily,
                                        modifier = Modifier.weight(1f)
                                    )

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        // Bookmark Action
                                        IconButton(
                                            onClick = {
                                                bookmarkedDuaIds = if (isBookmarked) bookmarkedDuaIds - dua.id else bookmarkedDuaIds + dua.id
                                                Toast.makeText(
                                                    context,
                                                    if (isBookmarked) "বুকমার্ক থেকে সরানো হয়েছে" else "দোয়াটি বুকমার্কে সংরক্ষণ করা হয়েছে",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(
                                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                                contentDescription = "Bookmark",
                                                tint = if (isBookmarked) GoldAccent else TextMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }

                                        // Copy Action
                                        IconButton(
                                            onClick = {
                                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                val clip = ClipData.newPlainText(
                                                    "Dua",
                                                    "${dua.titleBn}\n\n${dua.arabicText}\n\nউচ্চারণ: ${dua.pronunciationBn}\nঅর্থ: ${dua.meaningBn}\nসূত্র: ${dua.reference}"
                                                )
                                                clipboard.setPrimaryClip(clip)
                                                Toast.makeText(context, "দোয়া কপি করা হয়েছে", Toast.LENGTH_SHORT).show()
                                            },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = CyanBlue, modifier = Modifier.size(18.dp))
                                        }

                                        // Share Action
                                        IconButton(
                                            onClick = {
                                                val sendIntent = Intent().apply {
                                                    action = Intent.ACTION_SEND
                                                    putExtra(
                                                        Intent.EXTRA_TEXT,
                                                        "${dua.titleBn}\n\n${dua.arabicText}\n\nউচ্চারণ: ${dua.pronunciationBn}\nঅর্থ: ${dua.meaningBn}\n[সূত্র: ${dua.reference}]"
                                                    )
                                                    type = "text/plain"
                                                }
                                                val shareIntent = Intent.createChooser(sendIntent, "দোয়া শেয়ার করুন")
                                                context.startActivity(shareIntent)
                                            },
                                            modifier = Modifier.size(30.dp)
                                        ) {
                                            Icon(Icons.Default.Share, contentDescription = "Share", tint = PrimaryGreen, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Zikr Selector Dialog
    if (showZikrSelectorDialog) {
        Dialog(onDismissRequest = { showZikrSelectorDialog = false }) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(DarkSurfaceElevated)
                    .border(1.dp, PrimaryGreen.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
                    .padding(18.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "জিকির নির্বাচন করুন",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen,
                            fontFamily = SolaimanLipiFontFamily
                        )
                        IconButton(onClick = { showZikrSelectorDialog = false }, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(tasbeehList) { item ->
                            val isSelected = (item.id == selectedTasbeeh.id)
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) DarkGreen else DarkBackground)
                                    .border(1.dp, if (isSelected) PrimaryGreen else DarkGreenBorder, RoundedCornerShape(12.dp))
                                    .clickable {
                                        selectedTasbeeh = item
                                        tasbeehTarget = item.targetCount
                                        tasbeehCount = 0
                                        showZikrSelectorDialog = false
                                    }
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = item.nameBn,
                                        color = if (isSelected) NeonGreenGlow else TextWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                    Text(
                                        text = "ডিফল্ট টার্গেট: ${item.targetCount} বার",
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                }
                                Text(
                                    text = item.arabicText,
                                    color = GoldAccent,
                                    fontFamily = AmiriFontFamily,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
