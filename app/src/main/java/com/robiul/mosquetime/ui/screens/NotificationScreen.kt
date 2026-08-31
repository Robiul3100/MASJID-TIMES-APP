package com.robiul.mosquetime.ui.screens

import android.content.Intent
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.robiul.mosquetime.data.model.AppNotification
import com.robiul.mosquetime.data.model.NoticeCategory
import com.robiul.mosquetime.data.model.NoticeItem
import com.robiul.mosquetime.data.model.NotificationCategory
import com.robiul.mosquetime.data.repository.MosqueRepository
import com.robiul.mosquetime.data.repository.UserPreferencesRepository
import com.robiul.mosquetime.ui.components.CommonHeader
import com.robiul.mosquetime.ui.theme.AppRadius
import com.robiul.mosquetime.ui.theme.AppSpacing
import com.robiul.mosquetime.ui.theme.AppTypography
import com.robiul.mosquetime.ui.theme.CyanBlue
import com.robiul.mosquetime.ui.theme.DarkBackground
import com.robiul.mosquetime.ui.theme.DarkGreen
import com.robiul.mosquetime.ui.theme.DarkGreenBorder
import com.robiul.mosquetime.ui.theme.DarkSurface
import com.robiul.mosquetime.ui.theme.DarkSurfaceBorder
import com.robiul.mosquetime.ui.theme.DarkSurfaceElevated
import com.robiul.mosquetime.ui.theme.GoldAccent
import com.robiul.mosquetime.ui.theme.NeonGreenGlow
import com.robiul.mosquetime.ui.theme.PrimaryGreen
import com.robiul.mosquetime.ui.theme.PurpleAccent
import com.robiul.mosquetime.ui.theme.RedDigital
import com.robiul.mosquetime.ui.theme.TextMuted
import com.robiul.mosquetime.ui.theme.TextWhite

/**
 * Unified Notifications & Mosque Notice Board Center.
 * Consolidates notifications and notice board into a seamless, high-performance tabbed interface.
 */
