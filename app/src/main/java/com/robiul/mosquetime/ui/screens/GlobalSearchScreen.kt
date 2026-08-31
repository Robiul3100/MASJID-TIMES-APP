package com.robiul.mosquetime.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.ui.components.AppEmptyStateView
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.navigation.Screen
import com.robiul.mosquetime.ui.theme.*
import com.robiul.mosquetime.util.HapticUtils

enum class SearchCategoryScope(val titleBn: String) {
    ALL("সকল ফলাফল"),
    QURAN("পবিত্র কুরআন"),
    DUA("দোয়া ও যিকির"),
    NOTICES("নোটিশ ও বার্তা"),
    EVENTS("ইভেন্ট ও মাহফিল"),
    COMMITTEE("কমিটি ও ইমাম"),
    HOLY_DAYS("ইসলামিক দিবস"),
    HUJUR_KHANA("হুজুরের খানা")
}

data class SearchResultItem(
    val title: String,
    val subtitle: String,
    val category: SearchCategoryScope,
    val targetRoute: String,
    val icon: ImageVector,
    val iconTint: Color
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GlobalSearchScreen(
    onBackClick: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategoryScope by remember { mutableStateOf(SearchCategoryScope.ALL) }
    var recentSearches by remember { mutableStateOf(listOf("সূরা ইয়াসীন", "আয়াতুল কুরসী", "তাহাজ্জুদ", "রমজান", "হুজুরের খানা")) }

    // Aggregate Multi-Corpus Search Engine
    val allResults = remember(searchQuery) {
        if (searchQuery.isBlank()) emptyList()
        else {
            val list = mutableListOf<SearchResultItem>()
            val query = searchQuery.trim().lowercase()

            // 1. Search Holy Quran Surahs
            MosqueRepository.quranSurahs.forEach { surah ->
                val revType = if (surah.revelationType.equals("Makki", ignoreCase = true)) "মাক্কী" else "মাদানী"
                if (surah.nameBengali.contains(query, ignoreCase = true) ||
                    surah.nameArabic.contains(query, ignoreCase = true) ||
                    surah.nameEnglish.contains(query, ignoreCase = true) ||
                    surah.meaningBengali.contains(query, ignoreCase = true) ||
                    "সূরা".contains(query, ignoreCase = true) ||
                    "কুরআন".contains(query, ignoreCase = true)
                ) {
                    list.add(
                        SearchResultItem(
                            title = "সূরা ${surah.nameBengali} (${surah.nameArabic})",
                            subtitle = "${surah.meaningBengali} • $revType • আয়াত: ${surah.totalVerses}",
                            category = SearchCategoryScope.QURAN,
                            targetRoute = Screen.QuranSurahDetail.createRoute(surah.number),
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            iconTint = CyanBlue
                        )
                    )
                }
            }

            // 2. Search Duas & Dhikr
            MosqueRepository.duas.forEach { dua ->
                if (dua.titleBn.contains(query, ignoreCase = true) ||
                    dua.meaningBn.contains(query, ignoreCase = true) ||
                    dua.pronunciationBn.contains(query, ignoreCase = true) ||
                    dua.reference.contains(query, ignoreCase = true) ||
                    "দোয়া".contains(query, ignoreCase = true) ||
                    "যিকির".contains(query, ignoreCase = true)
                ) {
                    list.add(
                        SearchResultItem(
                            title = dua.titleBn,
                            subtitle = "${dua.category.titleBn} • ${dua.meaningBn}",
                            category = SearchCategoryScope.DUA,
                            targetRoute = Screen.DuaDhikr.route,
                            icon = Icons.Outlined.Mosque,
                            iconTint = GoldAccent
                        )
                    )
                }
            }

            // 3. Search Notices & Announcements
            MosqueRepository.notices.forEach { notice ->
                if (notice.title.contains(query, ignoreCase = true) ||
                    notice.summary.contains(query, ignoreCase = true) ||
                    notice.fullContent.contains(query, ignoreCase = true) ||
                    "নোটিশ".contains(query, ignoreCase = true)
                ) {
                    list.add(
                        SearchResultItem(
                            title = notice.title,
                            subtitle = "${notice.publishedDate} • ${notice.summary}",
                            category = SearchCategoryScope.NOTICES,
                            targetRoute = Screen.NoticeBoard.route,
                            icon = Icons.Outlined.Campaign,
                            iconTint = PrimaryGreen
                        )
                    )
                }
            }

            // 4. Search Events & Mahfils
            MosqueRepository.events.forEach { event ->
                if (event.title.contains(query, ignoreCase = true) ||
                    event.speaker.contains(query, ignoreCase = true) ||
                    event.description.contains(query, ignoreCase = true) ||
                    event.locationBn.contains(query, ignoreCase = true) ||
                    "ইভেন্ট".contains(query, ignoreCase = true) ||
                    "মাহফিল".contains(query, ignoreCase = true)
                ) {
                    list.add(
                        SearchResultItem(
                            title = event.title,
                            subtitle = "${event.dateBn} (${event.timeBn}) • আলোচক: ${event.speaker}",
                            category = SearchCategoryScope.EVENTS,
                            targetRoute = Screen.Events.route,
                            icon = Icons.Outlined.Event,
                            iconTint = PurpleAccent
                        )
                    )
                }
            }

            // 5. Search Committee Members & Scholars
            MosqueRepository.committeeMembers.forEach { member ->
                if (member.name.contains(query, ignoreCase = true) ||
                    member.designationBn.contains(query, ignoreCase = true) ||
                    member.phone.contains(query, ignoreCase = true) ||
                    "কমিটি".contains(query, ignoreCase = true) ||
                    "ইমাম".contains(query, ignoreCase = true) ||
                    "মুয়াজ্জিন".contains(query, ignoreCase = true)
                ) {
                    list.add(
                        SearchResultItem(
                            title = "${member.name} (${member.designationBn})",
                            subtitle = "মোবাইল: ${member.phone} • মেয়াদ: ${member.termYears}",
                            category = SearchCategoryScope.COMMITTEE,
                            targetRoute = Screen.Committee.route,
                            icon = Icons.Outlined.People,
                            iconTint = GoldAccent
                        )
                    )
                }
            }

            // 6. Search Islamic Holy Days
            MosqueRepository.islamicHolyDays.forEach { holyDay ->
                if (holyDay.nameBn.contains(query, ignoreCase = true) ||
                    holyDay.descriptionBn.contains(query, ignoreCase = true) ||
                    "দিবস".contains(query, ignoreCase = true) ||
                    "ক্যালেন্ডার".contains(query, ignoreCase = true)
                ) {
                    list.add(
                        SearchResultItem(
                            title = holyDay.nameBn,
                            subtitle = "${holyDay.hijriDateBn} • ${holyDay.descriptionBn}",
                            category = SearchCategoryScope.HOLY_DAYS,
                            targetRoute = Screen.IslamicCalendar.route,
                            icon = Icons.Outlined.CalendarMonth,
                            iconTint = CyanBlue
                        )
                    )
                }
            }

            // 7. Search Hujur's Khana
            com.robiul.mosquetime.data.repository.MockMealScheduleRepository.daySchedules.value.forEach { day ->
                day.allMeals.forEach { meal ->
                    if (meal.responsiblePersonName.contains(query, ignoreCase = true) ||
                        meal.householdName.contains(query, ignoreCase = true) ||
                        meal.area.contains(query, ignoreCase = true) ||
                        (meal.phoneNumber != null && meal.phoneNumber.contains(query, ignoreCase = true)) ||
                        "হুজুরের খানা".contains(query, ignoreCase = true) ||
                        "খানা".contains(query, ignoreCase = true)
                    ) {
                        list.add(
                            SearchResultItem(
                                title = "${meal.mealType.titleBn}: ${meal.householdName}",
                                subtitle = "${meal.responsiblePersonName} • ${day.dateBn} • ${meal.status.titleBn}",
                                category = SearchCategoryScope.HUJUR_KHANA,
                                targetRoute = Screen.HujurKhana.route,
                                icon = Icons.Outlined.Restaurant,
                                iconTint = NeonGreenGlow
                            )
                        )
                    }
                }
            }

            list
        }
    }

    // Filter results based on selected scope
    val filteredResults = remember(allResults, selectedCategoryScope) {
        if (selectedCategoryScope == SearchCategoryScope.ALL) allResults
        else allResults.filter { it.category == selectedCategoryScope }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("global_search_screen"),
        containerColor = DarkBackground,
        topBar = {
            CommonHeader(
                title = "সার্বজনীন অনুসন্ধান",
                subtitle = "কুরআন, দোয়া, নোটিশ, ইভেন্ট ও কমিটি খুঁজুন",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search Input Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("দোয়া, সূরা, নোটিশ, ইভেন্ট বা তথ্য খুঁজুন...", color = TextMuted, fontSize = 13.sp) },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotBlank()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextMuted,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = DarkBackground,
                        unfocusedContainerColor = DarkBackground,
                        cursorColor = PrimaryGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("global_search_field")
                )

                // Scope Filter Pills (Only visible when searching)
                if (searchQuery.isNotBlank() && allResults.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(SearchCategoryScope.entries.toTypedArray()) { scope ->
                            val isSelected = selectedCategoryScope == scope
                            val count = if (scope == SearchCategoryScope.ALL) allResults.size
                            else allResults.count { it.category == scope }

                            if (count > 0 || scope == SearchCategoryScope.ALL) {
                                val bg = if (isSelected) EmeraldDeep else DarkSurfaceElevated
                                val border = if (isSelected) PrimaryGreen else DarkSurfaceBorder
                                val textColor = if (isSelected) PrimaryGreen else TextMuted

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(bg)
                                        .border(1.dp, border, RoundedCornerShape(16.dp))
                                        .clickable {
                                            HapticUtils.performLongPressHaptic(view)
                                            selectedCategoryScope = scope
                                        }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = scope.titleBn,
                                            color = textColor,
                                            fontSize = 11.5.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(CircleShape)
                                                .background(if (isSelected) PrimaryGreen else DarkSurfaceBorder)
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = "$count",
                                                color = if (isSelected) DarkBackground else TextWhite,
                                                fontSize = 9.5.sp,
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

            // Results / Empty / Suggestions View
            if (searchQuery.isBlank()) {
                // Discovery & Recent Searches State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Popular Search Suggestions
                    Text(
                        text = "জনপ্রিয় অনুসন্ধান বিষয়সমূহ",
                        color = GoldAccent,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    val popularTags = listOf(
                        "সূরা ইয়াসীন", "আয়াতুল কুরসী", "তাহাজ্জুদ", "তারাবীহ",
                        "যাকাত ক্যালকুলেটর", "হুজুরের খানা", "জুমার নামাজ", "ডিজিটাল অনুদান",
                        "শবে কদর", "ঈদুল ফিতর", "ইমাম সাহেব", "ওয়াজ মাহফিল"
                    )

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        popularTags.forEach { tag ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(DarkSurface)
                                    .border(1.dp, DarkGreenBorder.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                                    .clickable {
                                        HapticUtils.performLongPressHaptic(view)
                                        searchQuery = tag
                                    }
                                    .padding(horizontal = 12.dp, vertical = 7.dp)
                            ) {
                                Text(tag, color = CyanBlue, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Recent Searches
                    if (recentSearches.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "সাম্প্রতিক অনুসন্ধান",
                                color = TextWhite,
                                fontSize = 13.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "মুছে ফেলুন",
                                color = TextMuted,
                                fontSize = 11.5.sp,
                                modifier = Modifier.clickable { recentSearches = emptyList() }
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        recentSearches.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .clickable {
                                        HapticUtils.performLongPressHaptic(view)
                                        searchQuery = item
                                    }
                                    .padding(vertical = 8.dp, horizontal = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Outlined.History, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(item, color = TextWhite.copy(alpha = 0.85f), fontSize = 13.sp)
                                }
                                Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = TextMuted, modifier = Modifier.size(14.dp))
                            }
                            HorizontalDivider(color = DarkSurfaceBorder.copy(alpha = 0.5f))
                        }
                    }
                }
            } else if (filteredResults.isEmpty()) {
                AppEmptyStateView(
                    icon = Icons.Outlined.SearchOff,
                    title = "কোনো ফলাফল মেলেনি",
                    subtitle = "'$searchQuery' সম্পর্কিত কোনো তথ্য পাওয়া যায়নি। অন্য শব্দ দিয়ে চেষ্টা করুন।",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 24.dp)
                ) {
                    items(filteredResults) { item ->
                        SearchResultCard(
                            item = item,
                            onClick = {
                                HapticUtils.performLongPressHaptic(view)
                                onNavigateToRoute(item.targetRoute)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SearchResultCard(
    item: SearchResultItem,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface)
            .border(1.dp, DarkGreenBorder.copy(alpha = 0.6f), RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(item.iconTint.copy(alpha = 0.15f))
                    .border(0.8.dp, item.iconTint.copy(alpha = 0.4f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = item.iconTint,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.title,
                        color = TextWhite,
                        fontSize = 13.5.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(DarkBackground)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.category.titleBn,
                            color = item.iconTint,
                            fontSize = 9.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = item.subtitle,
                    color = TextMuted,
                    fontSize = 11.5.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "Navigate",
                tint = PrimaryGreen,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

