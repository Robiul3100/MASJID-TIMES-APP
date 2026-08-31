package com.robiul.mosquetime.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.material.icons.automirrored.outlined.EventNote
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
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.robiul.mosquetime.data.model.EventCategory
import com.robiul.mosquetime.data.model.MosqueEvent
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.data.repository.UserPreferencesRepository
import com.robiul.mosquetime.ui.components.AppEmptyStateView
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.theme.*
import com.robiul.mosquetime.util.HapticUtils

enum class EventStatusFilter(val titleBn: String) {
    ALL("সকল অনুষ্ঠান"),
    UPCOMING("আসন্ন আয়োজন"),
    PAST("অতীত মাহফিল আর্কাইভ")
}

@Composable
fun EventsScreen(
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val view = LocalView.current

    val eventReminders by UserPreferencesRepository.eventReminders.collectAsState()
    val events by MosqueRepository.eventsFlow.collectAsState()

    var statusFilter by remember { mutableStateOf(EventStatusFilter.ALL) }
    var selectedCategory by remember { mutableStateOf(EventCategory.ALL) }
    var searchQuery by remember { mutableStateOf("") }
    var activeEventForDialog by remember { mutableStateOf<MosqueEvent?>(null) }

    val filteredEvents = remember(events, statusFilter, selectedCategory, searchQuery) {
        events.filter { event ->
            val matchStatus = when (statusFilter) {
                EventStatusFilter.ALL -> true
                EventStatusFilter.UPCOMING -> event.isUpcoming
                EventStatusFilter.PAST -> !event.isUpcoming
            }
            val matchCategory = selectedCategory == EventCategory.ALL || event.category == selectedCategory
            val matchQuery = searchQuery.isBlank() ||
                    event.title.contains(searchQuery, ignoreCase = true) ||
                    event.speaker.contains(searchQuery, ignoreCase = true) ||
                    event.locationBn.contains(searchQuery, ignoreCase = true) ||
                    event.description.contains(searchQuery, ignoreCase = true) ||
                    event.dateBn.contains(searchQuery, ignoreCase = true)
            matchStatus && matchCategory && matchQuery
        }
    }

    val featuredUpcoming = remember(events) {
        events.firstOrNull { it.isUpcoming }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .testTag("events_screen"),
        containerColor = DarkBackground,
        topBar = {
            CommonHeader(
                title = "মসজিদের ইভেন্ট ও মাহফিল",
                subtitle = "ওয়াজ, তাফসীরুল কুরআন, সেমিনার ও বিশেষ দোয়া",
                onBackClick = onBackClick
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Top Controls Bar (Search, Status, Category)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkSurface)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                // Search Input Field
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("events_search_input"),
                    placeholder = {
                        Text(
                            text = "অনুষ্ঠান, বক্তা বা বিষয় খুঁজুন...",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = "Search",
                            tint = PrimaryGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
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
                        focusedContainerColor = DarkBackground,
                        unfocusedContainerColor = DarkBackground,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        cursorColor = PrimaryGreen
                    ),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Status Filter Segment Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    EventStatusFilter.entries.forEach { status ->
                        val isSelected = statusFilter == status
                        val bg = if (isSelected) EmeraldDeep else DarkSurfaceElevated
                        val border = if (isSelected) PrimaryGreen else DarkSurfaceBorder
                        val textColor = if (isSelected) PrimaryGreen else TextMuted

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(bg)
                                .border(1.dp, border, RoundedCornerShape(10.dp))
                                .clickable {
                                    HapticUtils.performLongPressHaptic(view)
                                    statusFilter = status
                                }
                                .padding(vertical = 7.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = status.titleBn,
                                color = textColor,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Filter Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(EventCategory.entries.toTypedArray()) { category ->
                        val isSelected = selectedCategory == category
                        val categoryBg = if (isSelected) EmeraldDeep else DarkSurfaceElevated
                        val categoryBorder = if (isSelected) PrimaryGreen else DarkSurfaceBorder
                        val categoryTextColor = if (isSelected) PrimaryGreen else TextMuted

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(18.dp))
                                .background(categoryBg)
                                .border(1.dp, categoryBorder, RoundedCornerShape(18.dp))
                                .clickable {
                                    HapticUtils.performLongPressHaptic(view)
                                    selectedCategory = category
                                }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = category.titleBn,
                                color = categoryTextColor,
                                fontSize = 11.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Events Content List
            if (filteredEvents.isEmpty()) {
                AppEmptyStateView(
                    icon = Icons.Outlined.EventBusy,
                    title = "কোনো অনুষ্ঠান পাওয়া যায়নি",
                    subtitle = if (searchQuery.isNotEmpty()) "'$searchQuery'-এর সাথে মিলে এমন কোনো আয়োজন নেই" else "এই ক্যাটাগরিতে বর্তমানে কোনো ইভেন্ট নেই",
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 14.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 12.dp)
                ) {
                    // Featured Upcoming Hero Card (Shown if filter is ALL or UPCOMING and search is blank)
                    if (searchQuery.isBlank() && statusFilter != EventStatusFilter.PAST && featuredUpcoming != null) {
                        item {
                            FeaturedEventHeroCard(
                                event = featuredUpcoming,
                                onClick = {
                                    HapticUtils.performLongPressHaptic(view)
                                    activeEventForDialog = featuredUpcoming
                                },
                                onShare = { shareEventInvitation(context, featuredUpcoming) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.EventNote,
                                    contentDescription = null,
                                    tint = PrimaryGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "সকল অনুষ্ঠান ও মাহফিল সূচি",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TextWhite
                                )
                            }
                        }
                    }

                    // Events List Items
                    items(filteredEvents, key = { it.id }) { event ->
                        val hasReminder = eventReminders.contains(event.id)

                        RichEventCardItem(
                            event = event,
                            hasReminder = hasReminder,
                            onClick = {
                                HapticUtils.performLongPressHaptic(view)
                                activeEventForDialog = event
                            },
                            onToggleReminder = {
                                HapticUtils.performLongPressHaptic(view)
                                UserPreferencesRepository.toggleEventReminder(event.id)
                                Toast.makeText(
                                    context,
                                    if (hasReminder) "অনুষ্ঠানের রিমাইন্ডার বন্ধ করা হয়েছে" else "অনুষ্ঠানের রিমাইন্ডার সেট করা হয়েছে!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            onAddToCalendar = {
                                addEventToSystemCalendar(context, event)
                            },
                            onShare = {
                                shareEventInvitation(context, event)
                            }
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }

    // Event Detail Dialog
    activeEventForDialog?.let { event ->
        val hasReminder = eventReminders.contains(event.id)
        EventDetailDialog(
            event = event,
            hasReminder = hasReminder,
            onDismiss = { activeEventForDialog = null },
            onToggleReminder = {
                UserPreferencesRepository.toggleEventReminder(event.id)
                Toast.makeText(
                    context,
                    if (hasReminder) "অনুষ্ঠানের রিমাইন্ডার বন্ধ করা হয়েছে" else "অনুষ্ঠানের রিমাইন্ডার সেট করা হয়েছে!",
                    Toast.LENGTH_SHORT
                ).show()
            },
            onAddToCalendar = { addEventToSystemCalendar(context, event) },
            onShare = { shareEventInvitation(context, event) }
        )
    }
}

/**
 * Featured Hero Event Card
 */
@Composable
private fun FeaturedEventHeroCard(
    event: MosqueEvent,
    onClick: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(
                Brush.verticalGradient(
                    listOf(Color(0xFF1F3528), DarkSurface)
                )
            )
            .border(1.2.dp, GoldAccent.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(GoldAccent.copy(alpha = 0.15f))
                        .border(1.dp, GoldAccent.copy(alpha = 0.7f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "প্রধান আকর্ষণ ও বিশেষ আয়োজন",
                            color = GoldAccent,
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(EmeraldDeep)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "আসন্ন মাহফিল",
                        color = NeonGreenGlow,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = event.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextWhite,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${event.dateBn} • ${event.timeBn}",
                    color = GoldAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "প্রধান মেহমান: ${event.speaker}",
                    color = PrimaryGreen,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = DarkSurfaceBorder)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "বিস্তারিত সূচি ও আলোচকবৃন্দ দেখুন ›",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )

                IconButton(onClick = onShare, modifier = Modifier.size(30.dp)) {
                    Icon(Icons.Outlined.Share, contentDescription = "Share", tint = GoldAccent, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}

/**
 * Rich Event Card Item
 */
@Composable
private fun RichEventCardItem(
    event: MosqueEvent,
    hasReminder: Boolean,
    onClick: () -> Unit,
    onToggleReminder: () -> Unit,
    onAddToCalendar: () -> Unit,
    onShare: () -> Unit,
    modifier: Modifier = Modifier
) {
    val categoryColor = when (event.category) {
        EventCategory.SPECIAL_DUA -> GoldAccent
        EventCategory.HALQA -> CyanBlue
        EventCategory.QURAN_CLASS -> PrimaryGreen
        EventCategory.RAMADAN -> NeonGreenGlow
        EventCategory.WAZ -> PurpleAccent
        else -> TextMuted
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(DarkSurface)
            .border(
                1.dp,
                if (event.isUpcoming) DarkGreenBorder.copy(alpha = 0.7f) else DarkSurfaceBorder,
                RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(14.dp)
            .testTag("event_card_${event.id}")
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Category Badge & Reminder Pill
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(categoryColor.copy(alpha = 0.15f))
                            .border(0.8.dp, categoryColor.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = event.category.titleBn,
                            color = categoryColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (event.isUpcoming) PrimaryGreen.copy(alpha = 0.12f) else DarkBackground)
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = if (event.isUpcoming) "আসন্ন" else "সম্পন্ন",
                            color = if (event.isUpcoming) PrimaryGreen else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (event.isUpcoming) {
                    IconButton(
                        onClick = onToggleReminder,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = if (hasReminder) Icons.Default.AlarmOn else Icons.Outlined.Alarm,
                            contentDescription = "Reminder",
                            tint = if (hasReminder) NeonGreenGlow else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = event.title,
                color = TextWhite,
                fontSize = 14.5.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 20.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Date and Time
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${event.dateBn} • ${event.timeBn}",
                    color = GoldAccent,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Location
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = event.locationBn,
                    color = CyanBlue,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Speaker
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "বক্তা: ${event.speaker}",
                    color = PrimaryGreen,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = event.description,
                color = TextMuted,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = DarkSurfaceBorder.copy(alpha = 0.6f))
            Spacer(modifier = Modifier.height(6.dp))

            // Action Buttons Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "বিস্তারিত দেখুন ›",
                    fontSize = 11.5.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (event.isUpcoming) {
                        IconButton(onClick = onAddToCalendar, modifier = Modifier.size(28.dp)) {
                            Icon(Icons.Outlined.CalendarMonth, contentDescription = "Add to Calendar", tint = CyanBlue, modifier = Modifier.size(15.dp))
                        }
                    }

                    IconButton(onClick = onShare, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Outlined.Share, contentDescription = "Share", tint = TextMuted, modifier = Modifier.size(15.dp))
                    }
                }
            }
        }
    }
}

/**
 * Event Detail Full Dialog
 */
@Composable
private fun EventDetailDialog(
    event: MosqueEvent,
    hasReminder: Boolean,
    onDismiss: () -> Unit,
    onToggleReminder: () -> Unit,
    onAddToCalendar: () -> Unit,
    onShare: () -> Unit
) {
    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(18.dp))
                .background(DarkSurfaceElevated)
                .border(1.2.dp, PrimaryGreen.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                .padding(18.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(PrimaryGreen.copy(alpha = 0.15f))
                            .border(0.8.dp, PrimaryGreen, RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = event.category.titleBn,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PrimaryGreen
                        )
                    }

                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted, modifier = Modifier.size(20.dp))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = event.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextWhite,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Schedule and Venue Card
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(DarkBackground)
                        .border(1.dp, DarkSurfaceBorder, RoundedCornerShape(10.dp))
                        .padding(12.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Event, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "${event.dateBn} (${event.timeBn})", fontSize = 12.sp, color = GoldAccent, fontWeight = FontWeight.Bold)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, tint = CyanBlue, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "স্থান: ${event.locationBn}", fontSize = 12.sp, color = CyanBlue)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, tint = PrimaryGreen, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "প্রধান আলোচক: ${event.speaker}", fontSize = 12.sp, color = PrimaryGreen, fontWeight = FontWeight.Medium)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = DarkSurfaceBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "অনুষ্ঠানের বিস্তারিত বিবরণ ও কর্মসূচি:",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = PrimaryGreen
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = event.description,
                    fontSize = 12.5.sp,
                    color = TextWhite,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = DarkSurfaceBorder)
                Spacer(modifier = Modifier.height(12.dp))

                // Action Buttons Row
                if (event.isUpcoming) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = onToggleReminder,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = if (hasReminder) NeonGreenGlow else TextWhite),
                            border = androidx.compose.foundation.BorderStroke(1.dp, if (hasReminder) NeonGreenGlow else DarkSurfaceBorder),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = if (hasReminder) Icons.Default.AlarmOn else Icons.Outlined.Alarm,
                                contentDescription = null,
                                tint = if (hasReminder) NeonGreenGlow else PrimaryGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (hasReminder) "রিমাইন্ডার সক্রিয়" else "রিমাইন্ডার দিন", fontSize = 11.sp)
                        }

                        Button(
                            onClick = onAddToCalendar,
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = CyanBlue),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Outlined.CalendarMonth, contentDescription = null, tint = DarkBackground, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("ক্যালেন্ডার", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = onShare,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Outlined.Share, contentDescription = "Share", tint = DarkBackground, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("দাওয়াতনামা শেয়ার করুন", color = DarkBackground, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

private fun addEventToSystemCalendar(context: Context, event: MosqueEvent) {
    try {
        val intent = Intent(Intent.ACTION_INSERT).apply {
            data = CalendarContract.Events.CONTENT_URI
            putExtra(CalendarContract.Events.TITLE, event.title)
            putExtra(CalendarContract.Events.DESCRIPTION, "${event.description}\n\nবক্তা: ${event.speaker}")
            putExtra(CalendarContract.Events.EVENT_LOCATION, "${event.locationBn}, চৌধুরী পাটোয়ারী বাড়ি জামে মসজিদ")
            putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, false)
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        Toast.makeText(context, "ক্যালেন্ডার অ্যাপ পাওয়া যায়নি", Toast.LENGTH_SHORT).show()
    }
}

private fun shareEventInvitation(context: Context, event: MosqueEvent) {
    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(
            Intent.EXTRA_TEXT,
            "🕌 *দ্বীনি মাহফিলের দাওয়াতনামা*\n\n📌 *বিষয়:* ${event.title}\n📅 *তারিখ:* ${event.dateBn}\n⏰ *সময়:* ${event.timeBn}\n📍 *স্থান:* ${event.locationBn}\n🗣️ *প্রধান আলোচক:* ${event.speaker}\n\n📖 *বিবরণ:* ${event.description}\n\n— চৌধুরী পাটোয়ারী বাড়ি জামে মসজিদ ও ইসলামিক সেন্টার"
        )
        type = "text/plain"
    }
    val shareIntent = Intent.createChooser(sendIntent, "মাহফিলের দাওয়াত শেয়ার করুন")
    context.startActivity(shareIntent)
}
