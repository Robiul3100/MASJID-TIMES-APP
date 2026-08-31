package com.example.feature.admin.notices

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.PushPin
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.core.auth.AdminUser
import com.example.data.model.NoticeCategory
import com.example.data.model.NoticeItem
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminNoticeScreen(
    currentAdmin: AdminUser?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: AdminNoticeViewModel = viewModel()
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.selectedCategory.collectAsStateWithLifecycle()
    val actionMessage by viewModel.actionMessage.collectAsStateWithLifecycle()

    var showCreateDialog by remember { mutableStateOf(false) }
    var editingNotice by remember { mutableStateOf<NoticeItem?>(null) }
    var noticeToDelete by remember { mutableStateOf<NoticeItem?>(null) }

    LaunchedEffect(currentAdmin) {
        viewModel.setCurrentAdmin(currentAdmin)
    }

    LaunchedEffect(actionMessage) {
        actionMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            viewModel.clearActionMessage()
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
                            text = "বিজ্ঞপ্তি ও নোটিশ বোর্ড পরিচালনা",
                            style = AppTypography.screenTitle,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "পাবলিক নোটিশ ও ইসলামিক ঘোষণা প্রকাশ করুন",
                            fontSize = 11.sp,
                            color = PrimaryGreen,
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "ফিরে যান",
                            tint = TextWhite
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showCreateDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "নতুন নোটিশ তৈরি",
                            tint = TextWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DarkSurface,
                    titleContentColor = TextWhite
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showCreateDialog = true },
                containerColor = PrimaryGreen,
                contentColor = DarkBackground
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "নতুন নোটিশ")
            }
        }
    ) { innerPadding ->
        when (val state = uiState) {
            is AdminNoticeUiState.Loading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = PrimaryGreen)
                }
            }
            is AdminNoticeUiState.Error -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = state.message, color = RedDigital)
                }
            }
            is AdminNoticeUiState.Success -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp)
                ) {
                    // Search Bar
                    item {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = viewModel::onSearchQueryChange,
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text("বিজ্ঞপ্তির শিরোনাম বা বিষয় দিয়ে খুঁজুন...", fontSize = 13.sp, color = TextMuted) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = PrimaryGreen) },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                                    }
                                }
                            },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PrimaryGreen,
                                unfocusedBorderColor = DarkGreenBorder,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // Category Filter Chips
                    item {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            items(NoticeCategory.values()) { cat ->
                                FilterChip(
                                    selected = selectedCategory == cat,
                                    onClick = { viewModel.onCategorySelect(cat) },
                                    label = { Text(cat.titleBn, fontSize = 11.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = PrimaryGreen,
                                        selectedLabelColor = DarkBackground,
                                        containerColor = DarkSurface,
                                        labelColor = TextWhite
                                    )
                                )
                            }
                        }
                    }

                    // Notice List
                    if (state.filteredNotices.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 32.dp),
                                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "কোনো নোটিশ পাওয়া যায়নি",
                                        color = TextMuted,
                                        fontFamily = SolaimanLipiFontFamily
                                    )
                                }
                            }
                        }
                    } else {
                        items(state.filteredNotices, key = { it.id }) { notice ->
                            AdminNoticeItemCard(
                                notice = notice,
                                onEdit = { editingNotice = notice },
                                onTogglePin = { viewModel.togglePin(notice) },
                                onDelete = { noticeToDelete = notice }
                            )
                        }
                    }
                }
            }
        }
    }

    // Create / Edit Dialog
    if (showCreateDialog || editingNotice != null) {
        val target = editingNotice
        NoticeFormDialog(
            initialNotice = target,
            onDismiss = {
                showCreateDialog = false
                editingNotice = null
            },
            onSave = { title, summary, fullContent, cat, pinned, author ->
                viewModel.saveNotice(
                    id = target?.id,
                    title = title,
                    summary = summary,
                    fullContent = fullContent,
                    category = cat,
                    isPinned = pinned,
                    author = author
                )
                showCreateDialog = false
                editingNotice = null
            }
        )
    }

    // Delete Confirmation Dialog
    noticeToDelete?.let { notice ->
        AlertDialog(
            onDismissRequest = { noticeToDelete = null },
            title = { Text("বিজ্ঞপ্তি মুছে ফেলবেন?", color = TextWhite, fontFamily = SolaimanLipiFontFamily) },
            text = {
                Text(
                    text = "আপনি কি নিশ্চিতভাবে \"${notice.title}\" বিজ্ঞপ্তিটি মুছে ফেলতে চান? এটি পাবলিক নোটিশ বোর্ড থেকেও মুছে যাবে।",
                    color = TextMuted,
                    fontFamily = SolaimanLipiFontFamily
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteNotice(notice.id, notice.title)
                        noticeToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedDigital)
                ) {
                    Text("মুছে ফেলুন", color = TextWhite, fontFamily = SolaimanLipiFontFamily)
                }
            },
            dismissButton = {
                TextButton(onClick = { noticeToDelete = null }) {
                    Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
                }
            },
            containerColor = DarkSurface
        )
    }
}