@Composable
fun NotificationScreen(
    onBackClick: () -> Unit,
    initialTab: Int = 0,
    onNavigateToRoute: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(initialTab) }
    val readNotificationIds by UserPreferencesRepository.readNotificationIds.collectAsState()

    val allNotifications: List<AppNotification> = remember { MosqueRepository.initialNotifications }
    val allNotices: List<NoticeItem> = remember { MosqueRepository.notices }

    // State for Filter / Search
    var selectedNotifCategory by remember { mutableStateOf(NotificationCategory.ALL) }
    var selectedNoticeCategory by remember { mutableStateOf(NoticeCategory.ALL) }
    var noticeSearchQuery by remember { mutableStateOf("") }

    // Dialog state
    var activeNoticeForDialog by remember { mutableStateOf<NoticeItem?>(null) }
    var showClearConfirmDialog by remember { mutableStateOf(false) }

    val unreadCount = remember(allNotifications, readNotificationIds) {
        allNotifications.count { notif -> !readNotificationIds.contains(notif.id) }
    }

    val filteredNotifications = remember(allNotifications, selectedNotifCategory, readNotificationIds) {
        allNotifications.filter { notif: AppNotification ->
            selectedNotifCategory == NotificationCategory.ALL || notif.category == selectedNotifCategory
        }
    }

    val filteredNotices = remember(allNotices, selectedNoticeCategory, noticeSearchQuery) {
        allNotices.filter { notice: NoticeItem ->
            val matchCat = selectedNoticeCategory == NoticeCategory.ALL || notice.category == selectedNoticeCategory
            val matchQuery = noticeSearchQuery.isBlank() ||
                    notice.title.contains(noticeSearchQuery, ignoreCase = true) ||
                    notice.summary.contains(noticeSearchQuery, ignoreCase = true) ||
                    notice.fullContent.contains(noticeSearchQuery, ignoreCase = true)
            matchCat && matchQuery
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = DarkBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(DarkBackground)
        ) {
            // Standard Common Header with Back Navigation & Action
            CommonHeader(
                title = if (selectedTabIndex == 0) "নোটিফিকেশন সেন্টার" else "মসজিদ নোটিশ বোর্ড",
                subtitle = if (selectedTabIndex == 0) "মসজিদের ওয়াক্ত, অনুষ্ঠান ও জরুরি আপডেট" else "অফিসিয়াল বিজ্ঞপ্তি ও জরুরি ঘোষণা",
                onBackClick = onBackClick,
                actionIcon = if (selectedTabIndex == 0 && unreadCount > 0) Icons.Default.DoneAll else null,
                onActionClick = {
                    if (selectedTabIndex == 0 && unreadCount > 0) {
                        showClearConfirmDialog = true
                    }
                }
            )

            // Segmented Tab Selector
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = DarkSurface,
                contentColor = PrimaryGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        height = 3.dp,
                        color = PrimaryGreen
                    )
                },
                divider = {
                    HorizontalDivider(color = DarkSurfaceBorder, thickness = 1.dp)
                }
            ) {
                // Tab 1: Notifications
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "বিজ্ঞপ্তি",
                                fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Medium,
                                fontSize = 14.sp,
                                color = if (selectedTabIndex == 0) PrimaryGreen else TextMuted
                            )
                            if (unreadCount > 0) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(RedDigital)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "$unreadCount",
                                        color = TextWhite,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                )

                // Tab 2: Notice Board
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Text(
                            text = "নোটিশ বোর্ড",
                            fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 14.sp,
                            color = if (selectedTabIndex == 1) PrimaryGreen else TextMuted
                        )
                    }
                )
            }

            // Tab Content
            if (selectedTabIndex == 0) {
                // TAB 1: NOTIFICATIONS
                NotificationTabContent(
                    filteredNotifications = filteredNotifications,
                    selectedCategory = selectedNotifCategory,
                    readNotificationIds = readNotificationIds,
                    onSelectCategory = { selectedNotifCategory = it },
                    onNotificationClick = { notif ->
                        UserPreferencesRepository.markNotificationAsRead(notif.id)
                        notif.targetRoute?.let { onNavigateToRoute(it) }
                    },
                    onMarkAllRead = {
                        allNotifications.forEach { UserPreferencesRepository.markNotificationAsRead(it.id) }
                    }
                )
            } else {
                // TAB 2: NOTICES BOARD
                NoticeBoardTabContent(
                    filteredNotices = filteredNotices,
                    selectedCategory = selectedNoticeCategory,
                    searchQuery = noticeSearchQuery,
                    onSelectCategory = { selectedNoticeCategory = it },
                    onSearchQueryChange = { noticeSearchQuery = it },
                    onNoticeClick = { notice ->
                        activeNoticeForDialog = notice
                    }
                )
            }
        }
    }

    // Notice Detail Modal Dialog
    activeNoticeForDialog?.let { notice ->
        NoticeDetailDialog(
            notice = notice,
            onDismiss = { activeNoticeForDialog = null },
            onShare = {
                val shareIntent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TITLE, notice.title)
                    putExtra(
                        Intent.EXTRA_TEXT,
                        "📢 ${notice.title}\n\n${notice.fullContent}\n\n— বায়তুল আমান জামে মসজিদ"
                    )
                    type = "text/plain"
                }
                context.startActivity(Intent.createChooser(shareIntent, "নোটিশ শেয়ার করুন"))
            }
        )
    }

    // Clear confirmation dialog
    if (showClearConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showClearConfirmDialog = false },
            title = {
                Text(
                    text = "সব নোটিফিকেশন পড়া হয়েছে হিসেবে চিহ্নিত করবেন?",
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            },
            text = {
                Text(
                    text = "আপনার সকল অপঠিত নোটিফিকেশন পঠিত হিসেবে সংরক্ষিত হবে।",
                    color = TextWhite,
                    fontSize = 13.5.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        allNotifications.forEach { UserPreferencesRepository.markNotificationAsRead(it.id) }
                        showClearConfirmDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
                ) {
                    Text("হ্যাঁ, সব চিহ্নিত করুন", color = DarkBackground, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmDialog = false }) {
                    Text("বাতিল", color = TextMuted)
                }
            },
            containerColor = DarkSurface,
            shape = RoundedCornerShape(AppRadius.lg)
        )
    }
}

