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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.MosqueRepository
import com.example.ui.components.AppEmptyStateView
import com.example.ui.components.CommonHeader
import com.example.ui.navigation.Screen
import com.example.ui.theme.CyanBlue
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkGreen
import com.example.ui.theme.DarkGreenBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.GoldAccent
import com.example.ui.theme.NeonGreenGlow
import com.example.ui.theme.PrimaryGreen
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite

data class SearchResultItem(
    val title: String,
    val subtitle: String,
    val category: String,
    val targetRoute: String,
    val icon: ImageVector,
    val iconTint: androidx.compose.ui.graphics.Color
)

@Composable
fun GlobalSearchScreen(
    onBackClick: () -> Unit,
    onNavigateToRoute: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }

    val results = remember(searchQuery) {
        if (searchQuery.isBlank()) emptyList()
        else {
            val list = mutableListOf<SearchResultItem>()
            val query = searchQuery.trim().lowercase()

            // Search Quran
            MosqueRepository.quranSurahs.forEach { surah ->
                if (surah.nameBengali.contains(query, ignoreCase = true) ||
                    surah.nameArabic.contains(query, ignoreCase = true) ||
                    surah.meaningBengali.contains(query, ignoreCase = true)
                ) {
                    list.add(
                        SearchResultItem(
                            title = "সূরা ${surah.nameBengali} (${surah.nameArabic})",
                            subtitle = "${surah.meaningBengali} • আয়াত: ${surah.totalVerses}",
                            category = "কুরআন",
                            targetRoute = Screen.QuranSurahDetail.createRoute(surah.number),
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            iconTint = CyanBlue
                        )
                    )
                }
            }

            // Search Duas
            MosqueRepository.duas.forEach { dua ->
                if (dua.titleBn.contains(query, ignoreCase = true) ||
                    dua.meaningBn.contains(query, ignoreCase = true) ||
                    dua.pronunciationBn.contains(query, ignoreCase = true)
                ) {
                    list.add(
                        SearchResultItem(
                            title = dua.titleBn,
                            subtitle = dua.meaningBn,
                            category = "দোয়া",
                            targetRoute = Screen.DuaDhikr.route,
                            icon = Icons.AutoMirrored.Filled.MenuBook,
                            iconTint = GoldAccent
                        )
                    )
                }
            }

            // Search Notices
            MosqueRepository.notices.forEach { notice ->
                if (notice.title.contains(query, ignoreCase = true) ||
                    notice.summary.contains(query, ignoreCase = true)
                ) {
                    list.add(
                        SearchResultItem(
                            title = notice.title,
                            subtitle = notice.summary,
                            category = "নোটিশ",
                            targetRoute = Screen.NoticeBoard.route,
                            icon = Icons.Default.Campaign,
                            iconTint = PrimaryGreen
                        )
                    )
                }
            }

            // Search Events
            MosqueRepository.events.forEach { event ->
                if (event.title.contains(query, ignoreCase = true) ||
                    event.speaker.contains(query, ignoreCase = true)
                ) {
                    list.add(
                        SearchResultItem(
                            title = event.title,
                            subtitle = "${event.dateBn} • ${event.speaker}",
                            category = "অনুষ্ঠান",
                            targetRoute = Screen.Events.route,
                            icon = Icons.Default.Event,
                            iconTint = PurpleAccent
                        )
                    )
                }
            }

            // Search Committee
            MosqueRepository.committeeMembers.forEach { member ->
                if (member.name.contains(query, ignoreCase = true) ||
                    member.designationBn.contains(query, ignoreCase = true)
                ) {
                    list.add(
                        SearchResultItem(
                            title = member.name,
                            subtitle = member.designationBn,
                            category = "কমিটি",
                            targetRoute = Screen.Committee.route,
                            icon = Icons.Default.People,
                            iconTint = GoldAccent
                        )
                    )
                }
            }

            // Search Hujur's Khana
            com.example.data.repository.MockMealScheduleRepository.daySchedules.value.forEach { day ->
                day.allMeals.forEach { meal ->
                    if (meal.responsiblePersonName.contains(query, ignoreCase = true) ||
                        meal.householdName.contains(query, ignoreCase = true) ||
                        meal.area.contains(query, ignoreCase = true) ||
                        "হুজুরের খানা".contains(query, ignoreCase = true) ||
                        "খানা".contains(query, ignoreCase = true)
                    ) {
                        list.add(
                            SearchResultItem(
                                title = "${meal.mealType.titleBn}: ${meal.householdName}",
                                subtitle = "${meal.responsiblePersonName} • ${day.dateBn} • ${meal.status.titleBn}",
                                category = "হুজুরের খানা",
                                targetRoute = Screen.HujurKhana.route,
                                icon = Icons.Default.Restaurant,
                                iconTint = GoldAccent
                            )
                        )
                    }
                }
            }

            list
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        CommonHeader(
            title = "সার্বজনীন অনুসন্ধান",
            subtitle = "কুরআন, দোয়া, নোটিশ ও অনুষ্ঠান খুঁজুন",
            onBackClick = onBackClick
        )

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("দোয়া, সূরা, নোটিশ বা তথ্য খুঁজুন...", color = TextMuted, fontSize = 13.5.sp) },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(20.dp))
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(18.dp))
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryGreen,
                unfocusedBorderColor = DarkGreenBorder,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = DarkSurface,
                unfocusedContainerColor = DarkSurface
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 6.dp)
                .testTag("global_search_field")
        )

        if (searchQuery.isBlank()) {
            // Suggestion shortcuts
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "দ্রুত অনুসন্ধানের পরামর্শ:",
                    color = TextMuted,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("সূরা ইয়াসীন", "রমজান", "দোয়া", "জুমাহ").forEach { query ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(DarkSurface)
                                .border(1.dp, DarkGreenBorder, RoundedCornerShape(8.dp))
                                .clickable { searchQuery = query }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(query, color = CyanBlue, fontSize = 12.sp)
                        }
                    }
                }
            }
        } else if (results.isEmpty()) {
            AppEmptyStateView(
                title = "কোনো ফলাফল পাওয়া যায়নি",
                subtitle = "'$searchQuery' সম্পর্কিত কোনো তথ্য মেলেনি। বানান পরীক্ষা করুন।"
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(results) { item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurface)
                            .border(1.dp, DarkGreenBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable { onNavigateToRoute(item.targetRoute) }
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(item.iconTint.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(imageVector = item.icon, contentDescription = null, tint = item.iconTint, modifier = Modifier.size(20.dp))
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(item.title, color = TextWhite, fontSize = 13.5.sp, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(DarkBackground)
                                            .padding(horizontal = 5.dp, vertical = 1.dp)
                                    ) {
                                        Text(item.category, color = item.iconTint, fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(item.subtitle, color = TextMuted, fontSize = 11.5.sp, maxLines = 1)
                            }

                            Text("→", color = PrimaryGreen, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