@Composable
private fun AdminNoticeItemCard(
    notice: NoticeItem,
    onEdit: () -> Unit,
    onTogglePin: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            if (notice.isPinned) GoldAccent.copy(alpha = 0.6f) else DarkGreenBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Category Badge + Pinned + Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = when (notice.category) {
                            NoticeCategory.URGENT -> RedDigital.copy(alpha = 0.2f)
                            NoticeCategory.JUMAH -> PrimaryGreen.copy(alpha = 0.2f)
                            NoticeCategory.SPECIAL -> GoldAccent.copy(alpha = 0.2f)
                            else -> DarkSurfaceElevated
                        },
                        border = BorderStroke(
                            1.dp,
                            when (notice.category) {
                                NoticeCategory.URGENT -> RedDigital
                                NoticeCategory.JUMAH -> PrimaryGreen
                                NoticeCategory.SPECIAL -> GoldAccent
                                else -> DarkGreenBorder
                            }
                        )
                    ) {
                        Text(
                            text = notice.category.titleBn,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (notice.category) {
                                NoticeCategory.URGENT -> RedDigital
                                NoticeCategory.JUMAH -> PrimaryGreen
                                NoticeCategory.SPECIAL -> GoldAccent
                                else -> TextWhite
                            },
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                            fontFamily = SolaimanLipiFontFamily
                        )
                    }

                    if (notice.isPinned) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = GoldAccent.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, GoldAccent)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PushPin,
                                    contentDescription = null,
                                    tint = GoldAccent,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "পিন করা",
                                    fontSize = 10.sp,
                                    color = GoldAccent,
                                    fontFamily = SolaimanLipiFontFamily
                                )
                            }
                        }
                    }
                }

                // Action Icons (Pin, Edit, Delete)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onTogglePin,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (notice.isPinned) Icons.Default.PushPin else Icons.Outlined.PushPin,
                            contentDescription = "Pin Toggle",
                            tint = if (notice.isPinned) GoldAccent else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onEdit,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = TextWhite,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = RedDigital,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Notice Title
            Text(
                text = notice.title,
                style = AppTypography.cardTitle,
                fontSize = 15.sp,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Notice Summary
            Text(
                text = notice.summary,
                fontSize = 12.sp,
                color = TextMuted,
                lineHeight = 18.sp,
                fontFamily = SolaimanLipiFontFamily
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Author & Published Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "লেখক: ${notice.author}",
                    fontSize = 10.sp,
                    color = PrimaryGreen,
                    fontFamily = SolaimanLipiFontFamily
                )
                Text(
                    text = notice.publishedDate,
                    fontSize = 10.sp,
                    color = TextMuted,
                    fontFamily = SolaimanLipiFontFamily
                )
            }
        }
    }
}

@Composable
private fun NoticeFormDialog(
    initialNotice: NoticeItem?,
    onDismiss: () -> Unit,
    onSave: (title: String, summary: String, fullContent: String, category: NoticeCategory, isPinned: Boolean, author: String) -> Unit
) {
    var title by remember { mutableStateOf(initialNotice?.title ?: "") }
    var summary by remember { mutableStateOf(initialNotice?.summary ?: "") }
    var fullContent by remember { mutableStateOf(initialNotice?.fullContent ?: "") }
    var category by remember { mutableStateOf(initialNotice?.category ?: NoticeCategory.GENERAL) }
    var isPinned by remember { mutableStateOf(initialNotice?.isPinned ?: false) }
    var author by remember { mutableStateOf(initialNotice?.author ?: "মসজিদ পরিচালনা কমিটি") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialNotice == null) "নতুন বিজ্ঞপ্তি তৈরি করুন" else "বিজ্ঞপ্তি সম্পাদনা করুন",
                style = AppTypography.cardTitle,
                fontSize = 16.sp
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("বিজ্ঞপ্তির শিরোনাম", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Category Selector
                Text("বিভাগ নির্বাচন করুন:", fontSize = 11.sp, color = TextMuted)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(NoticeCategory.values().filter { it != NoticeCategory.ALL }) { cat ->
                        FilterChip(
                            selected = category == cat,
                            onClick = { category = cat },
                            label = { Text(cat.titleBn, fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryGreen,
                                selectedLabelColor = DarkBackground
                            )
                        )
                    }
                }

                OutlinedTextField(
                    value = summary,
                    onValueChange = { summary = it },
                    label = { Text("সংক্ষিপ্ত বিবরণ (১-২ লাইন)", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                OutlinedTextField(
                    value = fullContent,
                    onValueChange = { fullContent = it },
                    label = { Text("পূর্ণাঙ্গ বিস্তারিত নোটিশ", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 4,
                    maxLines = 8
                )

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("কর্তৃপক্ষ / লেখক", fontSize = 12.sp) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("শীর্ষে পিন করে রাখুন", fontSize = 12.sp, color = TextWhite, fontFamily = SolaimanLipiFontFamily)
                    Switch(
                        checked = isPinned,
                        onCheckedChange = { isPinned = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = GoldAccent,
                            checkedTrackColor = DarkSurface
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && summary.isNotBlank()) {
                        onSave(title, summary, fullContent.ifBlank { summary }, category, isPinned, author)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen),
                enabled = title.isNotBlank() && summary.isNotBlank()
            ) {
                Text(
                    text = if (initialNotice == null) "প্রকাশ করুন" else "আপডেট করুন",
                    color = DarkBackground,
                    fontFamily = SolaimanLipiFontFamily
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বাতিল", color = TextMuted, fontFamily = SolaimanLipiFontFamily)
            }
        },
        containerColor = DarkSurface
    )
}