@Composable
private fun NotificationTabContent(
    filteredNotifications: List<AppNotification>,
    selectedCategory: NotificationCategory,
    readNotificationIds: Set<String>,
    onSelectCategory: (NotificationCategory) -> Unit,
    onNotificationClick: (AppNotification) -> Unit,
    onMarkAllRead: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Horizontal Filter Categories
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = AppSpacing.sm, horizontal = AppSpacing.screenHorizontal),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            items(NotificationCategory.entries.toTypedArray()) { cat ->
                val isSelected = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) DarkGreen else DarkSurfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) PrimaryGreen else DarkSurfaceBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onSelectCategory(cat) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat.titleBn,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) NeonGreenGlow else TextMuted
                    )
                }
            }
        }

        if (filteredNotifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.NotificationsNone,
                        contentDescription = null,
                        tint = TextMuted.copy(alpha = 0.6f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.md))
                    Text(
                        text = "কোনো নোটিফিকেশন পাওয়া যায়নি",
                        style = AppTypography.cardTitle.copy(color = TextMuted)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = AppSpacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                items(filteredNotifications, key = { it.id }) { notif ->
                    val isRead = readNotificationIds.contains(notif.id)
                    NotificationItemCard(
                        notification = notif,
                        isRead = isRead,
                        onClick = { onNotificationClick(notif) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(AppSpacing.xl))
                }
            }
        }
    }
}

@Composable
private fun NoticeBoardTabContent(
    filteredNotices: List<NoticeItem>,
    selectedCategory: NoticeCategory,
    searchQuery: String,
    onSelectCategory: (NoticeCategory) -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onNoticeClick: (NoticeItem) -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // Search Bar for Notices
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.screenHorizontal, vertical = AppSpacing.sm)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("notice_search_input"),
                placeholder = {
                    Text(
                        text = "নোটিশ খুঁজুন...",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = PrimaryGreen,
                        modifier = Modifier.size(18.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = TextMuted,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(AppRadius.md),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = PrimaryGreen,
                    unfocusedBorderColor = DarkSurfaceBorder,
                    focusedContainerColor = DarkSurfaceElevated,
                    unfocusedContainerColor = DarkSurfaceElevated,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                )
            )
        }

        // Category Filter Row
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AppSpacing.screenHorizontal),
            horizontalArrangement = Arrangement.spacedBy(AppSpacing.xs)
        ) {
            items(NoticeCategory.entries.toTypedArray()) { cat ->
                val isSelected = cat == selectedCategory
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(if (isSelected) DarkGreen else DarkSurfaceElevated)
                        .border(
                            1.dp,
                            if (isSelected) PrimaryGreen else DarkSurfaceBorder,
                            RoundedCornerShape(20.dp)
                        )
                        .clickable { onSelectCategory(cat) }
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = cat.titleBn,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        color = if (isSelected) NeonGreenGlow else TextMuted
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(AppSpacing.sm))

        if (filteredNotices.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Outlined.Campaign,
                        contentDescription = null,
                        tint = TextMuted.copy(alpha = 0.6f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(AppSpacing.md))
                    Text(
                        text = "কোনো নোটিশ পাওয়া যায়নি",
                        style = AppTypography.cardTitle.copy(color = TextMuted)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = AppSpacing.screenHorizontal),
                verticalArrangement = Arrangement.spacedBy(AppSpacing.sm)
            ) {
                items(filteredNotices, key = { it.id }) { notice ->
                    NoticeItemCard(
                        notice = notice,
                        onClick = { onNoticeClick(notice) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(AppSpacing.xl))
                }
            }
        }
    }
}

