package com.robiul.mosquetime.feature.admin.events

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.robiul.mosquetime.data.model.EventCategory
import com.robiul.mosquetime.data.model.MosqueEvent
import com.robiul.mosquetime.ui.theme.CyanBlue
import com.robiul.mosquetime.ui.theme.DarkBackground
import com.robiul.mosquetime.ui.theme.DarkGreen
import com.robiul.mosquetime.ui.theme.DarkGreenBorder
import com.robiul.mosquetime.ui.theme.DarkSurface
import com.robiul.mosquetime.ui.theme.DarkSurfaceBorder
import com.robiul.mosquetime.ui.theme.GoldAccent
import com.robiul.mosquetime.ui.theme.NeonGreenGlow
import com.robiul.mosquetime.ui.theme.PrimaryGreen
import com.robiul.mosquetime.ui.theme.PurpleAccent
import com.robiul.mosquetime.ui.theme.TextMuted
import com.robiul.mosquetime.ui.theme.TextWhite

@Composable
fun AdminEventsScreen(
    onBackClick: () -> Unit,
    viewModel: AdminEventsViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(uiState.userMessage) {
        uiState.userMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearMessage()
        }
    }

    var eventToDelete by remember { mutableStateOf<MosqueEvent?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DarkBackground,
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddDialog() },
                containerColor = PrimaryGreen,
                contentColor = TextWhite,
                shape = CircleShape,
                modifier = Modifier.testTag("fab_add_event")
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "নতুন অনুষ্ঠান যোগ করুন",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Top Bar
            AdminEventsHeader(
                onBackClick = onBackClick,
                totalEvents = uiState.events.size
            )

            // Search Bar
            OutlinedTextField(
                value = uiState.searchQuery,
                onValueChange = { viewModel.onSearchQueryChanged(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .testTag("admin_events_search"),
                placeholder = { Text("অনুষ্ঠান বা বক্তার নাম খুঁজুন...", color = TextMuted, fontSize = 14.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "অনুসন্ধান",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (uiState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "পরিষ্কার করুন",
                                tint = TextMuted,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedContainerColor = DarkSurface,
                    unfocusedContainerColor = DarkSurface,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                singleLine = true
            )

            // Category Filter Chips
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    EventCategoryChip(
                        title = "সকল অনুষ্ঠান",
                        isSelected = uiState.selectedCategory == null,
                        onClick = { viewModel.onCategorySelected(null) }
                    )
                }
                items(EventCategory.values()) { category ->
                    EventCategoryChip(
                        title = category.titleBn,
                        isSelected = uiState.selectedCategory == category,
                        onClick = { viewModel.onCategorySelected(category) }
                    )
                }
            }

            // Events List
            if (uiState.filteredEvents.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Event,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "কোনো অনুষ্ঠান পাওয়া যায়নি",
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "নতুন অনুষ্ঠান যোগ করতে নিচের প্লাস (+) বাটনে চাপুন",
                            color = TextMuted,
                            fontSize = 13.sp
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(uiState.filteredEvents, key = { it.id }) { event ->
                        AdminEventCard(
                            event = event,
                            onEdit = { viewModel.openEditDialog(event) },
                            onDelete = { eventToDelete = event },
                            onToggleStatus = { viewModel.toggleUpcoming(event) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (uiState.isAddEditOpen) {
        AdminEventAddEditDialog(
            event = uiState.editingEvent,
            isSubmitting = uiState.isSubmitting,
            onDismiss = { viewModel.closeDialog() },
            onSave = { id, title, dateBn, timeBn, locationBn, description, category, speaker, isUpcoming ->
                viewModel.saveEvent(id, title, dateBn, timeBn, locationBn, description, category, speaker, isUpcoming)
            }
        )
    }

    // Delete Confirmation Dialog
    eventToDelete?.let { event ->
        AlertDialog(
            onDismissRequest = { eventToDelete = null },
            containerColor = DarkSurface,
            title = {
                Text("অনুষ্ঠান মুছে ফেলবেন?", color = TextWhite, fontWeight = FontWeight.Bold)
            },
            text = {
                Text(
                    "আপনি কি নিশ্চিত যে '${event.title}' অনুষ্ঠানটি স্থায়ীভাবে মুছে ফেলতে চান?",
                    color = TextMuted
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEvent(event)
                        eventToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("মুছে ফেলুন", color = Color.White)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { eventToDelete = null },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("বাতিল", color = TextWhite)
                }
            }
        )
    }
}

@Composable
private fun AdminEventsHeader(
    onBackClick: () -> Unit,
    totalEvents: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier.testTag("admin_events_back_button")
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "ফিরে যান",
                tint = TextWhite
            )
        }
        Spacer(modifier = Modifier.width(4.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "অনুষ্ঠান ও মাহফিল ব্যবস্থাপনা",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "মোট অনুষ্ঠান: $totalEvents টি",
                color = GoldAccent,
                fontSize = 12.sp
            )
        }
    }
}

@Composable
private fun EventCategoryChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(if (isSelected) PrimaryGreen else DarkSurface)
            .border(
                width = 1.dp,
                color = if (isSelected) PrimaryGreen else DarkSurfaceBorder,
                shape = RoundedCornerShape(20.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        Text(
            text = title,
            color = if (isSelected) TextWhite else TextMuted,
            fontSize = 13.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
        )
    }
}

@Composable
private fun AdminEventCard(
    event: MosqueEvent,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onToggleStatus: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("admin_event_card_${event.id}"),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (event.isUpcoming) DarkGreenBorder else DarkSurfaceBorder
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Category & Upcoming Badge Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val (badgeBg, badgeTextColor) = when (event.category) {
                    EventCategory.WAZ -> Pair(Color(0x33FFB300), GoldAccent)
                    EventCategory.SPECIAL_DUA -> Pair(Color(0x334CAF50), NeonGreenGlow)
                    EventCategory.HALQA -> Pair(Color(0x3300BCD4), CyanBlue)
                    EventCategory.QURAN_CLASS -> Pair(Color(0x339C27B0), PurpleAccent)
                    EventCategory.RAMADAN -> Pair(Color(0x3300E676), PrimaryGreen)
                    EventCategory.ALL -> Pair(DarkSurfaceBorder, TextMuted)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(badgeBg)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = event.category.titleBn,
                        color = badgeTextColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (event.isUpcoming) Color(0x334CAF50) else Color(0x33757575))
                        .clickable { onToggleStatus() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (event.isUpcoming) "চলমান" else "সম্পন্ন",
                        color = if (event.isUpcoming) NeonGreenGlow else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title
            Text(
                text = event.title,
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                lineHeight = 22.sp
            )

            if (event.speaker.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = GoldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = event.speaker,
                        color = GoldAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Date & Time Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = event.dateBn,
                    color = TextMuted,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = null,
                    tint = PrimaryGreen,
                    modifier = Modifier.size(15.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = event.timeBn,
                    color = TextMuted,
                    fontSize = 12.sp
                )
            }

            if (event.locationBn.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = event.locationBn,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            if (event.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = event.description,
                    color = TextMuted,
                    fontSize = 13.sp,
                    lineHeight = 18.sp,
                    maxLines = 3
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = PrimaryGreen),
                    modifier = Modifier.testTag("admin_event_edit_${event.id}")
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("সম্পাদনা", fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedButton(
                    onClick = onDelete,
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFEF5350)),
                    modifier = Modifier.testTag("admin_event_delete_${event.id}")
                ) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("মুছুন", fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun AdminEventAddEditDialog(
    event: MosqueEvent?,
    isSubmitting: Boolean,
    onDismiss: () -> Unit,
    onSave: (
        id: String,
        title: String,
        dateBn: String,
        timeBn: String,
        locationBn: String,
        description: String,
        category: EventCategory,
        speaker: String,
        isUpcoming: Boolean
    ) -> Unit
) {
    var title by remember { mutableStateOf(event?.title ?: "") }
    var speaker by remember { mutableStateOf(event?.speaker ?: "") }
    var dateBn by remember { mutableStateOf(event?.dateBn ?: "") }
    var timeBn by remember { mutableStateOf(event?.timeBn ?: "") }
    var locationBn by remember { mutableStateOf(event?.locationBn ?: "বায়তুল আমান জামে মসজিদ") }
    var description by remember { mutableStateOf(event?.description ?: "") }
    var category by remember { mutableStateOf(event?.category ?: EventCategory.SPECIAL_DUA) }
    var isUpcoming by remember { mutableStateOf(event?.isUpcoming ?: true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = DarkSurface,
        title = {
            Text(
                text = if (event == null) "নতুন ইসলামিক অনুষ্ঠান যোগ করুন" else "অনুষ্ঠান সম্পাদনা করুন",
                color = TextWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Title Field
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("অনুষ্ঠানের নাম / শিরোনাম *", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_event_title"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Category Selection
                Text("অনুষ্ঠানের ধরন", color = GoldAccent, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(EventCategory.values()) { cat ->
                        val isSelected = category == cat
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) PrimaryGreen else DarkBackground)
                                .border(1.dp, if (isSelected) PrimaryGreen else DarkSurfaceBorder, RoundedCornerShape(8.dp))
                                .clickable { category = cat }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = cat.titleBn,
                                color = if (isSelected) TextWhite else TextMuted,
                                fontSize = 12.sp
                            )
                        }
                    }
                }

                // Speaker Field
                OutlinedTextField(
                    value = speaker,
                    onValueChange = { speaker = it },
                    label = { Text("প্রধান আলোচক / বক্তা", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_event_speaker"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Date Field
                OutlinedTextField(
                    value = dateBn,
                    onValueChange = { dateBn = it },
                    label = { Text("তারিখ (বাংলায়) *", color = TextMuted) },
                    placeholder = { Text("যেমন: ২৫ মে, ২০২৫ / ২৭ রমজান", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_event_date"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Time Field
                OutlinedTextField(
                    value = timeBn,
                    onValueChange = { timeBn = it },
                    label = { Text("সময়", color = TextMuted) },
                    placeholder = { Text("যেমন: বাদ মাগরিব / রাত ১০:৩০ টা", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_event_time"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Location Field
                OutlinedTextField(
                    value = locationBn,
                    onValueChange = { locationBn = it },
                    label = { Text("স্থান", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_event_location"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Description Field
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("বিস্তারিত বিবরণ", color = TextMuted) },
                    modifier = Modifier.fillMaxWidth().testTag("dialog_event_description"),
                    minLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryGreen,
                        unfocusedBorderColor = DarkSurfaceBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                // Upcoming Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("আসন্ন অনুষ্ঠান হিসেবে প্রদর্শন করুন", color = TextWhite, fontSize = 13.sp)
                    Switch(
                        checked = isUpcoming,
                        onCheckedChange = { isUpcoming = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = TextWhite,
                            checkedTrackColor = PrimaryGreen,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = DarkBackground
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        event?.id ?: "",
                        title,
                        dateBn,
                        timeBn,
                        locationBn,
                        description,
                        category,
                        speaker,
                        isUpcoming
                    )
                },
                enabled = !isSubmitting && title.isNotBlank() && dateBn.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("dialog_event_save_button")
            ) {
                Text(if (event == null) "সংরক্ষণ করুন" else "আপডেট করুন", color = TextWhite)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("বাতিল", color = TextWhite)
            }
        }
    )
}