@Composable
private fun NotificationItemCard(
    notification: AppNotification,
    isRead: Boolean,
    onClick: () -> Unit
) {
    val borderColor = if (!isRead) PrimaryGreen.copy(alpha = 0.8f) else DarkSurfaceBorder
    val iconTint = when (notification.category) {
        NotificationCategory.PRAYER -> PrimaryGreen
        NotificationCategory.JUMAH -> PrimaryGreen
        NotificationCategory.NOTICE -> CyanBlue
        NotificationCategory.EVENT -> PurpleAccent
        NotificationCategory.SPECIAL -> GoldAccent
        NotificationCategory.ALL -> PrimaryGreen
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppRadius.lg))
            .background(if (!isRead) DarkSurfaceElevated else DarkSurface)
            .border(1.dp, borderColor, RoundedCornerShape(AppRadius.lg))
            .clickable { onClick() }
            .padding(AppSpacing.cardPadding)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f))
                    .border(1.dp, iconTint.copy(alpha = 0.4f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (notification.category) {
                        NotificationCategory.PRAYER -> Icons.Outlined.Schedule
                        NotificationCategory.EVENT -> Icons.Outlined.Event
                        NotificationCategory.NOTICE, NotificationCategory.SPECIAL -> Icons.Outlined.Campaign
                        else -> Icons.Default.Notifications
                    },
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(AppSpacing.md))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.title,
                        style = AppTypography.cardTitle.copy(
                            color = if (!isRead) PrimaryGreen else TextWhite,
                            fontWeight = if (!isRead) FontWeight.Bold else FontWeight.SemiBold
                        )
                    )

                    if (!isRead) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(RedDigital, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = notification.message,
                    fontSize = 13.sp,
                    color = if (!isRead) TextWhite.copy(alpha = 0.9f) else TextMuted
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = notification.timeAgo.ifBlank { notification.timestamp },
                        fontSize = 11.sp,
                        color = TextMuted
                    )

                    if (notification.targetRoute != null) {
                        Text(
                            text = "বিস্তারিত দেখুন →",
                            fontSize = 11.sp,
                            color = CyanBlue,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun NoticeItemCard(
    notice: NoticeItem,
    onClick: () -> Unit
) {
    val borderColor = if (notice.isPinned) GoldAccent.copy(alpha = 0.7f) else DarkGreenBorder.copy(alpha = 0.5f)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppRadius.lg))
            .background(if (notice.isPinned) DarkSurfaceElevated else DarkSurface)
            .border(1.dp, borderColor, RoundedCornerShape(AppRadius.lg))
            .clickable { onClick() }
            .padding(AppSpacing.cardPadding)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Category Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(PrimaryGreen.copy(alpha = 0.15f))
                        .border(1.dp, PrimaryGreen.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = notice.category.titleBn,
                        fontSize = 11.sp,
                        color = PrimaryGreen,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (notice.isPinned) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.PushPin,
                            contentDescription = "Pinned",
                            tint = GoldAccent,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "পিন করা",
                            fontSize = 11.sp,
                            color = GoldAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(AppSpacing.sm))

            Text(
                text = notice.title,
                style = AppTypography.cardTitle.copy(
                    fontSize = 15.sp,
                    color = TextWhite,
                    fontWeight = FontWeight.Bold
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = notice.summary,
                fontSize = 13.sp,
                color = TextMuted,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(AppSpacing.sm))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "তারিখ: ${notice.publishedDate}",
                    fontSize = 11.sp,
                    color = TextMuted
                )

                Text(
                    text = "সম্পূর্ণ পড়ুন →",
                    fontSize = 11.sp,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun NoticeDetailDialog(
    notice: NoticeItem,
    onDismiss: () -> Unit,
    onShare: () -> Unit
) {
    val scrollState = rememberScrollState()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notice.category.titleBn,
                    fontSize = 13.sp,
                    color = PrimaryGreen,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextMuted
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = notice.title,
                    style = AppTypography.cardTitle.copy(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextWhite
                    )
                )

                Spacer(modifier = Modifier.height(AppSpacing.xs))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "প্রকাশক: ${notice.author}",
                        fontSize = 11.sp,
                        color = CyanBlue
                    )
                    Text(
                        text = notice.publishedDate,
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = AppSpacing.sm),
                    color = DarkSurfaceBorder
                )

                Text(
                    text = notice.fullContent,
                    fontSize = 14.sp,
                    color = TextWhite,
                    lineHeight = 22.sp
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onShare,
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryGreen)
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null,
                    tint = DarkBackground,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("শেয়ার করুন", color = DarkBackground, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("বন্ধ করুন", color = TextMuted)
            }
        },
        containerColor = DarkSurface,
        shape = RoundedCornerShape(AppRadius.lg)
    )
}
